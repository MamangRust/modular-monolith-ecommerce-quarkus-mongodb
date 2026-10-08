package com.sanedge.transaction.repository.statsbymerchant;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.sanedge.transaction.domain.requests.FindMonthlyMethodsByMerchantRequest;
import com.sanedge.transaction.entity.Transaction;
import com.sanedge.transaction.entity.TransactionMonthlyMethod;
import com.sanedge.transaction.entity.TransactionYearlyMethod;
import com.sanedge.transaction.enums.PaymentStatus;
import com.sanedge.transaction.repository.StatsSupport;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * MongoDB aggregation counterpart of the former PostgreSQL native
 * merchant-scoped transaction-method statistics queries (the
 * {@code merchant_id = ?} predicate is added to both the distinct-methods CTE
 * and the per-month/per-year aggregation).
 */
@ApplicationScoped
public class TransactionMethodByMerchantRepository implements ReactivePanacheMongoRepository<Transaction> {

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

    private Uni<List<String>> distinctPaymentMethods(ObjectId merchantId) {
        List<Bson> pipeline = new ArrayList<>();
        pipeline.add(Aggregates.match(Filters.and(
                Filters.eq("deletedAt", null),
                Filters.eq("merchantId", merchantId))));
        pipeline.add(Aggregates.group("$paymentMethod"));
        pipeline.add(Aggregates.sort(Sorts.ascending("_id")));
        return runAggregate(pipeline).map(docs -> {
            List<String> list = new ArrayList<>();
            for (Document doc : docs) {
                String method = doc.getString("_id");
                if (method != null) {
                    list.add(method);
                }
            }
            return list;
        });
    }

    private Uni<List<Document>> monthlyTransactions(Date start1, Date end1, Date start2, Date end2,
            PaymentStatus status, ObjectId merchantId) {
        List<Bson> pipeline = new ArrayList<>();
        pipeline.add(Aggregates.match(Filters.and(
                Filters.eq("deletedAt", null),
                Filters.eq("status", status.name()),
                Filters.eq("merchantId", merchantId),
                Filters.or(
                        Filters.and(Filters.gte("createdAt", start1), Filters.lt("createdAt", end1)),
                        Filters.and(Filters.gte("createdAt", start2), Filters.lt("createdAt", end2))))));
        pipeline.add(Aggregates.group(
                new Document("year",
                        new Document("$dateToString", new Document("format", "%Y").append("date", "$createdAt")))
                        .append("monthIndex", new Document("$month", "$createdAt"))
                        .append("paymentMethod", "$paymentMethod"),
                Accumulators.sum("totalTransactions", 1),
                Accumulators.sum("totalAmount", "$amount")));
        return runAggregate(pipeline);
    }

    private Uni<List<Document>> yearlyTransactions(int startYear, int endYear, PaymentStatus status,
            ObjectId merchantId) {
        Date start = Date.from(Instant.parse(firstDayOfMonth(startYear, 1)));
        Date end = Date.from(Instant.parse(firstDayOfMonth(endYear + 1, 1)));

        List<Bson> pipeline = new ArrayList<>();
        pipeline.add(Aggregates.match(Filters.and(
                Filters.eq("deletedAt", null),
                Filters.eq("status", status.name()),
                Filters.eq("merchantId", merchantId),
                Filters.gte("createdAt", start),
                Filters.lt("createdAt", end))));
        pipeline.add(Aggregates.group(
                new Document("year",
                        new Document("$dateToString", new Document("format", "%Y").append("date", "$createdAt")))
                        .append("paymentMethod", "$paymentMethod"),
                Accumulators.sum("totalTransactions", 1),
                Accumulators.sum("totalAmount", "$amount")));
        return runAggregate(pipeline);
    }

    private static String key(String year, int monthIndex, String method) {
        return year + "|" + monthIndex + "|" + method;
    }

    private List<TransactionMonthlyMethod> buildMonthly(List<String> methods, List<Document> docs,
            YearMonth from, YearMonth to) {
        Map<String, Document> index = new HashMap<>();
        for (Document doc : docs) {
            Document id = doc.get("_id", Document.class);
            index.put(key(id.getString("year"), id.getInteger("monthIndex"), id.getString("paymentMethod")), doc);
        }

        List<TransactionMonthlyMethod> list = new ArrayList<>();
        YearMonth cursor = from;
        while (!cursor.isAfter(to)) {
            String year = String.valueOf(cursor.getYear());
            int month = cursor.getMonthValue();
            for (String method : methods) {
                Document doc = index.get(key(year, month, method));
                long total = doc != null ? ((Number) doc.getOrDefault("totalTransactions", 0)).longValue() : 0L;
                long amount = doc != null ? ((Number) doc.getOrDefault("totalAmount", 0)).longValue() : 0L;
                list.add(new TransactionMonthlyMethod(StatsSupport.monthAbbr(month), method, total, amount));
            }
            cursor = cursor.plusMonths(1);
        }
        return list;
    }

    private List<TransactionYearlyMethod> buildYearly(List<String> methods, List<Document> docs,
            int startYear, int endYear) {
        Map<String, Document> index = new HashMap<>();
        for (Document doc : docs) {
            Document id = doc.get("_id", Document.class);
            index.put(id.getString("year") + "|" + id.getString("paymentMethod"), doc);
        }

        List<TransactionYearlyMethod> list = new ArrayList<>();
        for (int year = startYear; year <= endYear; year++) {
            String yearKey = String.valueOf(year);
            for (String method : methods) {
                Document doc = index.get(yearKey + "|" + method);
                long total = doc != null ? ((Number) doc.getOrDefault("totalTransactions", 0)).longValue() : 0L;
                long amount = doc != null ? ((Number) doc.getOrDefault("totalAmount", 0)).longValue() : 0L;
                list.add(new TransactionYearlyMethod(yearKey, method, total, amount));
            }
        }
        return list;
    }

    private Uni<List<TransactionMonthlyMethod>> monthlyMethods(int year, int month, PaymentStatus status,
            ObjectId merchantId) {
        LocalDate current = LocalDate.of(year, month, 1);
        LocalDate prev = current.minusMonths(1);
        int year2 = prev.getYear();
        int month2 = prev.getMonthValue();

        YearMonth a = YearMonth.of(year, month);
        YearMonth b = YearMonth.of(year2, month2);
        YearMonth from = a.isBefore(b) ? a : b;
        YearMonth to = a.isAfter(b) ? a : b;

        Date start1 = Date.from(Instant.parse(firstDayOfMonth(year, month)));
        Date end1 = Date.from(Instant.parse(firstDayOfNextMonth(year, month)));
        Date start2 = Date.from(Instant.parse(firstDayOfMonth(year2, month2)));
        Date end2 = Date.from(Instant.parse(firstDayOfNextMonth(year2, month2)));

        return Uni.combine().all()
                .unis(distinctPaymentMethods(merchantId),
                        monthlyTransactions(start1, end1, start2, end2, status, merchantId))
                .asTuple()
                .map(tuple -> buildMonthly(tuple.getItem1(), tuple.getItem2(), from, to));
    }

    private Uni<List<TransactionYearlyMethod>> yearlyMethods(int year, PaymentStatus status, ObjectId merchantId) {
        int startYear = year - 1;
        int endYear = year;
        return Uni.combine().all()
                .unis(distinctPaymentMethods(merchantId),
                        yearlyTransactions(startYear, endYear, status, merchantId))
                .asTuple()
                .map(tuple -> buildYearly(tuple.getItem1(), tuple.getItem2(), startYear, endYear));
    }

    public Uni<List<TransactionMonthlyMethod>> findMonthlyTransactionMethodsSuccessByMerchant(
            FindMonthlyMethodsByMerchantRequest req) {
        return monthlyMethods(req.getYear(), req.getMonth(), PaymentStatus.SUCCESS,
                new ObjectId(req.getMerchantId()));
    }

    public Uni<List<TransactionMonthlyMethod>> findMonthlyTransactionMethodsFailedByMerchant(
            FindMonthlyMethodsByMerchantRequest req) {
        return monthlyMethods(req.getYear(), req.getMonth(), PaymentStatus.FAILED,
                new ObjectId(req.getMerchantId()));
    }

    public Uni<List<TransactionYearlyMethod>> findYearlyTransactionMethodsSuccessByMerchant(String merchantId,
            Integer year) {
        return yearlyMethods(year, PaymentStatus.SUCCESS, new ObjectId(merchantId));
    }

    public Uni<List<TransactionYearlyMethod>> findYearlyTransactionMethodsFailedByMerchant(String merchantId,
            Integer year) {
        return yearlyMethods(year, PaymentStatus.FAILED, new ObjectId(merchantId));
    }
}
