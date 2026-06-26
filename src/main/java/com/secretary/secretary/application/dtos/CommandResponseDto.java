package com.secretary.secretary.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandResponseDto {
    private String intent;
    private String message;
    private CycleResponseDto cycle;
}
