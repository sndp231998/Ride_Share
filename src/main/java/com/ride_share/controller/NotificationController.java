package com.ride_share.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ride_share.entities.Notification;
import com.ride_share.playoads.NotificationDto;

import com.ride_share.service.NotificationService;

public class NotificationController {
	@Autowired
    private NotificationService notificationService;

    
    @PostMapping("/user/{userId}")
    public ResponseEntity<NotificationDto> createNotification(
            @PathVariable Integer userId, 
            @RequestBody NotificationDto notificationDto) {
        
        NotificationDto createdNotification = notificationService.createNotification(userId, notificationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotification);
    }
  
    
    // Endpoint to get all notifications for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getAllNotificationForUser(@PathVariable Integer userId) {
        List<Notification> notifications = notificationService.getUnreadNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }

    // Endpoint to get only unread notifications for a user
 // Endpoint to get only unread notifications for a user
 // Endpoint to get only unread notifications for a user
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotificationsForUser(@PathVariable Integer userId) {
        List<Notification> unreadNotifications = notificationService.getUnreadNotificationsForUser(userId);
        return ResponseEntity.ok(unreadNotifications);
    }



    // Endpoint to mark all notifications as read for a user
    @PostMapping("/user/{userId}/mark-read")
    public ResponseEntity<Void> markNotificationsAsRead(@PathVariable Integer userId) {
        notificationService.markNotificationsAsRead(userId);
        return ResponseEntity.ok().build();
    }

    // Endpoint to get the count of unread notifications for a user
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadNotificationCount(@PathVariable Integer userId) {
        long unreadCount = notificationService.getUnreadNotificationsForUser(userId).stream().count();
        return ResponseEntity.ok(unreadCount);
    }
    
 // Endpoint to get all notifications for a user, including read and unread
    @GetMapping("/user/{userId}/all")
    public ResponseEntity<List<NotificationDto>> getAllNotificationsForUser(@PathVariable Integer userId) {
    	 List<NotificationDto> allNotifications = notificationService.getAllNotificationsForUser(userId);
         return new ResponseEntity<>(allNotifications, HttpStatus.OK);
    }

}
