package com.sanedge.category.repository.statsbyid;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.sanedge.category.domain.requests.FindMonthlyCategoryTotalPriceByIdRequest;
import com.sanedge.common.test.MongoDbResource;
import com.sanedge.common.test.MongoTestBase;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
@QuarkusTestResource(MongoDbResource.class)
class CategoryTotalPriceByIdRepositoryTest extends MongoTestBase {

    private static final String CATEGORY_ID = "507f1f77bcf86cd799439011";

    @Inject
    CategoryTotalPriceByIdRepository categoryTotalPriceByIdRepository;

    private FindMonthlyCategoryTotalPriceByIdRequest request(String categoryId, int year, int month) {
        FindMonthlyCategoryTotalPriceByIdRequest req = new FindMonthlyCategoryTotalPriceByIdRequest();
        req.setCategoryId(categoryId);
        req.setYear(year);
        req.setMonth(month);
        return req;
    }

    @Test
    void testFindMonthlyTotalPriceByCategory_ReturnsTwoPlaceholders() {
        categoryTotalPriceByIdRepository
                .findMonthlyTotalPriceByCategory(request(CATEGORY_ID, 2023, 12))
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).hasSize(2);
                    assertThat(result.get(0).getTotalRevenue()).isZero();
                })
                .await().indefinitely();
    }

    @Test
    void testFindYearlyTotalPriceByCategory_ReturnsTwoPlaceholders() {
        categoryTotalPriceByIdRepository
                .findYearlyTotalPriceByCategory(CATEGORY_ID, 2024)
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).hasSize(2);
                    assertThat(result.get(0).getTotalRevenue()).isZero();
                })
                .await().indefinitely();
    }

    @Test
    void testFindMonthlyTotalPriceByCategory_ReturnsNonNullResult() {
        categoryTotalPriceByIdRepository
                .findMonthlyTotalPriceByCategory(request(CATEGORY_ID, 2024, 6))
                .invoke(result -> assertThat(result).isNotNull())
                .await().indefinitely();
    }
}
