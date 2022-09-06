package com.imizsoft.backendsecurity.controller;

import com.imizsoft.backendsecurity.model.User;
import com.imizsoft.backendsecurity.payload.MessageResponse;
import com.imizsoft.backendsecurity.payload.ProfileRequest;
import com.imizsoft.backendsecurity.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ProfileRequest> getProfile(@PathVariable("id") String id){
        ProfileRequest profile = profileService.getProfile(id);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<MessageResponse> updateProfile(@RequestBody ProfileRequest profile){
        User user = profileService.updateProfile(profile);
        if(user!=null)
            return ResponseEntity.ok(new MessageResponse(1, "Profile updated successfully"));
        return new ResponseEntity<>(new MessageResponse(0, "User not found"), HttpStatus.NOT_FOUND);
    }

}
