package com.secretary.secretary.infra.whatsapp;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WhatsappService {

    public void sendMessage(String to, String message) {
        log.info("[WHATSAPP MOCK API] Enviando para: {} | Mensagem: {}", to, message);
    }
}
