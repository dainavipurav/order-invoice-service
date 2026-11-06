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
import com.lowagie.text.pdf.draw.LineSeparator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.awt.Color;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository repository;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override

    public InvoiceResponseDto generateInvoice(InvoiceRequestDto request)
            throws DocumentException {

        // Step 1: Generate PDF in memory
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Document document = new Document(
                PageSize.A4,
                40,
                40,
                40,
                40
        );

        PdfWriter.getInstance(document, outputStream);

        document.open();

        // Step 2: Define invoice colors
        Color primaryColor = new Color(31, 58, 95);
        Color lightBlue = new Color(235, 242, 250);
        Color lightGray = new Color(245, 247, 250);
        Color darkGray = new Color(70, 70, 70);
        Color white = Color.WHITE;

        // Step 3: Define fonts
        Font academyFont = new Font(
                Font.HELVETICA,
                22,
                Font.BOLD,
                primaryColor
        );

        Font invoiceFont = new Font(
                Font.HELVETICA,
                18,
                Font.BOLD,
                darkGray
        );

        Font sectionFont = new Font(
                Font.HELVETICA,
                11,
                Font.BOLD,
                primaryColor
        );

        Font normalFont = new Font(
                Font.HELVETICA,
                10,
                Font.NORMAL,
                darkGray
        );

        Font tableHeaderFont = new Font(
                Font.HELVETICA,
                10,
                Font.BOLD,
                white
        );

        Font totalFont = new Font(
                Font.HELVETICA,
                12,
                Font.BOLD,
                primaryColor
        );

        // Step 4: Create invoice header
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{60, 40});

        PdfPCell academyCell = new PdfPCell();
        academyCell.addElement(new Paragraph(
                "TECHNICAL ACADEMY",
                academyFont
        ));
        academyCell.addElement(new Paragraph(
                "Learn. Build. Grow.",
                normalFont
        ));
        academyCell.setBorder(Rectangle.NO_BORDER);
        academyCell.setPadding(10);

        PdfPCell invoiceCell = new PdfPCell(
                new Paragraph("INVOICE", invoiceFont)
        );
        invoiceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        invoiceCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        invoiceCell.setBorder(Rectangle.NO_BORDER);
        invoiceCell.setPadding(10);

        headerTable.addCell(academyCell);
        headerTable.addCell(invoiceCell);

        document.add(headerTable);

        // Header separator
        LineSeparator separator = new LineSeparator();
        separator.setLineColor(primaryColor);
        separator.setLineWidth(2);

        document.add(new Paragraph(" "));
        document.add(separator);
        document.add(new Paragraph(" "));

        // Step 5: Invoice information
        PdfPTable invoiceInfo = new PdfPTable(2);
        invoiceInfo.setWidthPercentage(100);

        PdfPCell invoiceNumberCell = new PdfPCell(
                new Paragraph(
                        "Invoice Number: INV-" + request.getEnrollmentId(),
                        normalFont
                )
        );

        PdfPCell invoiceDateCell = new PdfPCell(
                new Paragraph(
                        "Invoice Date: " + LocalDate.now(),
                        normalFont
                )
        );

        invoiceNumberCell.setBorder(Rectangle.NO_BORDER);
        invoiceDateCell.setBorder(Rectangle.NO_BORDER);

        invoiceInfo.addCell(invoiceNumberCell);
        invoiceInfo.addCell(invoiceDateCell);

        document.add(invoiceInfo);
        document.add(new Paragraph(" "));

        // Step 6: Student information section
        document.add(new Paragraph("BILL TO", sectionFont));
        document.add(new Paragraph(" "));

        PdfPTable studentTable = new PdfPTable(1);
        studentTable.setWidthPercentage(100);

        PdfPCell studentCell = new PdfPCell();

        studentCell.addElement(new Paragraph(
                "Student Name: " + request.getStudentName(),
                normalFont
        ));

        studentCell.addElement(new Paragraph(
                "Enrollment ID: " + request.getEnrollmentId(),
                normalFont
        ));

        studentCell.setBackgroundColor(lightGray);
        studentCell.setBorder(Rectangle.NO_BORDER);
        studentCell.setPadding(12);

        studentTable.addCell(studentCell);

        document.add(studentTable);
        document.add(new Paragraph(" "));
        document.add(new Paragraph("COURSE DETAILS", sectionFont));
        document.add(new Paragraph(" "));

        // Step 7: Course details table
        PdfPTable courseTable = new PdfPTable(4);

        courseTable.setWidthPercentage(100);
        courseTable.setWidths(new float[]{40, 20, 20, 20});

        String[] headers = {
                "Description",
                "Quantity",
                "Unit Price",
                "Total"
        };

        for (String header : headers) {

            PdfPCell cell = new PdfPCell(
                    new Phrase(header, tableHeaderFont)
            );

            cell.setBackgroundColor(primaryColor);
            cell.setPadding(10);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            courseTable.addCell(cell);
        }

        // Course description
        String description =
                request.getCourseName()
                        + "\n"
                        + "Course enrollment and access to learning materials.";

        String amount = "INR " + request.getAmount();

        String[] values = {
                description,
                "1",
                amount,
                amount
        };

        for (String value : values) {

            PdfPCell cell = new PdfPCell(
                    new Phrase(value, normalFont)
            );

            cell.setPadding(12);
            cell.setBackgroundColor(lightBlue);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            courseTable.addCell(cell);
        }

        document.add(courseTable);

        document.add(new Paragraph(" "));

        // Step 8: Amount summary
        PdfPTable totalTable = new PdfPTable(2);

        totalTable.setWidthPercentage(50);
        totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalTable.setWidths(new float[]{50, 50});

        PdfPCell subtotalLabel = new PdfPCell(
                new Phrase("Subtotal", normalFont)
        );

        PdfPCell subtotalValue = new PdfPCell(
                new Phrase(amount, normalFont)
        );

        PdfPCell totalLabel = new PdfPCell(
                new Phrase("TOTAL AMOUNT", totalFont)
        );

        PdfPCell totalValue = new PdfPCell(
                new Phrase(amount, totalFont)
        );

        for (PdfPCell cell : new PdfPCell[]{
                subtotalLabel, subtotalValue,
                totalLabel, totalValue
        }) {
            cell.setPadding(10);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        }

        subtotalLabel.setBackgroundColor(lightGray);
        subtotalValue.setBackgroundColor(lightGray);
        totalLabel.setBackgroundColor(lightBlue);
        totalValue.setBackgroundColor(lightBlue);

        totalTable.addCell(subtotalLabel);
        totalTable.addCell(subtotalValue);
        totalTable.addCell(totalLabel);
        totalTable.addCell(totalValue);

        document.add(totalTable);

        // Step 9: Footer
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        document.add(separator);
        document.add(new Paragraph(" "));

        Paragraph thankYou = new Paragraph(
                "Thank you for choosing Technical Academy!",
                sectionFont
        );

        thankYou.setAlignment(Element.ALIGN_CENTER);

        document.add(thankYou);

        Paragraph footer = new Paragraph(
                "This is a computer-generated invoice.",
                new Font(Font.HELVETICA, 9, Font.ITALIC, darkGray)
        );

        footer.setAlignment(Element.ALIGN_CENTER);

        document.add(footer);

        // Step 10: Close PDF document
        document.close();

        // Step 11: Convert PDF to byte array
        byte[] pdfBytes = outputStream.toByteArray();

        // Step 12: Create InvoiceEntity
        InvoiceEntity invoice = InvoiceEntity.builder()
                .fileName("invoice-" + request.getEnrollmentId() + ".pdf")
                .contentType("application/pdf")
                .pdfData(pdfBytes)
                .build();

        // Step 13: Save invoice in MySQL
        InvoiceEntity result = repository.save(invoice);

        // Step 14: Return downloadable path
        return InvoiceResponseDto.builder()
                .invoiceId(result.getId())
                .fileName(result.getFileName())
                .downloadPath(
                        baseUrl + "/invoices/download/" + result.getId()
                )
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
                .downloadPath(baseUrl + "/invoices/download/" + id)
                .status(HttpStatus.OK.value())
                .message("Invoice downloaded Successfully")
                .contentType(invoiceEntity.getContentType())
                .pdfData(invoiceEntity.getPdfData())
                .build();
    }
}
