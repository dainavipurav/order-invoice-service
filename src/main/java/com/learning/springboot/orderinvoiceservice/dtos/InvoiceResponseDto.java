package com.learning.springboot.orderinvoiceservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceResponseDto {
    private Long invoiceId;

    private String fileName;

    private String downloadPath;

    private int status;

    private String message;

    private String contentType;

    private byte[] pdfData;
}
