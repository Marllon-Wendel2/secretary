package com.secretary.secretary.interfaces.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.secretary.secretary.application.dtos.ParsedCommandDto;
import com.secretary.secretary.application.services.CommandProcessorService;
import com.secretary.secretary.domain.model.Bank;
import com.secretary.secretary.infra.repositorys.BankRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/simulation")
@RequiredArgsConstructor
public class SimulationController {

    private final CommandProcessorService commandProcessorService;
    private final BankRepository bankRepository;

    @PostMapping("/setup-bank")
    public ResponseEntity<String> setupBank(@RequestParam String name) {
        if (bankRepository.findByNameIgnoreCaseAndActiveTrue(name).isPresent()) {
            return ResponseEntity.ok("Banco já cadastrado!");
        }
        Bank bank = Bank.builder().name(name).active(true).build();
        bankRepository.save(bank);
        return ResponseEntity.ok("Banco " + name + " cadastrado com ID: " + bank.getId());
    }

    @PostMapping("/command")
    public ResponseEntity<String> testCommand(@RequestBody ParsedCommandDto mockAiResponse) {
        try {
            String feedback = commandProcessorService.executeCommand(mockAiResponse);
            return ResponseEntity.ok(feedback);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao executar comando: " + e.getMessage());
        }
    }
}
