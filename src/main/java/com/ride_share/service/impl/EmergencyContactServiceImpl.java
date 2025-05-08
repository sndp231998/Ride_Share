package com.ride_share.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride_share.entities.EmergencyContact;
import com.ride_share.entities.User;
import com.ride_share.entities.Vehicle;
import com.ride_share.exceptions.ResourceNotFoundException;
import com.ride_share.playoads.EmergencyContactDto;
import com.ride_share.playoads.VehicleDto;
import com.ride_share.repositories.EmergencyContactRepo;
import com.ride_share.repositories.UserRepo;
import com.ride_share.service.EmergencyContactService;
@Service
public class EmergencyContactServiceImpl implements EmergencyContactService {

    @Autowired
    private EmergencyContactRepo emergencyContactRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public EmergencyContactDto CreateEmergencyContact(EmergencyContactDto emergencyContactDto,Integer userId) {
    	 User user = userRepository.findById(userId)
                 .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
         
    	 EmergencyContact contact = modelMapper.map(emergencyContactDto, EmergencyContact.class);
        
        contact.setName(emergencyContactDto.getName());
        contact.setMobile(emergencyContactDto.getMobile());
        contact.setUser(user);

        EmergencyContact saved = emergencyContactRepository.save(contact);

        return modelMapper.map(saved,EmergencyContactDto.class);
       
    }

    @Override
    public void deleteEmergencyContact(Integer contactId) {
        EmergencyContact contact = emergencyContactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Emergency contact not found with ID: " + contactId));
        emergencyContactRepository.delete(contact);
    }

    @Override
    public List<EmergencyContactDto> getAllEmergencyContact() {
        return emergencyContactRepository.findAll()
                .stream()
                .map(this::emergencyContactToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmergencyContactDto> getEmergencyContactsByUser(Integer userId) {
    	 User user = this.userRepository.findById(userId)
                 .orElseThrow(() -> new ResourceNotFoundException("User ", "userId ", userId));
    	 List<EmergencyContact> contacts = this.emergencyContactRepository.findByUser(user);
       List<EmergencyContactDto> contactDtos = contacts.stream().map((emergencyContact) -> this.modelMapper.map(emergencyContact, EmergencyContactDto.class))
       .collect(Collectors.toList());	
       return contactDtos;
    }


    private EmergencyContact dtoToEmergencyContact(EmergencyContactDto emergencyContactDto) {
        return this.modelMapper.map(emergencyContactDto, EmergencyContact.class);
    }

    private EmergencyContactDto emergencyContactToDto(EmergencyContact contact) {
        return this.modelMapper.map(contact, EmergencyContactDto.class);
    }
}