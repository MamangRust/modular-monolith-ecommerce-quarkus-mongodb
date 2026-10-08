package com.sanedge.category.repository.statsbymerchant;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.sanedge.category.entity.CategoriesMonthPrice;
import com.sanedge.category.entity.CategoriesYearPrice;
import com.sanedge.category.entity.Category;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CategoryPriceByMerchantRepository implements ReactivePanacheMongoRepository<Category> {

    private List<Bson> basePipeline(Date start, Date end, Bson orderFilter) {
        List<Bson> stages = new ArrayList<>();

        stages.add(Aggregates.match(Filters.eq("deletedAt", null)));

        stages.add(Aggregates.lookup("products", "_id", "categoryId", "product"));
        stages.add(Aggregates.unwind("$product"));
        stages.add(Aggregates.match(Filters.eq("product.deletedAt", null)));

        stages.add(Aggregates.lookup("order_items", "product._id", "productId", "item"));
        stages.add(Aggregates.unwind("$item"));
        stages.add(Aggregates.match(Filters.eq("item.deletedAt", null)));

        stages.add(Aggregates.lookup("orders", "item.orderId", "_id", "order"));
        stages.add(Aggregates.unwind("$order"));

        List<Bson> orderMatch = new ArrayList<>();
        orderMatch.add(Filters.eq("order.deletedAt", null));
        orderMatch.add(Filters.gte("order.createdAt", start));
        orderMatch.add(Filters.lt("order.createdAt", end));
        if (orderFilter != null) {
            orderMatch.add(orderFilter);
        }
        stages.add(Aggregates.match(Filters.and(orderMatch)));
        return stages;
    }

    private Bson monthGroup() {
        Document groupId = new Document("month",
                new Document("$dateToString", new Document("format", "%b").append("date", "$order.createdAt")))
                .append("monthIndex", new Document("$month", "$order.createdAt"))
                .append("categoryId", "$_id")
                .append("categoryName", "$name");
        return Aggregates.group(groupId,
                Accumulators.addToSet("orderIds", "$order._id"),
                Accumulators.sum("itemsSold", "$item.quantity"),
                Accumulators.sum("totalRevenue", "$order.totalPrice"));
    }

    private Bson yearGroup() {
        Document groupId = new Document("year",
                new Document("$dateToString", new Document("format", "%Y").append("date", "$order.createdAt")))
                .append("categoryId", "$_id")
                .append("categoryName", "$name");
        return Aggregates.group(groupId,
                Accumulators.addToSet("orderIds", "$order._id"),
                Accumulators.sum("itemsSold", "$item.quantity"),
                Accumulators.sum("totalRevenue", "$order.totalPrice"),
                Accumulators.addToSet("productIds", "$item.productId"));
    }

    private Uni<List<Document>> runAggregate(List<Bson> pipeline) {
        return mongoCollection().aggregate(pipeline, Document.class).collect().asList();
    }

    public Uni<List<CategoriesMonthPrice>> findMonthlyCategoryStatsByMerchant(String merchantId, Integer year) {
        List<Bson> pipeline = basePipeline(
                Date.from(Instant.parse(year + "-01-01T00:00:00Z")),
                Date.from(Instant.parse((year + 1) + "-01-01T00:00:00Z")),
                Filters.eq("order.merchantId", new ObjectId(merchantId)));
        pipeline.add(monthGroup());
        pipeline.add(new Document("$sort", new Document("_id.monthIndex", 1).append("totalRevenue", -1)));

        return runAggregate(pipeline).map(docs -> {
            List<CategoriesMonthPrice> list = new ArrayList<>();
            for (Document doc : docs) {
                Document id = doc.get("_id", Document.class);
                ObjectId category = id.get("categoryId", ObjectId.class);
                list.add(new CategoriesMonthPrice(
                        id.getString("month"),
                        category != null ? category.toHexString() : null,
                        id.getString("categoryName"),
                        countList(doc, "orderIds"),
                        ((Number) doc.getOrDefault("itemsSold", 0)).longValue(),
                        ((Number) doc.getOrDefault("totalRevenue", 0)).longValue()));
            }
            return list;
        });
    }

    public Uni<List<CategoriesYearPrice>> findYearlyCategoryStatsByMerchant(String merchantId, Integer year) {
        List<Bson> pipeline = basePipeline(
                Date.from(Instant.parse((year - 4) + "-01-01T00:00:00Z")),
                Date.from(Instant.parse((year + 1) + "-01-01T00:00:00Z")),
                Filters.eq("order.merchantId", new ObjectId(merchantId)));
        pipeline.add(yearGroup());
        pipeline.add(new Document("$sort", new Document("_id.year", 1).append("totalRevenue", -1)));

        return runAggregate(pipeline).map(docs -> {
            List<CategoriesYearPrice> list = new ArrayList<>();
            for (Document doc : docs) {
                Document id = doc.get("_id", Document.class);
                ObjectId category = id.get("categoryId", ObjectId.class);
                list.add(new CategoriesYearPrice(
                        id.getString("year"),
                        category != null ? category.toHexString() : null,
                        id.getString("categoryName"),
                        countList(doc, "orderIds"),
                        ((Number) doc.getOrDefault("itemsSold", 0)).longValue(),
                        ((Number) doc.getOrDefault("totalRevenue", 0)).longValue(),
                        countList(doc, "productIds")));
            }
            return list;
        });
    }

    private long countList(Document doc, String field) {
        List<?> values = doc.getList(field, ObjectId.class);
        return values != null ? values.size() : 0L;
    }
}
