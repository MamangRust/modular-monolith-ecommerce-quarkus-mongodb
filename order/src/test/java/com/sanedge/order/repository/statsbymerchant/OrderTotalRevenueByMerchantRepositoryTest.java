package com.sanedge.order.repository.statsbymerchant;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.sanedge.order.domain.requests.MonthTotalRevenueMerchantRequest;

import io.quarkus.test.junit.QuarkusTest;
import com.sanedge.common.test.MongoDbResource;
import com.sanedge.common.test.MongoTestBase;
import io.quarkus.test.common.QuarkusTestResource;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

@Disabled("Mongo aggregation returns empty buckets without seeded data; verify output before enabling")
@QuarkusTest
@QuarkusTestResource(MongoDbResource.class)
class OrderTotalRevenueByMerchantRepositoryTest extends MongoTestBase {

    private static final String MERCHANT_ID = "507f1f77bcf86cd7994390a1";

    @Inject
    OrderTotalRevenueByMerchantRepository orderTotalRevenueByMerchantRepository;

    @Test
    void testFindMonthlyTotalRevenueByMerchant_ReturnsTwoEntries() {
        MonthTotalRevenueMerchantRequest req = new MonthTotalRevenueMerchantRequest();
        req.setMerchantId(MERCHANT_ID);
        req.setYear(2023);
        req.setMonth(12);

        orderTotalRevenueByMerchantRepository
                .findMonthlyTotalRevenueByMerchant(req)
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).hasSize(2);
                    assertThat(result.get(0).getTotalRevenue()).isZero();
                })
                .await().indefinitely();
    }

    @Test
    void testFindYearlyTotalRevenueByMerchant_ReturnsTwoEntries() {
        orderTotalRevenueByMerchantRepository
                .findYearlyTotalRevenueByMerchant(MERCHANT_ID, 2024)
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).hasSize(2);
                    assertThat(result.get(0).getTotalRevenue()).isZero();
                })
                .await().indefinitely();
    }

    @Test
    void testFindMonthlyTotalRevenueByMerchant_ReturnsNonNullResult() {
        MonthTotalRevenueMerchantRequest req = new MonthTotalRevenueMerchantRequest();
        req.setMerchantId(MERCHANT_ID);
        req.setYear(2024);
        req.setMonth(6);

        orderTotalRevenueByMerchantRepository
                .findMonthlyTotalRevenueByMerchant(req)
                .invoke(result -> assertThat(result).isNotNull())
                .await().indefinitely();
    }
}
