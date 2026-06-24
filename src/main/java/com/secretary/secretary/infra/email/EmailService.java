package com.secretary.secretary.infra.email;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String htmlBody) throws MessagingException {
        if (to == null || to.isBlank()) {
            log.warn("[EMAIL] Destinatário vazio. Email não enviado. Assunto: {}", subject);
            return;
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("secretary@system.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        log.info("[EMAIL] Enviando para: {} | Assunto: {}", to, subject);
        mailSender.send(message);
        log.info("[EMAIL] Enviado com sucesso para: {}", to);
    }
}
