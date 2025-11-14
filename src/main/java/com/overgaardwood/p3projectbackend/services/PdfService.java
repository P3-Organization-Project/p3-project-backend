package com.overgaardwood.p3projectbackend.services;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.overgaardwood.p3projectbackend.entities.Case;
import com.overgaardwood.p3projectbackend.entities.DoorItem;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;

@Service
public class PdfService {

    @Value("${app.upload-dir}")
    private String uploadDir; // ← Spring injects the value

    public String generateCasePdf(Case caseEntity) throws IOException {
        String fileName = "case-" + caseEntity.getCaseId() + ".pdf";
        String filePath = uploadDir + "/" + fileName; // ← Use config

        //Ensure directory exist
        File dir = new File(uploadDir);
        dir.mkdirs(); // ← Create if missing

        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        //Title
        document.add(new Paragraph("Case Summary")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));

        // Info Table OBS: cells are able to contain pictures from DB
        Table infoTable = new Table(2);
        infoTable.addCell("Case ID:");
        infoTable.addCell(caseEntity.getCaseId().toString());
        infoTable.addCell("Date:");
        infoTable.addCell(caseEntity.getCreatedDate().toString());
        infoTable.addCell("Status");
        infoTable.addCell(caseEntity.getDealStatus());
        infoTable.addCell("Customer");
        infoTable.addCell(caseEntity.getCustomer().getName());
        infoTable.addCell("Seller");
        infoTable.addCell(caseEntity.getSeller().getName());
        document.add(infoTable);

        document.add(new Paragraph("\n"));

        //Door Items Table OBS: cells are able to contain pictures from DB
        Table doorTable = new Table(5);
        doorTable.addHeaderCell("Height");
        doorTable.addHeaderCell("Width");
        doorTable.addHeaderCell("Hinge Side");
        doorTable.addHeaderCell("Direction");
        doorTable.addHeaderCell("Material Cost");

        for (DoorItem di : caseEntity.getDoorItems()) {
            doorTable.addCell(di.getHeight().toString());
            doorTable.addCell(di.getWidth().toString());
            doorTable.addCell(di.getHingeSide());
            doorTable.addCell(di.getOpeningDirection());
            doorTable.addCell(di.getTotalMaterialCost().toString());
            }

        document.add(doorTable);
        //Total
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("TOTAL PRICE" + caseEntity.getTotalPrice() + "DKK")
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.RIGHT));

        document.close();

        return filePath;


        }
    }
