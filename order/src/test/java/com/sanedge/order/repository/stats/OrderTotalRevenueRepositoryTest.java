package com.sanedge.order.repository.stats;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import com.sanedge.common.test.MongoDbResource;
import com.sanedge.common.test.MongoTestBase;
import io.quarkus.test.common.QuarkusTestResource;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

@Disabled("Mongo aggregation returns empty buckets without seeded data; verify output before enabling")
@QuarkusTest
@QuarkusTestResource(MongoDbResource.class)
class OrderTotalRevenueRepositoryTest extends MongoTestBase {

    @Inject
    OrderTotalRevenueRepository orderTotalRevenueRepository;

    @Test
    void testFindMonthlyTotalRevenue_ReturnsTwoEntries() {
        orderTotalRevenueRepository
                .findMonthlyTotalRevenue(2023, 12)
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).hasSize(2);
                    assertThat(result.get(0).getTotalRevenue()).isZero();
                })
                .await().indefinitely();
    }

    @Test
    void testFindYearlyTotalRevenue_ReturnsTwoEntries() {
        orderTotalRevenueRepository
                .findYearlyTotalRevenue(2024)
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).hasSize(2);
                    assertThat(result.get(0).getTotalRevenue()).isZero();
                })
                .await().indefinitely();
    }

    @Test
    void testFindMonthlyTotalRevenue_ReturnsNonNullResult() {
        orderTotalRevenueRepository
                .findMonthlyTotalRevenue(2024, 6)
                .invoke(result -> assertThat(result).isNotNull())
                .await().indefinitely();
    }
}
