package com.sanedge.transaction.repository.statsbymerchant;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.sanedge.common.test.MongoDbResource;
import com.sanedge.common.test.MongoTestBase;
import com.sanedge.transaction.domain.requests.FindMonthlyAmountByMerchantRequest;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
@QuarkusTestResource(MongoDbResource.class)
class TransactionAmountByMerchantRepositoryTest extends MongoTestBase {

    @Inject
    TransactionAmountByMerchantRepository transactionAmountByMerchantRepository;

    private static final String MERCHANT_ID = "000000000000000000000001";

    private FindMonthlyAmountByMerchantRequest monthlyReq(String merchantId, Integer year, Integer month) {
        FindMonthlyAmountByMerchantRequest req = new FindMonthlyAmountByMerchantRequest();
        req.setMerchantId(merchantId);
        req.setYear(year);
        req.setMonth(month);
        return req;
    }

    @Test
    void testFindMonthlySuccessByMerchant_ReturnsTwoPlaceholders() {
        transactionAmountByMerchantRepository
                .findMonthlySuccessByMerchant(monthlyReq(MERCHANT_ID, 2023, 12))
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).hasSize(2);
                    assertThat(result.get(0).getTotalSuccess()).isZero();
                    assertThat(result.get(0).getTotalAmount()).isZero();
                })
                .replaceWithVoid().await().indefinitely();
    }

    @Test
    void testFindYearlySuccessByMerchant_ReturnsTwoPlaceholders() {
        transactionAmountByMerchantRepository
                .findYearlySuccessByMerchant(MERCHANT_ID, 2024)
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).hasSize(2);
                    assertThat(result.get(0).getTotalSuccess()).isZero();
                })
                .replaceWithVoid().await().indefinitely();
    }

    @Test
    void testFindMonthlyFailedByMerchant_ReturnsTwoPlaceholders() {
        transactionAmountByMerchantRepository
                .findMonthlyFailedByMerchant(monthlyReq(MERCHANT_ID, 2023, 12))
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).hasSize(2);
                    assertThat(result.get(0).getTotalFailed()).isZero();
                })
                .replaceWithVoid().await().indefinitely();
    }

    @Test
    void testFindYearlyFailedByMerchant_ReturnsTwoPlaceholders() {
        transactionAmountByMerchantRepository
                .findYearlyFailedByMerchant(MERCHANT_ID, 2024)
                .invoke(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result).hasSize(2);
                    assertThat(result.get(0).getTotalFailed()).isZero();
                })
                .replaceWithVoid().await().indefinitely();
    }
}
