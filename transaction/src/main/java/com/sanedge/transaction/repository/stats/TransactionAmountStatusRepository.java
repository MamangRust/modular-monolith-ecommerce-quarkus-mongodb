package com.sanedge.transaction.repository.stats;

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
import com.sanedge.transaction.entity.Transaction;
import com.sanedge.transaction.entity.TransactionMonthlyAmountFailed;
import com.sanedge.transaction.entity.TransactionMonthlyAmountSuccess;
import com.sanedge.transaction.entity.TransactionYearlyAmountFailed;
import com.sanedge.transaction.entity.TransactionYearlyAmountSuccess;
import com.sanedge.transaction.enums.PaymentStatus;
import com.sanedge.transaction.repository.StatsSupport;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * MongoDB aggregation counterpart of the former PostgreSQL native
 * transaction-amount statistics queries. The SQL {@code WITH monthly_data ...
 * GROUP BY EXTRACT(YEAR/MONTH ...)} CTEs are translated to a {@code $match} +
 * {@code $group} pipeline; the placeholder rows for months with no data and the
 * descending year/month ordering are produced in Java.
 */
@ApplicationScoped
public class TransactionAmountStatusRepository implements ReactivePanacheMongoRepository<Transaction> {

    private Uni<List<Document>> runAggregate(List<Bson> pipeline) {
        return mongoCollection().aggregate(pipeline, Document.class).collect().asList();
    }

    private static String firstDayOfMonth(int year, int month) {
        return String.format("%04d-%02d-01T00:00:00Z", year, month);
    }

    private static String firstDayOfNextMonth(int year, int month) {
        int nextYear = month == 12 ? year + 1 : year;
        int nextMonth = month == 12 ? 1 : month + 1;
        return firstDayOfMonth(nextYear, nextMonth);
    }

    private Uni<List<Document>> monthlyAggregate(int year, int month, int prevYear, int prevMonth,
            PaymentStatus status, ObjectId merchantId) {
        Date start1 = Date.from(Instant.parse(firstDayOfMonth(year, month)));
        Date end1 = Date.from(Instant.parse(firstDayOfNextMonth(year, month)));
        Date start2 = Date.from(Instant.parse(firstDayOfMonth(prevYear, prevMonth)));
        Date end2 = Date.from(Instant.parse(firstDayOfNextMonth(prevYear, prevMonth)));

        List<Bson> conditions = new ArrayList<>();
        conditions.add(Filters.eq("deletedAt", null));
        conditions.add(Filters.eq("status", status.name()));
        if (merchantId != null) {
            conditions.add(Filters.eq("merchantId", merchantId));
        }
        conditions.add(Filters.or(
                Filters.and(Filters.gte("createdAt", start1), Filters.lt("createdAt", end1)),
                Filters.and(Filters.gte("createdAt", start2), Filters.lt("createdAt", end2))));

        List<Bson> pipeline = new ArrayList<>();
        pipeline.add(Aggregates.match(Filters.and(conditions)));
        pipeline.add(Aggregates.group(
                new Document("year",
                        new Document("$dateToString", new Document("format", "%Y").append("date", "$createdAt")))
                        .append("monthIndex", new Document("$month", "$createdAt")),
                Accumulators.sum("totalCount", 1),
                Accumulators.sum("totalAmount", "$amount")));
        return runAggregate(pipeline);
    }

    private Uni<List<Document>> yearlyAggregate(int year, PaymentStatus status, ObjectId merchantId) {
        Date start = Date.from(Instant.parse(firstDayOfMonth(year - 1, 1)));
        Date end = Date.from(Instant.parse(firstDayOfMonth(year + 1, 1)));

        List<Bson> conditions = new ArrayList<>();
        conditions.add(Filters.eq("deletedAt", null));
        conditions.add(Filters.eq("status", status.name()));
        if (merchantId != null) {
            conditions.add(Filters.eq("merchantId", merchantId));
        }
        conditions.add(Filters.gte("createdAt", start));
        conditions.add(Filters.lt("createdAt", end));

        List<Bson> pipeline = new ArrayList<>();
        pipeline.add(Aggregates.match(Filters.and(conditions)));
        pipeline.add(Aggregates.group(
                new Document("year",
                        new Document("$dateToString", new Document("format", "%Y").append("date", "$createdAt"))),
                Accumulators.sum("totalCount", 1),
                Accumulators.sum("totalAmount", "$amount")));
        return runAggregate(pipeline);
    }

    private static void ensureMonthSuccess(List<TransactionMonthlyAmountSuccess> list, String year, int month) {
        String abbr = StatsSupport.monthAbbr(month);
        boolean exists = list.stream().anyMatch(r -> year.equals(r.getYear()) && abbr.equals(r.getMonth()));
        if (!exists) {
            list.add(new TransactionMonthlyAmountSuccess(year, abbr, 0L, 0L));
        }
    }

    private static void ensureMonthFailed(List<TransactionMonthlyAmountFailed> list, String year, int month) {
        String abbr = StatsSupport.monthAbbr(month);
        boolean exists = list.stream().anyMatch(r -> year.equals(r.getYear()) && abbr.equals(r.getMonth()));
        if (!exists) {
            list.add(new TransactionMonthlyAmountFailed(year, abbr, 0L, 0L));
        }
    }

    private static void ensureYearSuccess(List<TransactionYearlyAmountSuccess> list, String year) {
        boolean exists = list.stream().anyMatch(r -> year.equals(r.getYear()));
        if (!exists) {
            list.add(new TransactionYearlyAmountSuccess(year, 0L, 0L));
        }
    }

    private static void ensureYearFailed(List<TransactionYearlyAmountFailed> list, String year) {
        boolean exists = list.stream().anyMatch(r -> year.equals(r.getYear()));
        if (!exists) {
            list.add(new TransactionYearlyAmountFailed(year, 0L, 0L));
        }
    }

    private static void sortMonthlySuccess(List<TransactionMonthlyAmountSuccess> list) {
        list.sort((a, b) -> {
            int byYear = b.getYear().compareTo(a.getYear());
            if (byYear != 0) {
                return byYear;
            }
            return Integer.compare(StatsSupport.monthIndex(b.getMonth()), StatsSupport.monthIndex(a.getMonth()));
        });
    }

    private static void sortMonthlyFailed(List<TransactionMonthlyAmountFailed> list) {
        list.sort((a, b) -> {
            int byYear = b.getYear().compareTo(a.getYear());
            if (byYear != 0) {
                return byYear;
            }
            return Integer.compare(StatsSupport.monthIndex(b.getMonth()), StatsSupport.monthIndex(a.getMonth()));
        });
    }

    private static long longValue(Document doc, String key) {
        return ((Number) doc.getOrDefault(key, 0)).longValue();
    }

    public Uni<List<TransactionMonthlyAmountSuccess>> findMonthlyTransactionSuccess(Integer year, Integer month) {
        LocalDate currentMonth = LocalDate.of(year, month, 1);
        LocalDate prev = currentMonth.minusMonths(1);

        return monthlyAggregate(year, month, prev.getYear(), prev.getMonthValue(), PaymentStatus.SUCCESS, null)
                .map(docs -> {
                    List<TransactionMonthlyAmountSuccess> list = new ArrayList<>();
                    for (Document doc : docs) {
                        Document id = doc.get("_id", Document.class);
                        list.add(new TransactionMonthlyAmountSuccess(
                                id.getString("year"),
                                StatsSupport.monthAbbr(id.getInteger("monthIndex")),
                                longValue(doc, "totalCount"),
                                longValue(doc, "totalAmount")));
                    }
                    ensureMonthSuccess(list, String.valueOf(year), month);
                    ensureMonthSuccess(list, String.valueOf(prev.getYear()), prev.getMonthValue());
                    sortMonthlySuccess(list);
                    return list;
                });
    }

    public Uni<List<TransactionYearlyAmountSuccess>> findYearlyTransactionSuccess(Integer year) {
        return yearlyAggregate(year, PaymentStatus.SUCCESS, null)
                .map(docs -> {
                    List<TransactionYearlyAmountSuccess> list = new ArrayList<>();
                    for (Document doc : docs) {
                        Document id = doc.get("_id", Document.class);
                        list.add(new TransactionYearlyAmountSuccess(
                                id.getString("year"),
                                longValue(doc, "totalCount"),
                                longValue(doc, "totalAmount")));
                    }
                    ensureYearSuccess(list, String.valueOf(year));
                    ensureYearSuccess(list, String.valueOf(year - 1));
                    list.sort((a, b) -> b.getYear().compareTo(a.getYear()));
                    return list;
                });
    }

    public Uni<List<TransactionMonthlyAmountFailed>> findMonthlyTransactionFailed(Integer year, Integer month) {
        LocalDate currentMonth = LocalDate.of(year, month, 1);
        LocalDate prev = currentMonth.minusMonths(1);

        return monthlyAggregate(year, month, prev.getYear(), prev.getMonthValue(), PaymentStatus.FAILED, null)
                .map(docs -> {
                    List<TransactionMonthlyAmountFailed> list = new ArrayList<>();
                    for (Document doc : docs) {
                        Document id = doc.get("_id", Document.class);
                        list.add(new TransactionMonthlyAmountFailed(
                                id.getString("year"),
                                StatsSupport.monthAbbr(id.getInteger("monthIndex")),
                                longValue(doc, "totalCount"),
                                longValue(doc, "totalAmount")));
                    }
                    ensureMonthFailed(list, String.valueOf(year), month);
                    ensureMonthFailed(list, String.valueOf(prev.getYear()), prev.getMonthValue());
                    sortMonthlyFailed(list);
                    return list;
                });
    }

    public Uni<List<TransactionYearlyAmountFailed>> findYearlyTransactionFailed(Integer year) {
        return yearlyAggregate(year, PaymentStatus.FAILED, null)
                .map(docs -> {
                    List<TransactionYearlyAmountFailed> list = new ArrayList<>();
                    for (Document doc : docs) {
                        Document id = doc.get("_id", Document.class);
                        list.add(new TransactionYearlyAmountFailed(
                                id.getString("year"),
                                longValue(doc, "totalCount"),
                                longValue(doc, "totalAmount")));
                    }
                    ensureYearFailed(list, String.valueOf(year));
                    ensureYearFailed(list, String.valueOf(year - 1));
                    list.sort((a, b) -> b.getYear().compareTo(a.getYear()));
                    return list;
                });
    }
}
