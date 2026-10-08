package com.sanedge.transaction.domain.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.QueryParam;
import lombok.Data;

@Data
@Schema(description = "Request untuk mengambil metode transaksi bulanan berdasarkan merchant")
public class FindMonthlyMethodsByMerchantRequest {

    @NotNull
    @QueryParam("merchantId")
    @Schema(description = "ID merchant", example = "507f1f77bcf86cd799439011", requiredMode = Schema.RequiredMode.REQUIRED)
    private String merchantId;

    @NotNull
    @QueryParam("year")
    @Schema(description = "Tahun transaksi", example = "2025", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer year;

    @NotNull
    @QueryParam("month")
    @Schema(description = "Bulan transaksi", example = "9", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer month;
}
