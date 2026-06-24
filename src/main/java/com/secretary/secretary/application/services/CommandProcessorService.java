package com.secretary.secretary.application.services;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.secretary.secretary.application.dtos.ParsedCommandDto;
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

    private final BankRepository bankRepository;
    private final CreditCycleRepository creditCycleRepository;
    private final NotificationService notificationService;

    @Value("${secretary.mail.admin-email}")
    private String adminEmail;

    @Transactional
    public String executeCommand(ParsedCommandDto command) {
        if (command.getIntent() == null) {
            return "Intenção de comando não especificada de forma válida.";
        }

        switch (command.getIntent().toUpperCase()) {
            case "CREATE_CYCLE":
                if (command.getBank() == null || command.getAmount() == null) {
                    return "Dados insuficientes para criar um ciclo. Certifique-se de informar o banco e o valor.";
                }

                Bank bank = bankRepository.findByNameIgnoreCaseAndActiveTrue(command.getBank())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Banco '" + command.getBank() + "' não encontrado ou inativo no sistema."));

                // Instancia o ciclo de crédito (A data e o status serão inferidos no ciclo de
                // persistência)
                CreditCycle cycle = CreditCycle.builder()
                        .bank(bank)
                        .amount(command.getAmount())
                        .startDate(LocalDate.now())
                        .status(CycleStatus.ACTIVE)
                        .build();

                creditCycleRepository.save(cycle);

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

                return String.format(
                        "Ciclo de R$ %s criado com sucesso para o %s. Vencimento padrão: %s | Vencimento real (antecipado se fim de semana): *%s*",
                        cycle.getAmount(), bank.getName(), cycle.getEndDate(), cycle.getAdjustedEndDate());

            case "CHECK_STATUS":
                return "Consulta de estados pendente de implementação de layout.";

            default:
                return "Intenção de comando não reconhecida pelo sistema.";
        }
    }
}
