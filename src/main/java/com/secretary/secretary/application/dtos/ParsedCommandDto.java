package com.secretary.secretary.application.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedCommandDto {

    @NotNull(message = "Intenção é obrigatória")
    @NotBlank(message = "Intenção não pode estar vazia")
    private String intent;

    @NotBlank(message = "Nome do banco é obrigatório")
    private String bank;

    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    private BigDecimal amount;

    private Integer cycleDays;
}
