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
class TransactionMethodRepositoryTest extends MongoTestBase {

    @Inject
    TransactionMethodRepository transactionMethodRepository;

    @Test
    void testFindMonthlyMethodsSuccess_ReturnsNonNullResult() {
        transactionMethodRepository.findMonthlyMethodsSuccess(2024, 6)
                .invoke(result -> assertThat(result).isNotNull())
                .replaceWithVoid().await().indefinitely();
    }

    @Test
    void testFindMonthlyMethodsFailed_ReturnsNonNullResult() {
        transactionMethodRepository.findMonthlyMethodsFailed(2024, 6)
                .invoke(result -> assertThat(result).isNotNull())
                .replaceWithVoid().await().indefinitely();
    }

    @Test
    void testFindYearlyMethodsSuccess_ReturnsNonNullResult() {
        transactionMethodRepository.findYearlyMethodsSuccess(2024)
                .invoke(result -> assertThat(result).isNotNull())
                .replaceWithVoid().await().indefinitely();
    }

    @Test
    void testFindYearlyMethodsFailed_ReturnsNonNullResult() {
        transactionMethodRepository.findYearlyMethodsFailed(2024)
                .invoke(result -> assertThat(result).isNotNull())
                .replaceWithVoid().await().indefinitely();
    }

    @Test
    void testFindMonthlyMethodsSuccess_PastYearReturnsEmpty() {
        transactionMethodRepository.findMonthlyMethodsSuccess(1990, 6)
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).isEmpty();
                })
                .replaceWithVoid().await().indefinitely();
    }
}
