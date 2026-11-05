package com.learning.springboot.orderinvoiceservice.service;

import com.learning.springboot.orderinvoiceservice.dtos.InvoiceRequestDto;
import com.learning.springboot.orderinvoiceservice.dtos.InvoiceResponseDto;

public interface InvoiceService {

    InvoiceResponseDto generateInvoice(InvoiceRequestDto request);

    InvoiceResponseDto downloadInvoiceById(long id);
}
