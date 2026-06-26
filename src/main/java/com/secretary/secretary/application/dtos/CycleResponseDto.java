package com.secretary.secretary.application.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CycleResponseDto {
    private Long id;
    private String bankName;
    private BigDecimal amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate adjustedEndDate;
    private String status;
}
