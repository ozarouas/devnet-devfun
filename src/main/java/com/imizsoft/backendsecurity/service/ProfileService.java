package com.imizsoft.backendsecurity.service;

import com.imizsoft.backendsecurity.model.User;
import com.imizsoft.backendsecurity.payload.ProfileRequest;
import com.imizsoft.backendsecurity.reprository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    private final UserRepository userRepository;

    @Autowired
    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ProfileRequest getProfile(String id){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new IllegalStateException("User with id: "+id+" is not found"));
        return new ProfileRequest(user.getId(), user.getFirstname(), user.getLastname(), user.getBirthdate(), user.getJob());
    }

    @Transactional
    public User updateProfile(ProfileRequest profile){
        if(userRepository.existsById(profile.getId())){
            User user = userRepository.findById(profile.getId())
                    .orElseThrow(()-> new IllegalStateException("User with id: "+profile.getId()+" is not found"));
            user.setFirstname(profile.getFirstname());
            user.setLastname(profile.getLastname());
            user.setBirthdate(profile.getBirthdate());
            user.setJob(profile.getJob());
            return userRepository.save(user);
        }
        return null;
    }

}
