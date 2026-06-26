package com.secretary.secretary.infra.notification;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.secretary.secretary.domain.exceptions.NotificationException;
import com.secretary.secretary.infra.email.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService implements NotificationService {

    private final EmailService emailService;

    @Override
    public void send(String to, String subject, String message) {
        String htmlBody = """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"></head>
                <body style="font-family: Arial, sans-serif; padding: 20px; background-color: #f5f5f5;">
                    <div style="max-width: 600px; margin: 0 auto; background: white; border-radius: 8px; padding: 24px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                        <h2 style="color: #333; border-bottom: 2px solid #4CAF50; padding-bottom: 12px;">Secretary - Alerta Financeiro</h2>
                        <div style="color: #555; font-size: 16px; line-height: 1.6;">
                            %s
                        </div>
                        <hr style="margin: 20px 0; border: none; border-top: 1px solid #eee;">
                        <p style="color: #999; font-size: 12px;">Sistema Secretary - Controle de Ciclos Financeiros</p>
                    </div>
                </body>
                </html>
                """.formatted(message);
        try {
            emailService.sendEmail(to, subject, htmlBody);
            log.info("Email enviado com sucesso para {}", to);
        } catch (Exception e) {
            throw new NotificationException("email", to, e);
        }
    }
}
