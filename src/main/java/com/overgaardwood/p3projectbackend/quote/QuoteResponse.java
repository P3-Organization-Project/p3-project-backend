package com.overgaardwood.p3projectbackend.quote;

public record QuoteResponse(String quoteId, String description, double totalPriceExVat) {}