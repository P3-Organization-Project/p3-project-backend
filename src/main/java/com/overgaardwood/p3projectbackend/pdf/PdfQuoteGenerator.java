package com.overgaardwood.p3projectbackend.pdf;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.io.image.ImageDataFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PdfQuoteGenerator {

    public static byte[] generateQuotePdf(String quoteId, String description, double totalPriceExVat) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont bold = PdfFontFactory.createFont("Helvetica-Bold");
            PdfFont regular = PdfFontFactory.createFont("Helvetica");

            // === HEADER WITH LOGO (optional) ===
            try {
                Image logo = new Image(ImageDataFactory.create(
                        new ClassPathResource("static/logo.png").getURL()));
                logo.setWidth(140);
                logo.setAutoScale(true);
                document.add(logo.setMarginBottom(20));
            } catch (Exception e) {
                document.add(new Paragraph("OVERGAARD WOOD A/S")
                        .setFont(bold).setFontSize(26)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(10));
            }

            document.add(new Paragraph("TILBUD – INDVENDIG DØR")
                    .setFont(bold).setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            document.add(new Paragraph("Tilbud nr: " + quoteId)
                    .setFont(regular).setFontSize(12));
            document.add(new Paragraph("Dato: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
                    .setFont(regular).setFontSize(12)
                    .setMarginBottom(20));

            // === DOOR DESCRIPTION ===
            document.add(new Paragraph("Konfiguration:")
                    .setFont(bold).setFontSize(14));
            document.add(new Paragraph(description.replace(" | ", "\n"))
                    .setFont(regular).setFontSize(11)
                    .setMarginBottom(30));

            // === PRICE TABLE ===
            float[] columnWidths = {380, 120};
            Table table = new Table(columnWidths);
            table.setWidth(500);

            table.addHeaderCell(new Cell().add(new Paragraph("Beskrivelse").setFont(bold))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY).setBorder(Border.NO_BORDER));
            table.addHeaderCell(new Cell().add(new Paragraph("Pris ekskl. moms").setFont(bold))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

            table.addCell(new Cell().add(new Paragraph("Indvendig dør inkl. karm og beslag"))
                    .setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph(String.format("%,.2f DKK", totalPriceExVat))
                    .setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)));

            table.addCell(new Cell(1, 2).add(new Paragraph(" ").setHeight(15)).setBorder(Border.NO_BORDER));

            table.addCell(new Cell().add(new Paragraph("TOTAL EKSKL. MOMS").setFont(bold))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph(String.format("%,.2f DKK", totalPriceExVat)).setFont(bold))
                    .setTextAlignment(TextAlignment.RIGHT).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBorder(Border.NO_BORDER));

            document.add(table);

            // === FOOTER ===
            document.add(new Paragraph("Tak for din forespørgsel!\nTilbuddet er gældende i 30 dage fra dags dato.")
                    .setFont(regular).setFontSize(10).setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(50));

            document.add(new Paragraph("Overgaard Wood A/S • Industriparken 15 • 5550 Langeskov • Tlf. 1234 5678 • CVR: 12345678")
                    .setFont(regular).setFontSize(9).setTextAlignment(TextAlignment.CENTER));

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}