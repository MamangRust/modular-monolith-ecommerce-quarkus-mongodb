package com.sanedge.category.repository.statsbyid;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.sanedge.category.domain.requests.FindMonthlyCategoryTotalPriceByIdRequest;
import com.sanedge.category.entity.CategoriesMonthlyTotalPrice;
import com.sanedge.category.entity.CategoriesYearlyTotalPrice;
import com.sanedge.category.entity.Category;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CategoryTotalPriceByIdRepository implements ReactivePanacheMongoRepository<Category> {

    private static final String[] MONTH_NAMES = { "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December" };

    private List<Bson> basePipeline(Date start, Date end, Bson categoryFilter) {
        List<Bson> stages = new ArrayList<>();

        List<Bson> categoryMatch = new ArrayList<>();
        categoryMatch.add(Filters.eq("deletedAt", null));
        if (categoryFilter != null) {
            categoryMatch.add(categoryFilter);
        }
        stages.add(Aggregates.match(Filters.and(categoryMatch)));

        stages.add(Aggregates.lookup("products", "_id", "categoryId", "product"));
        stages.add(Aggregates.unwind("$product"));
        stages.add(Aggregates.match(Filters.eq("product.deletedAt", null)));

        stages.add(Aggregates.lookup("order_items", "product._id", "productId", "item"));
        stages.add(Aggregates.unwind("$item"));
        stages.add(Aggregates.match(Filters.eq("item.deletedAt", null)));

        stages.add(Aggregates.lookup("orders", "item.orderId", "_id", "order"));
        stages.add(Aggregates.unwind("$order"));

        stages.add(Aggregates.match(Filters.and(
                Filters.eq("order.deletedAt", null),
                Filters.gte("order.createdAt", start),
                Filters.lt("order.createdAt", end))));
        return stages;
    }

    private Bson monthGroup() {
        Document groupId = new Document("year",
                new Document("$dateToString", new Document("format", "%Y").append("date", "$order.createdAt")))
                .append("monthIndex", new Document("$month", "$order.createdAt"));
        return Aggregates.group(groupId,
                Accumulators.sum("totalRevenue", "$order.totalPrice"));
    }

    private Bson yearGroup() {
        Document groupId = new Document("year",
                new Document("$dateToString", new Document("format", "%Y").append("date", "$order.createdAt")));
        return Aggregates.group(groupId,
                Accumulators.sum("totalRevenue", "$order.totalPrice"));
    }

    private Uni<List<Document>> runAggregate(List<Bson> pipeline) {
        return mongoCollection().aggregate(pipeline, Document.class).collect().asList();
    }

    private Uni<List<CategoriesMonthlyTotalPrice>> mapMonthly(List<Document> docs, int startYear, int startMonth,
            int endYear, int endMonth) {
        List<CategoriesMonthlyTotalPrice> list = new ArrayList<>();
        for (Document doc : docs) {
            Document id = doc.get("_id", Document.class);
            list.add(new CategoriesMonthlyTotalPrice(
                    id.getString("year"),
                    MONTH_NAMES[id.getInteger("monthIndex", 1) - 1],
                    ((Number) doc.getOrDefault("totalRevenue", 0)).longValue()));
        }
        ensureMonth(list, String.valueOf(startYear), MONTH_NAMES[startMonth - 1]);
        if (endYear != startYear || endMonth != startMonth) {
            ensureMonth(list, String.valueOf(endYear), MONTH_NAMES[endMonth - 1]);
        }
        list.sort((a, b) -> {
            int byYear = b.getYear().compareTo(a.getYear());
            return byYear != 0 ? byYear : monthOrder(b.getMonth()) - monthOrder(a.getMonth());
        });
        return Uni.createFrom().item(list);
    }

    private void ensureMonth(List<CategoriesMonthlyTotalPrice> list, String year, String monthName) {
        boolean exists = list.stream().anyMatch(r -> year.equals(r.getYear()) && monthName.equals(r.getMonth()));
        if (!exists) {
            list.add(new CategoriesMonthlyTotalPrice(year, monthName, 0L));
        }
    }

    private int monthOrder(String monthName) {
        for (int i = 0; i < MONTH_NAMES.length; i++) {
            if (MONTH_NAMES[i].equals(monthName)) {
                return i + 1;
            }
        }
        return 0;
    }

    private Uni<List<CategoriesYearlyTotalPrice>> mapYearly(List<Document> docs, int year, int yearMinusOne) {
        List<CategoriesYearlyTotalPrice> list = new ArrayList<>();
        for (Document doc : docs) {
            Document id = doc.get("_id", Document.class);
            list.add(new CategoriesYearlyTotalPrice(
                    id.getString("year"),
                    ((Number) doc.getOrDefault("totalRevenue", 0)).longValue()));
        }
        ensureYear(list, String.valueOf(year));
        if (yearMinusOne != year) {
            ensureYear(list, String.valueOf(yearMinusOne));
        }
        list.sort((a, b) -> b.getYear().compareTo(a.getYear()));
        return Uni.createFrom().item(list);
    }

    private void ensureYear(List<CategoriesYearlyTotalPrice> list, String year) {
        boolean exists = list.stream().anyMatch(r -> year.equals(r.getYear()));
        if (!exists) {
            list.add(new CategoriesYearlyTotalPrice(year, 0L));
        }
    }

    public Uni<List<CategoriesMonthlyTotalPrice>> findMonthlyTotalPriceByCategory(
            FindMonthlyCategoryTotalPriceByIdRequest req) {
        int year = req.getYear();
        int month = req.getMonth();
        LocalDate currentMonth = LocalDate.of(year, month, 1);
        LocalDate nextMonth = currentMonth.plusMonths(1);
        int startYear = year;
        int startMonth = month;
        int endYear = nextMonth.getYear();
        int endMonth = nextMonth.getMonthValue();
        LocalDate endExclusive = currentMonth.plusMonths(2);

        Date start = Date.from(Instant.parse(
                String.format("%04d-%02d-01T00:00:00Z", startYear, startMonth)));
        Date end = Date.from(Instant.parse(
                String.format("%04d-%02d-01T00:00:00Z", endExclusive.getYear(), endExclusive.getMonthValue())));

        List<Bson> pipeline = basePipeline(start, end, Filters.eq("_id", new ObjectId(req.getCategoryId())));
        pipeline.add(monthGroup());

        return runAggregate(pipeline)
                .chain(docs -> mapMonthly(docs, startYear, startMonth, endYear, endMonth));
    }

    public Uni<List<CategoriesYearlyTotalPrice>> findYearlyTotalPriceByCategory(String categoryId, Integer year) {
        int yearMinusOne = year - 1;
        Date start = Date.from(Instant.parse(yearMinusOne + "-01-01T00:00:00Z"));
        Date end = Date.from(Instant.parse((year + 1) + "-01-01T00:00:00Z"));

        List<Bson> pipeline = basePipeline(start, end, Filters.eq("_id", new ObjectId(categoryId)));
        pipeline.add(yearGroup());

        return runAggregate(pipeline)
                .chain(docs -> mapYearly(docs, year, yearMinusOne));
    }
}
