package com.secretary.secretary.infra.schedulers;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.secretary.secretary.domain.model.CreditCycle;
import com.secretary.secretary.domain.model.CycleStatus;
import com.secretary.secretary.infra.repositorys.CreditCycleRepository;
import com.secretary.secretary.infra.whatsapp.WhatsappService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyCycleScheduler {

    private final CreditCycleRepository cycleRepository;
    private final WhatsappService whatsappService;

    @Scheduled(cron = "0 0 7 * * *")
    @Transactional
    public void processFinancialCycle() {
        log.info("Iniciando rotina automática diária de análise de ciclos financeiros...");
        LocalDate today = LocalDate.now();

        List<CreditCycle> activeCycles = cycleRepository.findByStatusIn(
                List.of(CycleStatus.ACTIVE, CycleStatus.CLOSE_TO_EXPIRY, CycleStatus.OVERDUE));

        for (CreditCycle cycle : activeCycles) {
            cycle.updateStatusBasedOnDate(today);
            cycleRepository.save(cycle);

            long daysLeft = ChronoUnit.DAYS.between(today, cycle.getAdjustedEndDate());

            if (daysLeft == 3 || daysLeft == 1 || daysLeft == 0) {
                sendAlertMessage(cycle, daysLeft);
            }
        }

        log.info("Rotina diária de ciclos financeiros finalizada.");
    }

    private void sendAlertMessage(CreditCycle cycle, long daysLeft) {
        String message;
        if (daysLeft == 0) {
            message = String.format(
                    "*VENCIMENTO HOJE!* O ciclo do %s no valor de R$ %s vence hoje (%s). Faça a rotação de caixa imediatamente para evitar juros!",
                    cycle.getBank().getName(), cycle.getAmount(), cycle.getAdjustedEndDate());
        } else {
            message = String.format(
                    "*ALERTA:* Faltam %d dia(s) para o vencimento do ciclo no %s (Valor: R$ %s). Vencimento real em: %s.",
                    daysLeft, cycle.getBank().getName(), cycle.getAmount(), cycle.getAdjustedEndDate());
        }

        // Número de destino fixado para o administrador do sistema no MVP
        whatsappService.sendMessage("5511999999999", message);
    }

}
