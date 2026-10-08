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
class CategoryPriceRepositoryTest extends MongoTestBase {

    @Inject
    CategoryPriceRepository categoryPriceRepository;

    @Test
    void testFindMonthlyCategoryStats_ReturnsEmptyListWhenNoData() {
        categoryPriceRepository
                .findMonthlyCategoryStats(2024)
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).isEmpty();
                })
                .await().indefinitely();
    }

    @Test
    void testFindYearlyCategoryStats_ReturnsEmptyListWhenNoData() {
        categoryPriceRepository
                .findYearlyCategoryStats(2024)
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).isEmpty();
                })
                .await().indefinitely();
    }

    @Test
    void testFindMonthlyCategoryStats_ReturnsNonNullResult() {
        categoryPriceRepository
                .findMonthlyCategoryStats(2023)
                .invoke(result -> assertThat(result).isNotNull())
                .await().indefinitely();
    }
}
