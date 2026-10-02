package com.gabrielmachado.iams.dto;

import java.math.BigDecimal;

public record PutProductResponse(BigDecimal price, String description) {
}
