package com.sanedge.category.repository.statsbyid;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.sanedge.common.test.MongoDbResource;
import com.sanedge.common.test.MongoTestBase;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
@QuarkusTestResource(MongoDbResource.class)
class CategoryPriceByIdRepositoryTest extends MongoTestBase {

    @Inject
    CategoryPriceByIdRepository categoryPriceByIdRepository;

    @Test
    void testFindMonthlyCategoryStatsById_ReturnsEmptyList() {
        categoryPriceByIdRepository
                .findMonthlyCategoryStatsById("507f1f77bcf86cd799439011", 2024)
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).isEmpty();
                })
                .await().indefinitely();
    }

    @Test
    void testFindYearlyCategoryStatsById_ReturnsEmptyList() {
        categoryPriceByIdRepository
                .findYearlyCategoryStatsById("507f1f77bcf86cd799439011", 2024)
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).isEmpty();
                })
                .await().indefinitely();
    }

    @Test
    void testFindMonthlyCategoryStatsById_ReturnsNonNullResult() {
        categoryPriceByIdRepository
                .findMonthlyCategoryStatsById("507f1f77bcf86cd799439011", 2023)
                .invoke(result -> assertThat(result).isNotNull())
                .await().indefinitely();
    }
}
