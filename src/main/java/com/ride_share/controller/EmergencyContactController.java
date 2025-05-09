package com.ride_share.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ride_share.playoads.ApiResponse;
import com.ride_share.playoads.EmergencyContactDto;
import com.ride_share.service.EmergencyContactService;

@RestController
@RequestMapping("/api/v1/emergencycontact")
public class EmergencyContactController {

    @Autowired
    private EmergencyContactService emergencyContactService;

    // Create emergency contact for a user
    @PostMapping("/user/{userId}")
    public ResponseEntity<EmergencyContactDto> createEmergencyContact(
            @RequestBody EmergencyContactDto contactDto,
            @PathVariable Integer userId) {

        EmergencyContactDto created = emergencyContactService.CreateEmergencyContact(contactDto, userId);
        return ResponseEntity.ok(created);
    }

    // Get all emergency contacts
    @GetMapping
    public ResponseEntity<List<EmergencyContactDto>> getAllContacts() {
        List<EmergencyContactDto> contacts = emergencyContactService.getAllEmergencyContact();
        return ResponseEntity.ok(contacts);
    }

    // Get emergency contacts by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EmergencyContactDto>> getContactsByUser(@PathVariable Integer userId) {
        List<EmergencyContactDto> contacts = emergencyContactService.getEmergencyContactsByUser(userId);
        return ResponseEntity.ok(contacts);
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<ApiResponse> deleteEmergencyContact(@PathVariable Integer contactId) {
        emergencyContactService.deleteEmergencyContact(contactId);
        return ResponseEntity.ok(new ApiResponse("Emergency contact deleted successfully", true));
    }

}
