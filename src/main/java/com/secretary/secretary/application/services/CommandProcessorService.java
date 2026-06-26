package com.secretary.secretary.application.services;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.secretary.secretary.application.dtos.CommandResponseDto;
import com.secretary.secretary.application.dtos.CycleResponseDto;
import com.secretary.secretary.application.dtos.ParsedCommandDto;
import com.secretary.secretary.domain.exceptions.InvalidCommandException;
import com.secretary.secretary.domain.exceptions.ResourceNotFoundException;
import com.secretary.secretary.domain.model.Bank;
import com.secretary.secretary.domain.model.CreditCycle;
import com.secretary.secretary.domain.model.CycleStatus;
import com.secretary.secretary.infra.notification.NotificationService;
import com.secretary.secretary.infra.repositorys.BankRepository;
import com.secretary.secretary.infra.repositorys.CreditCycleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommandProcessorService {

    private static final Logger log = LoggerFactory.getLogger(CommandProcessorService.class);

    private final BankRepository bankRepository;
    private final CreditCycleRepository creditCycleRepository;
    private final NotificationService notificationService;

    @Value("${secretary.mail.admin-email}")
    private String adminEmail;

    @Transactional
    public CommandResponseDto executeCommand(ParsedCommandDto command) {
        if (command.getIntent() == null) {
            throw new InvalidCommandException("Intenção de comando não especificada de forma válida.");
        }

        String intent = command.getIntent().toUpperCase();
        log.info("Processando comando com intenção: {}", intent);

        return switch (intent) {
            case "CREATE_CYCLE" -> handleCreateCycle(command);
            case "CHECK_STATUS" -> CommandResponseDto.builder()
                    .intent(intent)
                    .message("Consulta de estados pendente de implementação de layout.")
                    .build();
            default -> throw new InvalidCommandException(intent, "Intenção de comando não reconhecida pelo sistema.");
        };
    }

    private CommandResponseDto handleCreateCycle(ParsedCommandDto command) {
        if (command.getBank() == null || command.getAmount() == null) {
            throw new InvalidCommandException("CREATE_CYCLE",
                    "Dados insuficientes para criar um ciclo. Certifique-se de informar o banco e o valor.");
        }

        Bank bank = bankRepository.findByNameIgnoreCaseAndActiveTrue(command.getBank())
                .orElseThrow(() -> new ResourceNotFoundException("Banco", "nome", command.getBank()));

        CreditCycle cycle = CreditCycle.builder()
                .bank(bank)
                .amount(command.getAmount())
                .startDate(LocalDate.now())
                .status(CycleStatus.ACTIVE)
                .build();

        creditCycleRepository.save(cycle);
        log.info("Ciclo criado com sucesso - ID: {}, Banco: {}, Valor: R$ {}",
                cycle.getId(), bank.getName(), cycle.getAmount());

        sendCycleCreatedNotification(bank, cycle);

        CycleResponseDto cycleDto = CycleResponseDto.builder()
                .id(cycle.getId())
                .bankName(bank.getName())
                .amount(cycle.getAmount())
                .startDate(cycle.getStartDate())
                .endDate(cycle.getEndDate())
                .adjustedEndDate(cycle.getAdjustedEndDate())
                .status(cycle.getStatus().name())
                .build();

        return CommandResponseDto.builder()
                .intent("CREATE_CYCLE")
                .message(String.format(
                        "Ciclo de R$ %s criado com sucesso para o %s.",
                        cycle.getAmount(), bank.getName()))
                .cycle(cycleDto)
                .build();
    }

    private void sendCycleCreatedNotification(Bank bank, CreditCycle cycle) {
        try {
            notificationService.send(
                    adminEmail,
                    "Secretary - Novo Ciclo Criado",
                    String.format(
                            "Novo ciclo criado com sucesso!<br><br>" +
                                    "<strong>Banco:</strong> %s<br>" +
                                    "<strong>Valor:</strong> R$ %s<br>" +
                                    "<strong>Data Início:</strong> %s<br>" +
                                    "<strong>Vencimento:</strong> %s<br>" +
                                    "<strong>Vencimento Real (ajustado):</strong> %s",
                            bank.getName(), cycle.getAmount(), cycle.getStartDate(),
                            cycle.getEndDate(), cycle.getAdjustedEndDate())
            );
        } catch (Exception e) {
            log.error("Ciclo criado, mas falha ao enviar notificação: {}", e.getMessage(), e);
        }
    }
}
