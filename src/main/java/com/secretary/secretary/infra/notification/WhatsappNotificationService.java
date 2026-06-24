package com.secretary.secretary.infra.notification;

import org.springframework.stereotype.Component;

import com.secretary.secretary.infra.whatsapp.WhatsappService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WhatsappNotificationService implements NotificationService {

    private final WhatsappService whatsappService;

    @Override
    public void send(String to, String subject, String message) {
        whatsappService.sendMessage(to, message);
    }
}
