package com.ride_share.playoads;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ride_share.entities.User;

@Service
public class FirebaseNotification {

    @Autowired
    private FirebaseMessaging firebaseMessaging;

    public void notifyUser(User user, String message) {
        if (user.getDeviceToken() == null) {
            throw new RuntimeException("Device token not found for user id: " + user.getId());
        } else {
            Message fcmMessage = Message.builder()
                .setToken(user.getDeviceToken())
                .setNotification(Notification.builder()
                    .setTitle("New Notification")
                    .setBody(message)
                    .build())
                .build();

            try {
                String response = firebaseMessaging.send(fcmMessage);
                System.out.println("Push notification sent: " + response);
            } catch (FirebaseMessagingException e) {
                throw new RuntimeException("Failed to send notification", e);
            }
        }
    }
}