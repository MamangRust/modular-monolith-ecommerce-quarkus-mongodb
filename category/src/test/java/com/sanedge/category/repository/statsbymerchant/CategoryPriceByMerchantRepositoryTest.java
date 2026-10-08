package com.sanedge.category.repository.statsbymerchant;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.sanedge.common.test.MongoDbResource;
import com.sanedge.common.test.MongoTestBase;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
@QuarkusTestResource(MongoDbResource.class)
class CategoryPriceByMerchantRepositoryTest extends MongoTestBase {

    private static final String MERCHANT_ID = "507f1f77bcf86cd799439011";

    @Inject
    CategoryPriceByMerchantRepository categoryPriceByMerchantRepository;

    @Test
    void testFindMonthlyCategoryStatsByMerchant_ReturnsEmptyList() {
        categoryPriceByMerchantRepository
                .findMonthlyCategoryStatsByMerchant(MERCHANT_ID, 2024)
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).isEmpty();
                })
                .await().indefinitely();
    }

    @Test
    void testFindYearlyCategoryStatsByMerchant_ReturnsEmptyList() {
        categoryPriceByMerchantRepository
                .findYearlyCategoryStatsByMerchant(MERCHANT_ID, 2024)
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).isEmpty();
                })
                .await().indefinitely();
    }

    @Test
    void testFindMonthlyCategoryStatsByMerchant_ReturnsNonNullResult() {
        categoryPriceByMerchantRepository
                .findMonthlyCategoryStatsByMerchant(MERCHANT_ID, 2023)
                .invoke(result -> assertThat(result).isNotNull())
                .await().indefinitely();
    }
}
