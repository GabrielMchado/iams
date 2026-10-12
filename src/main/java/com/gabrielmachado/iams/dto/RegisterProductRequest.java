package com.gabrielmachado.iams.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RegisterProductRequest(@NotBlank String productName, String description, @NotNull @Positive(message = "Preço invalido") BigDecimal price){
}
