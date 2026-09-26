package com.gabrielmachado.iams.dto;

import java.math.BigDecimal;

public record RegisterProductRequest(String productName, String description, BigDecimal price){
}
