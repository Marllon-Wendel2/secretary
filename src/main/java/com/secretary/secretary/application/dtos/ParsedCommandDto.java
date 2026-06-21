package com.secretary.secretary.application.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedCommandDto {
    private String intent;
    private String bank;
    private BigDecimal amount;
    private Integer cycleDays;
}
