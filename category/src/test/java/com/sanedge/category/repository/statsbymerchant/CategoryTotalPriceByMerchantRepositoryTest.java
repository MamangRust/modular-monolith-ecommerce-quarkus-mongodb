package com.sanedge.category.repository.statsbymerchant;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.sanedge.category.domain.requests.FindMonthlyCategoryTotalPriceByMerchantRequest;
import com.sanedge.common.test.MongoDbResource;
import com.sanedge.common.test.MongoTestBase;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
@QuarkusTestResource(MongoDbResource.class)
class CategoryTotalPriceByMerchantRepositoryTest extends MongoTestBase {

    private static final String MERCHANT_ID = "507f1f77bcf86cd799439011";

    @Inject
    CategoryTotalPriceByMerchantRepository categoryTotalPriceByMerchantRepository;

    private FindMonthlyCategoryTotalPriceByMerchantRequest request(String merchantId, int year, int month) {
        FindMonthlyCategoryTotalPriceByMerchantRequest req = new FindMonthlyCategoryTotalPriceByMerchantRequest();
        req.setMerchantId(merchantId);
        req.setYear(year);
        req.setMonth(month);
        return req;
    }

    @Test
    void testFindMonthlyTotalPriceByMerchant_ReturnsTwoPlaceholders() {
        categoryTotalPriceByMerchantRepository
                .findMonthlyTotalPriceByMerchant(request(MERCHANT_ID, 2023, 12))
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).hasSize(2);
                    assertThat(result.get(0).getTotalRevenue()).isZero();
                })
                .await().indefinitely();
    }

    @Test
    void testFindYearlyTotalPriceByMerchant_ReturnsTwoPlaceholders() {
        categoryTotalPriceByMerchantRepository
                .findYearlyTotalPriceByMerchant(MERCHANT_ID, 2024)
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).hasSize(2);
                    assertThat(result.get(0).getTotalRevenue()).isZero();
                })
                .await().indefinitely();
    }

    @Test
    void testFindMonthlyTotalPriceByMerchant_ReturnsNonNullResult() {
        categoryTotalPriceByMerchantRepository
                .findMonthlyTotalPriceByMerchant(request(MERCHANT_ID, 2024, 6))
                .invoke(result -> assertThat(result).isNotNull())
                .await().indefinitely();
    }
}
