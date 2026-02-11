package com.example.demo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class LocationController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SmsService smsService;

    @Autowired
    private FCMService fcmService;

    // Replace with the device token of the recipient phone
    private final String deviceToken = "YOUR_DEVICE_FCM_TOKEN";

    // Method to send alerts
    private void sendFloodAlert(String location) {
        String msg = "🚨 FLOOD ALERT! Location: " + location;

        // Send SMS
        smsService.sendSms("YOUR_NUMBER", msg);
        smsService.sendSms("YOUR_NUMBER", msg);

        // Send push notification
        fcmService.sendPushNotification("Flood Alert", msg, deviceToken);


        // Send Email
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo("YOUR_EMAIL@gmail.com", "YOUR_EMAIL@gmail.com");
            mailMessage.setSubject("🚨 Flood Help Alert");
            mailMessage.setText("Emergency location received:\n" + msg);
            mailSender.send(mailMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/post-location")
    public String receiveLocation(@RequestBody Map<String, Object> payload) {
        String locationMessage;

        if (payload.containsKey("manualLocation")) {
            locationMessage = (String) payload.get("manualLocation");
        } else {
            double lat = Double.parseDouble(payload.get("latitude").toString());
            double lon = Double.parseDouble(payload.get("longitude").toString());

            // Call Nominatim API for reverse geocoding
            String url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" 
                         + lat + "&lon=" + lon + "&zoom=18&addressdetails=1";

            Map response = restTemplate.getForObject(url, Map.class);
            locationMessage = (String) response.get("display_name");
        }

        try {
            sendFloodAlert(locationMessage);
            return "✅ Alerts sent successfully! Location: " + locationMessage;
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Failed to send alerts: " + e.getMessage();
        }
    }
}
