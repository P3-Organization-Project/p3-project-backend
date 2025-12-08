package com.overgaardwood.p3projectbackend.services;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.overgaardwood.p3projectbackend.entities.Case;
import com.overgaardwood.p3projectbackend.entities.DoorItem;
import com.overgaardwood.p3projectbackend.interiordoor.InteriorDoor;
import com.overgaardwood.p3projectbackend.interiordoor.InteriorDoorRequest;
import com.overgaardwood.p3projectbackend.interiordoor.pricing.MaterialPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final ObjectMapper objectMapper;
    private final MaterialPriceService materialPriceService;

    @Value("${app.upload-dir}")
    private String uploadDir;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public String generateCasePdf(Case caseEntity) throws IOException {
        String fileName = "case-" + caseEntity.getCaseId() + ".pdf";
        String filePath = uploadDir + "/" + fileName;

        File dir = new File(uploadDir);
        dir.mkdirs();

        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        try {
            PdfFont bold = PdfFontFactory.createFont("Helvetica-Bold");
            PdfFont regular = PdfFontFactory.createFont("Helvetica");

            // === TITLE ===
            document.add(new Paragraph("TILBUD")
                    .setFont(bold)
                    .setFontSize(28)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(30));

            // === INFO TABLE ===
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}));
            infoTable.setWidth(UnitValue.createPercentValue(100));

            infoTable.addCell(cell("Tilbudsnr.", bold));
            infoTable.addCell(cell("CASE-" + caseEntity.getCaseId(), regular));

            infoTable.addCell(cell("Dato", bold));
            infoTable.addCell(cell(caseEntity.getCreatedDate().format(DATE_FORMAT), regular));

            // Kunde — ONLY uses getName() — NO getCompany() ANYWHERE
            infoTable.addCell(cell("Kunde", bold));
            String customerName = caseEntity.getCustomer() != null && caseEntity.getCustomer().getName() != null
                    ? caseEntity.getCustomer().getName()
                    : "Ukendt kunde";
            infoTable.addCell(cell(customerName, regular));

            infoTable.addCell(cell("Sælger", bold));
            String sellerName = caseEntity.getSeller() != null && caseEntity.getSeller().getName() != null
                    ? caseEntity.getSeller().getName()
                    : "Ukendt sælger";
            infoTable.addCell(cell(sellerName, regular));

            document.add(infoTable.setMarginBottom(40));

            // === DOOR ITEMS ===
            for (DoorItem di : caseEntity.getDoorItems()) {
                String description = "Dørkonfiguration mangler";

                if (di.getDoorConfigurationJson() != null && !di.getDoorConfigurationJson().isBlank()) {
                    try {
                        InteriorDoorRequest req = objectMapper.readValue(
                                di.getDoorConfigurationJson(), InteriorDoorRequest.class);
                        InteriorDoor door = req.toInteriorDoor(materialPriceService);
                        description = door.describe();
                    } catch (Exception e) {
                        description = "Kunne ikke læse dørkonfiguration";
                    }
                }

                Cell doorCell = new Cell()
                        .add(new Paragraph(description)
                                .setFont(regular)
                                .setFontSize(11))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setPadding(15)
                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(ColorConstants.GRAY, 1));

                document.add(new Table(UnitValue.createPercentArray(1))
                        .setWidth(UnitValue.createPercentValue(100))
                        .addCell(doorCell));
                document.add(new Paragraph(" ").setMarginBottom(10));
            }

            // === TOTAL PRICE ===
            document.add(new Paragraph("TOTAL EKSKL. MOMS")
                    .setFont(bold)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.add(new Paragraph(String.format("%,.2f DKK", caseEntity.getTotalPrice()))
                    .setFont(bold)
                    .setFontSize(26)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(60));

            // === FOOTER ===
            document.add(new Paragraph("Tak for din forespørgsel!\nTilbuddet er gældende i 30 dage fra dags dato.")
                    .setFont(regular)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("Overgaard Wood A/S • Industriparken 15 • 5550 Langeskov • Tlf. 12 34 56 78 • CVR: 12 34 56 78")
                    .setFont(regular)
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER));

        } finally {
            document.close();
        }

        return filePath;
    }

    private Cell cell(String content, PdfFont font) {
        return new Cell()
                .add(new Paragraph(content).setFont(font).setFontSize(11))
                .setBorder(Border.NO_BORDER)
                .setPadding(5);
    }
}