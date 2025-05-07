package com.ride_share.service;

import java.util.List;

import com.ride_share.entities.Notification;
import com.ride_share.entities.User;
import com.ride_share.playoads.NotificationDto;



public interface NotificationService {

	public NotificationDto createNotification(NotificationDto notificationDto,Integer userId);
	
    List<Notification> getUnreadNotificationsForUser(Integer userId);
    
  

	List<NotificationDto> getAllNotificationsForUser(Integer userId);
	 void markNotificationsAsRead(Integer notificationId);

	//NotificationDto createNotification(Integer userId, NotificationDto notificationDto);

	//void notifyUser(User user, String message);

	
}
