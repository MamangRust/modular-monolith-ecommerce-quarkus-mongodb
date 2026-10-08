package com.sanedge.order.repository.stats;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.sanedge.order.entity.Order;
import com.sanedge.order.entity.OrderMonthlyTotalRevenue;
import com.sanedge.order.entity.OrderYearlyTotalRevenue;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderTotalRevenueRepository implements ReactivePanacheMongoRepository<Order> {

    private static final String[] MONTH_NAMES = { "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December" };

    private static Date dateOf(int year, int month) {
        return Date.from(Instant.parse(String.format("%04d-%02d-01T00:00:00Z", year, month)));
    }

    private List<Bson> basePipeline(Date start, Date end, Bson orderFilter) {
        List<Bson> matchFilters = new ArrayList<>();
        matchFilters.add(Filters.eq("deletedAt", null));
        matchFilters.add(Filters.gte("createdAt", start));
        matchFilters.add(Filters.lt("createdAt", end));
        if (orderFilter != null) {
            matchFilters.add(orderFilter);
        }

        List<Bson> stages = new ArrayList<>();
        stages.add(Aggregates.match(Filters.and(matchFilters)));
        stages.add(Aggregates.lookup("order_items", "_id", "orderId", "items"));
        stages.add(Aggregates.unwind("$items"));
        stages.add(Aggregates.match(Filters.eq("items.deletedAt", null)));
        return stages;
    }

    private Uni<List<Document>> runAggregate(List<Bson> pipeline) {
        return mongoCollection().aggregate(pipeline, Document.class).collect().asList();
    }

    public Uni<List<OrderMonthlyTotalRevenue>> findMonthlyTotalRevenue(Integer year, Integer month) {
        int nextYear = month == 12 ? year + 1 : year;
        int nextMonth = month == 12 ? 1 : month + 1;

        List<Bson> pipeline = basePipeline(dateOf(year, month), dateOf(nextYear, nextMonth), null);
        Document groupId = new Document("year",
                new Document("$dateToString", new Document("format", "%Y").append("date", "$createdAt")))
                .append("monthIndex", new Document("$month", "$createdAt"));
        pipeline.add(Aggregates.group(groupId, Accumulators.sum("totalRevenue", "$totalPrice")));

        return runAggregate(pipeline).map(docs -> {
            Map<Integer, OrderMonthlyTotalRevenue> byIndex = new LinkedHashMap<>();
            for (Document doc : docs) {
                Document id = doc.get("_id", Document.class);
                int y = Integer.parseInt(id.getString("year"));
                int m = id.getInteger("monthIndex", 1);
                byIndex.put(y * 100 + m, new OrderMonthlyTotalRevenue(
                        id.getString("year"),
                        MONTH_NAMES[m - 1],
                        ((Number) doc.getOrDefault("totalRevenue", 0)).longValue()));
            }
            for (int[] ym : new int[][] { { year, month }, { nextYear, nextMonth } }) {
                byIndex.computeIfAbsent(ym[0] * 100 + ym[1],
                        i -> new OrderMonthlyTotalRevenue(String.valueOf(ym[0]), MONTH_NAMES[ym[1] - 1], 0L));
            }
            List<Integer> sorted = new ArrayList<>(byIndex.keySet());
            sorted.sort((a, b) -> Integer.compare(b, a));
            List<OrderMonthlyTotalRevenue> result = new ArrayList<>();
            for (Integer index : sorted) {
                result.add(byIndex.get(index));
            }
            return result;
        });
    }

    public Uni<List<OrderYearlyTotalRevenue>> findYearlyTotalRevenue(Integer year) {
        List<Bson> pipeline = basePipeline(dateOf(year - 1, 1), dateOf(year + 1, 1), null);
        Document groupId = new Document("year",
                new Document("$dateToString", new Document("format", "%Y").append("date", "$createdAt")));
        pipeline.add(Aggregates.group(groupId, Accumulators.sum("totalRevenue", "$totalPrice")));

        return runAggregate(pipeline).map(docs -> {
            Map<Integer, OrderYearlyTotalRevenue> byYear = new LinkedHashMap<>();
            for (Document doc : docs) {
                Document id = doc.get("_id", Document.class);
                int y = Integer.parseInt(id.getString("year"));
                byYear.put(y, new OrderYearlyTotalRevenue(id.getString("year"),
                        ((Number) doc.getOrDefault("totalRevenue", 0)).longValue()));
            }
            for (int y : new int[] { year, year - 1 }) {
                byYear.computeIfAbsent(y, i -> new OrderYearlyTotalRevenue(String.valueOf(i), 0L));
            }
            List<Integer> sorted = new ArrayList<>(byYear.keySet());
            sorted.sort((a, b) -> Integer.compare(b, a));
            List<OrderYearlyTotalRevenue> result = new ArrayList<>();
            for (Integer y : sorted) {
                result.add(byYear.get(y));
            }
            return result;
        });
    }
}
