package com.sanedge.category.repository.stats;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.sanedge.common.test.MongoDbResource;
import com.sanedge.common.test.MongoTestBase;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
@QuarkusTestResource(MongoDbResource.class)
class CategoryTotalPriceRepositoryTest extends MongoTestBase {

    @Inject
    CategoryTotalPriceRepository categoryTotalPriceRepository;

    @Test
    void testFindMonthlyTotalPrice_ReturnsTwoPlaceholders() {
        categoryTotalPriceRepository
                .findMonthlyTotalPrice(2023, 12)
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).hasSize(2);
                    assertThat(result.get(0).getTotalRevenue()).isZero();
                })
                .await().indefinitely();
    }

    @Test
    void testFindYearlyTotalPrice_ReturnsTwoPlaceholders() {
        categoryTotalPriceRepository
                .findYearlyTotalPrice(2024)
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).hasSize(2);
                    assertThat(result.get(0).getTotalRevenue()).isZero();
                })
                .await().indefinitely();
    }

    @Test
    void testFindMonthlyTotalPrice_ReturnsNonNullResult() {
        categoryTotalPriceRepository
                .findMonthlyTotalPrice(2024, 6)
                .invoke(result -> assertThat(result).isNotNull())
                .await().indefinitely();
    }
}
