package com.ride_share.service;

import java.util.List;

import com.ride_share.entities.Notification;
import com.ride_share.entities.User;
import com.ride_share.playoads.NotificationDto;



public interface NotificationService {

void createNotification(Integer userId, String message);
	
    List<Notification> getUnreadNotificationsForUser(Integer userId);
    
    void markNotificationsAsRead(Integer userId); // optional if you want read marking

	List<NotificationDto> getAllNotificationsForUser(Integer userId);

	NotificationDto createNotification(Integer userId, NotificationDto notificationDto);

	//void notifyUser(User user, String message);

	
}
