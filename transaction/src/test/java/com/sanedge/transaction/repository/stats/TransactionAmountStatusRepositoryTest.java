package com.sanedge.transaction.repository.stats;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.sanedge.common.test.MongoDbResource;
import com.sanedge.common.test.MongoTestBase;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
@QuarkusTestResource(MongoDbResource.class)
class TransactionAmountStatusRepositoryTest extends MongoTestBase {

    @Inject
    TransactionAmountStatusRepository transactionAmountStatusRepository;

    @Test
    void testFindMonthlyTransactionSuccess_ReturnsTwoPlaceholders() {
        transactionAmountStatusRepository
                .findMonthlyTransactionSuccess(2023, 12)
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).hasSize(2);
                    assertThat(result.get(0).getTotalSuccess()).isZero();
                    assertThat(result.get(0).getTotalAmount()).isZero();
                })
                .replaceWithVoid().await().indefinitely();
    }

    @Test
    void testFindYearlyTransactionSuccess_ReturnsTwoPlaceholders() {
        transactionAmountStatusRepository
                .findYearlyTransactionSuccess(2024)
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).hasSize(2);
                    assertThat(result.get(0).getTotalSuccess()).isZero();
                })
                .replaceWithVoid().await().indefinitely();
    }

    @Test
    void testFindMonthlyTransactionFailed_ReturnsTwoPlaceholders() {
        transactionAmountStatusRepository
                .findMonthlyTransactionFailed(2023, 12)
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).hasSize(2);
                    assertThat(result.get(0).getTotalFailed()).isZero();
                })
                .replaceWithVoid().await().indefinitely();
    }

    @Test
    void testFindYearlyTransactionFailed_ReturnsTwoPlaceholders() {
        transactionAmountStatusRepository
                .findYearlyTransactionFailed(2024)
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).hasSize(2);
                    assertThat(result.get(0).getTotalFailed()).isZero();
                })
                .replaceWithVoid().await().indefinitely();
    }

    @Test
    void testFindMonthlyTransactionSuccess_ReturnsNonNullResult() {
        transactionAmountStatusRepository
                .findMonthlyTransactionSuccess(2024, 6)
                .invoke(result -> assertThat(result).isNotNull())
                .replaceWithVoid().await().indefinitely();
    }
}
