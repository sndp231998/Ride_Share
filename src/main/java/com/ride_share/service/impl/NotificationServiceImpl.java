package com.ride_share.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride_share.entities.Notification;
import com.ride_share.entities.User;
import com.ride_share.exceptions.ResourceNotFoundException;
import com.ride_share.playoads.NotificationDto;
//import com.ride_share.payloads.NotificationDto;
import com.ride_share.repositories.NotificationRepo;
import com.ride_share.repositories.UserRepo;
//import com.ride_share.services.NotificationService;
import com.ride_share.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    
  //call from other class
    //create notification for specific user
    @Override
    public void createNotification(Integer userId, String message) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notificationRepo.save(notification);
    }
    
    // create notification for all user 
    @Override
    public NotificationDto createNotification(Integer userId, NotificationDto notificationDto) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Notification notification = this.modelMapper.map(notificationDto, Notification.class);
       
        notification.setUser(user);
        notification.setMessage(notificationDto.getMessage());
        Notification not=this.notificationRepo.save(notification);
        NotificationDto savedAotDto = this.modelMapper.map(not, NotificationDto.class);
        
        return savedAotDto;
    }

 
    @Override
    public List<Notification> getUnreadNotificationsForUser(Integer userId) {
        return notificationRepo.findByUserIdAndIsReadFalse(userId);
    }

    @Override
    public void markNotificationsAsRead(Integer userId) {
        List<Notification> notifications = notificationRepo.findByUserId(userId);
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepo.saveAll(notifications);
    }


   
    @Override
    public List<NotificationDto> getAllNotificationsForUser(Integer userId) {
        return notificationRepo.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notification -> modelMapper.map(notification, NotificationDto.class))
                .collect(Collectors.toList());
    }



	
}