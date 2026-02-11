package com.example.demo;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class FCMService {

    public void sendPushNotification(String title, String body, String token) {
        try {
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setToken(token)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ FCM sent successfully: " + response);
        } catch (Exception e) {
            System.err.println("❌ Failed to send FCM: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
