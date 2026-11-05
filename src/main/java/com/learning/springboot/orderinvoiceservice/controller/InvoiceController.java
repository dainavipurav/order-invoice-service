package com.learning.springboot.orderinvoiceservice.controller;

import com.learning.springboot.orderinvoiceservice.dtos.InvoiceRequestDto;
import com.learning.springboot.orderinvoiceservice.dtos.InvoiceResponseDto;
import com.learning.springboot.orderinvoiceservice.entity.InvoiceEntity;
import com.learning.springboot.orderinvoiceservice.repository.InvoiceRepository;
import com.learning.springboot.orderinvoiceservice.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invoices")
@Validated
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/generate")
    ResponseEntity<InvoiceResponseDto> generateInvoice(@Valid @RequestBody InvoiceRequestDto request){

        InvoiceResponseDto invoice =
                invoiceService.generateInvoice(request);

        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/download/{id}")
    ResponseEntity<byte[]> downloadInvoice(@PathVariable long id){
        InvoiceResponseDto invoice = invoiceService.downloadInvoiceById(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + invoice.getFileName() + "\""
                )
                .body(invoice.getPdfData());
    }
}
