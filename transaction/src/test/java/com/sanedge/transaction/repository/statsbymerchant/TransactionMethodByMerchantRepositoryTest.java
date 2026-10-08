package com.sanedge.transaction.repository.statsbymerchant;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.sanedge.common.test.MongoDbResource;
import com.sanedge.common.test.MongoTestBase;
import com.sanedge.transaction.domain.requests.FindMonthlyMethodsByMerchantRequest;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
@QuarkusTestResource(MongoDbResource.class)
class TransactionMethodByMerchantRepositoryTest extends MongoTestBase {

    @Inject
    TransactionMethodByMerchantRepository transactionMethodByMerchantRepository;

    private static final String MERCHANT_ID = "000000000000000000000001";

    private FindMonthlyMethodsByMerchantRequest monthlyReq(String merchantId, Integer year, Integer month) {
        FindMonthlyMethodsByMerchantRequest req = new FindMonthlyMethodsByMerchantRequest();
        req.setMerchantId(merchantId);
        req.setYear(year);
        req.setMonth(month);
        return req;
    }

    @Test
    void testFindMonthlyMethodsSuccessByMerchant_ReturnsNonNullResult() {
        transactionMethodByMerchantRepository
                .findMonthlyTransactionMethodsSuccessByMerchant(monthlyReq(MERCHANT_ID, 2024, 6))
                .invoke(result -> assertThat(result).isNotNull())
                .replaceWithVoid().await().indefinitely();
    }

    @Test
    void testFindMonthlyMethodsFailedByMerchant_ReturnsNonNullResult() {
        transactionMethodByMerchantRepository
                .findMonthlyTransactionMethodsFailedByMerchant(monthlyReq(MERCHANT_ID, 2024, 6))
                .invoke(result -> assertThat(result).isNotNull())
                .replaceWithVoid().await().indefinitely();
    }

    @Test
    void testFindYearlyMethodsSuccessByMerchant_ReturnsNonNullResult() {
        transactionMethodByMerchantRepository
                .findYearlyTransactionMethodsSuccessByMerchant(MERCHANT_ID, 2024)
                .invoke(result -> assertThat(result).isNotNull())
                .replaceWithVoid().await().indefinitely();
    }

    @Test
    void testFindYearlyMethodsFailedByMerchant_ReturnsNonNullResult() {
        transactionMethodByMerchantRepository
                .findYearlyTransactionMethodsFailedByMerchant(MERCHANT_ID, 2024)
                .invoke(result -> assertThat(result).isNotNull())
                .replaceWithVoid().await().indefinitely();
    }

    @Test
    void testFindMonthlyMethodsSuccessByMerchant_PastYearReturnsEmpty() {
        transactionMethodByMerchantRepository
                .findMonthlyTransactionMethodsSuccessByMerchant(
                        monthlyReq("000000000000000000009999", 1990, 6))
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).isEmpty();
                })
                .replaceWithVoid().await().indefinitely();
    }
}
