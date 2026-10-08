package com.sanedge.gateway.service;

import com.sanedge.gateway.dto.TransactionDto;
import io.smallrye.mutiny.Uni;

public interface TransactionService {
    Uni<TransactionDto.ApiResponsePaginationTransaction> findAll(int page, int size, String search);
    Uni<TransactionDto.ApiResponsePaginationTransaction> findByMerchant(String merchantId, int page, int size, String search);
    Uni<TransactionDto.ApiResponseTransaction> findById(String id);
    Uni<TransactionDto.ApiResponseTransaction> findByOrderId(String orderId);
    Uni<TransactionDto.ApiResponsePaginationTransaction> findByActive(int page, int size, String search);
    Uni<TransactionDto.ApiResponsePaginationTransactionDeleteAt> findByTrashed(int page, int size, String search);

    Uni<TransactionDto.ApiResponseTransaction> create(pb.transaction.TransactionCommand.CreateTransactionRequest body);
    Uni<TransactionDto.ApiResponseTransaction> update(String id, pb.transaction.TransactionCommand.UpdateTransactionRequest body);
    Uni<TransactionDto.ApiResponseTransactionDeleteAt> delete(String id);
    Uni<TransactionDto.ApiResponseTransactionDeleteAt> restore(String id);
    Uni<TransactionDto.SimpleResponse> deletePermanent(String id);
    Uni<TransactionDto.SimpleResponse> restoreAll();
    Uni<TransactionDto.SimpleResponse> deleteTransactionByOrderPermanent(String orderId);
    Uni<TransactionDto.SimpleResponse> deleteAllPermanent();

    Uni<TransactionDto.ApiResponseTransactionMonthAmountSuccess> getMonthlyAmountSuccess(int year, int month);
    Uni<TransactionDto.ApiResponseTransactionYearAmountSuccess> getYearlyAmountSuccess(int year);
    Uni<TransactionDto.ApiResponseTransactionMonthAmountFailed> getMonthlyAmountFailed(int year, int month);
    Uni<TransactionDto.ApiResponseTransactionYearAmountFailed> getYearlyAmountFailed(int year);

    Uni<TransactionDto.ApiResponseTransactionMonthPaymentMethod> getMonthlyTransactionMethodSuccess(int year, int month);
    Uni<TransactionDto.ApiResponseTransactionYearPaymentmethod> getYearlyTransactionMethodSuccess(int year);
    Uni<TransactionDto.ApiResponseTransactionMonthPaymentMethod> getMonthlyTransactionMethodFailed(int year, int month);
    Uni<TransactionDto.ApiResponseTransactionYearPaymentmethod> getYearlyTransactionMethodFailed(int year);

    Uni<TransactionDto.ApiResponseTransactionMonthAmountSuccess> getMonthlyAmountSuccessByMerchant(String merchantId, int year, int month);
    Uni<TransactionDto.ApiResponseTransactionYearAmountSuccess> getYearlyAmountSuccessByMerchant(String merchantId, int year);
    Uni<TransactionDto.ApiResponseTransactionMonthAmountFailed> getMonthlyAmountFailedByMerchant(String merchantId, int year, int month);
    Uni<TransactionDto.ApiResponseTransactionYearAmountFailed> getYearlyAmountFailedByMerchant(String merchantId, int year);

    Uni<TransactionDto.ApiResponseTransactionMonthPaymentMethod> getMonthlyTransactionMethodByMerchantSuccess(String merchantId, int year, int month);
    Uni<TransactionDto.ApiResponseTransactionYearPaymentmethod> getYearlyTransactionMethodByMerchantSuccess(String merchantId, int year);
    Uni<TransactionDto.ApiResponseTransactionMonthPaymentMethod> getMonthlyTransactionMethodByMerchantFailed(String merchantId, int year, int month);
    Uni<TransactionDto.ApiResponseTransactionYearPaymentmethod> getYearlyTransactionMethodByMerchantFailed(String merchantId, int year);
}
