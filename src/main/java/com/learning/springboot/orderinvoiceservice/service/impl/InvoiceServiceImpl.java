package com.learning.springboot.orderinvoiceservice.service.impl;

import com.learning.springboot.orderinvoiceservice.dtos.InvoiceRequestDto;
import com.learning.springboot.orderinvoiceservice.dtos.InvoiceResponseDto;
import com.learning.springboot.orderinvoiceservice.entity.InvoiceEntity;
import com.learning.springboot.orderinvoiceservice.exception.InvoiceNotFoundException;
import com.learning.springboot.orderinvoiceservice.repository.InvoiceRepository;
import com.learning.springboot.orderinvoiceservice.service.InvoiceService;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository repository;

    @Override
    public InvoiceResponseDto generateInvoice(InvoiceRequestDto request) {

        // Step 1: Generate PDF in memory
        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        Document document = new Document();

        PdfWriter.getInstance(document, outputStream);

        document.open();

        // Step 2: Add dummy invoice details
        Font titleFont = new Font(
                Font.HELVETICA,
                20,
                Font.BOLD
        );

        document.add(new Paragraph("TECHNICAL ACADEMY", titleFont));

        document.add(new Paragraph(" "));

        document.add(new Paragraph("INVOICE"));

        document.add(new Paragraph("Invoice Number: "));

        document.add(new Paragraph("Student Name: " + request.getStudentName()));

        document.add(new Paragraph("Course: " + request.getCourseName()));

        document.add(new Paragraph("Amount: INR " + request.getAmount()));

        document.add(new Paragraph("Invoice Date: " + LocalDateTime.now()));

        document.add(new Paragraph(" "));

        document.add(new Paragraph("Thank you for enrolling!"));

        // Step 3: Close document
        document.close();

        // Step 4: Convert PDF into byte array
        byte[] pdfBytes = outputStream.toByteArray();

        // Step 5: Create InvoiceEntity
        InvoiceEntity invoice = InvoiceEntity.builder()
                .fileName("invoice.pdf")
                .contentType("application/pdf")
                .pdfData(pdfBytes).build();

        // Step 6: Save PDF bytes in MySQL

        InvoiceEntity result = repository.save(invoice);
        return InvoiceResponseDto.builder()
                .invoiceId(result.getId())
                .fileName(result.getFileName())
                .downloadPath("/api/invoices/" + result.getId() + "/download")
                .status(HttpStatus.OK.value())
                .message("Invoice Generated Successfully")
                .build();
    }

    @Override
    public InvoiceResponseDto downloadInvoiceById(long id) {
        InvoiceEntity invoiceEntity = repository.findById(id).orElseThrow(() -> new InvoiceNotFoundException("Invoice not found : " + id));

        return InvoiceResponseDto.builder()
                .invoiceId(invoiceEntity.getId())
                .fileName(invoiceEntity.getFileName())
                .downloadPath("/api/invoices/" + id + "/download")
                .status(HttpStatus.OK.value())
                .message("Invoice downloaded Successfully")
                .contentType(invoiceEntity.getContentType())
                .pdfData(invoiceEntity.getPdfData())
                .build();
    }
}
