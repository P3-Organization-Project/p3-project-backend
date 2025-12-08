package com.overgaardwood.p3projectbackend.quote;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.overgaardwood.p3projectbackend.interiordoor.InteriorDoorController;
import com.overgaardwood.p3projectbackend.interiordoor.InteriorDoorRequest;
import com.overgaardwood.p3projectbackend.pdf.PdfQuoteGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final InteriorDoorController doorController;
    private final QuoteRepository quoteRepository;
    private final ObjectMapper objectMapper; // Spring injects this

    // SAVE QUOTE
    @PostMapping("/save")
    public QuoteResponse saveQuote(@RequestBody InteriorDoorRequest request) throws JsonProcessingException {
        var result = doorController.calculate(request);

        Quote quote = Quote.builder()
                .requestJson(objectMapper.writeValueAsString(request))
                .description(result.description())
                .totalPriceExVat(result.totalPriceExVat())
                .build();

        quoteRepository.save(quote);

        return new QuoteResponse(quote.getId(), quote.getDescription(), quote.getTotalPriceExVat());
    }

    // GET ALL SAVED QUOTES (for list in frontend)
    @GetMapping
    public List<QuoteResponse> getRecentQuotes() {
        return quoteRepository.findTop20ByOrderByCreatedAtDesc().stream()
                .map(q -> new QuoteResponse(q.getId(), q.getDescription(), q.getTotalPriceExVat()))
                .toList();
    }

    // GENERATE PDF
    @PostMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generatePdf(@RequestBody InteriorDoorRequest request) {
        var result = doorController.calculate(request);

        byte[] pdfBytes = PdfQuoteGenerator.generateQuotePdf(
                "Q" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
                result.description(),
                result.totalPriceExVat()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("Tilbud_InteriorDoor_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf")
                .build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}

