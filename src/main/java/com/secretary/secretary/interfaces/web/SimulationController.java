package com.secretary.secretary.interfaces.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.secretary.secretary.application.dtos.BankResponseDto;
import com.secretary.secretary.application.dtos.CommandResponseDto;
import com.secretary.secretary.application.dtos.ParsedCommandDto;
import com.secretary.secretary.application.services.CommandProcessorService;
import com.secretary.secretary.domain.exceptions.ResourceAlreadyExistsException;
import com.secretary.secretary.domain.model.Bank;
import com.secretary.secretary.infra.repositorys.BankRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/simulation")
@RequiredArgsConstructor
public class SimulationController {

    private final CommandProcessorService commandProcessorService;
    private final BankRepository bankRepository;

    @PostMapping("/setup-bank")
    public ResponseEntity<BankResponseDto> setupBank(@RequestParam String name) {
        if (bankRepository.findByNameIgnoreCaseAndActiveTrue(name).isPresent()) {
            throw new ResourceAlreadyExistsException("Banco", "nome", name);
        }
        Bank bank = Bank.builder().name(name).active(true).build();
        bankRepository.save(bank);

        BankResponseDto response = BankResponseDto.builder()
                .id(bank.getId())
                .name(bank.getName())
                .active(bank.isActive())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/command")
    public ResponseEntity<CommandResponseDto> testCommand(@Valid @RequestBody ParsedCommandDto mockAiResponse) {
        CommandResponseDto response = commandProcessorService.executeCommand(mockAiResponse);
        return ResponseEntity.ok(response);
    }
}
