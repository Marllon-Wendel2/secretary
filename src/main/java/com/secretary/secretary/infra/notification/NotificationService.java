package com.secretary.secretary.infra.notification;

public interface NotificationService {
    void send(String to, String subject, String message);
}
