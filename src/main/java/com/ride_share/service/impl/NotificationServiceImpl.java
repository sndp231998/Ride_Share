package com.ride_share.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride_share.entities.Notification;
import com.ride_share.entities.User;
import com.ride_share.exceptions.ApiException;
import com.ride_share.exceptions.ResourceNotFoundException;
import com.ride_share.playoads.FirebaseNotification;
import com.ride_share.playoads.NotificationDto;

import com.ride_share.repositories.NotificationRepo;
import com.ride_share.repositories.UserRepo;

import com.ride_share.service.NotificationService;


@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FirebaseNotification firebaseNotification;

    
  //call from other class
    //create notification for specific user
    @Override
    public NotificationDto createNotification(NotificationDto notificationDto, Integer userId) {
        // Check if message is empty or null
        if (notificationDto.getMessage() == null || notificationDto.getMessage().trim().isEmpty()) {
            throw new ApiException("Message cannot be empty");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
//
//        if(user.getDeviceToken()==null) {
//        	throw new ApiException("DeviceToken is empty");
//        }
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(notificationDto.getMessage());

        try {
            firebaseNotification.notifyUser(user, notificationDto.getMessage());
        } catch (RuntimeException e) {
        	System.out.println("Push failed: " + e.getMessage());
           // throw new ApiException("Push failed: " + e.getMessage());
        }

        Notification savedNotification = notificationRepo.save(notification);
        return modelMapper.map(savedNotification, NotificationDto.class);
    }

    
    // create notification for all user 
//    @Override
//    public NotificationDto createNotification(Integer userId, NotificationDto notificationDto) {
//        User user = userRepo.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
//        
//        Notification notification = this.modelMapper.map(notificationDto, Notification.class);
//       
//        notification.setUser(user);
//        notification.setMessage(notificationDto.getMessage());
//        Notification not=this.notificationRepo.save(notification);
//        NotificationDto savedAotDto = this.modelMapper.map(not, NotificationDto.class);
//        
//        return savedAotDto;
//    }

 
    @Override
    public List<Notification> getUnreadNotificationsForUser(Integer userId) {
        return notificationRepo.findByUserIdAndIsReadFalse(userId);
    }

    @Override
    public void markNotificationsAsRead(Integer notificationId) {
    	
    	 Notification notification = notificationRepo.findById(notificationId)
                 .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        
        notification.setRead(true);
        notificationRepo.save(notification);
    }


   
    @Override
    public List<NotificationDto> getAllNotificationsForUser(Integer userId) {
        return notificationRepo.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notification -> modelMapper.map(notification, NotificationDto.class))
                .collect(Collectors.toList());
    }





}