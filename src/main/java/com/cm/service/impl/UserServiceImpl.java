package com.cm.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.cm.helpers.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cm.entities.User;
import com.cm.helpers.ResourceNotFoundException;
import com.cm.repository.UserRepository;
import com.cm.service.UserService;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepo;


    @Override
    public User saveUser(User user) {
        //UserId is auto generated
        String userId = UUID.randomUUID().toString();
        user.setUserId(userId);

        //set Password encoded
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        //user set roles
        user.setRoles(List.of(AppConstants.ROLE_USER));

        return userRepo.save(user);
    }

    @Override
    public Optional<User> getUserById(String id) {
        return userRepo.findById(id);
    }

    @Override
    public Optional<User> updateUser(User user) {
        User user2 = userRepo.findById(user.getUserId())
        .orElseThrow(()-> new ResourceNotFoundException("User not found "));
        user2.setName(user.getName());
        user2.setEmail(user.getEmail());
        user2.setPassword(user.getPassword());
        user2.setPhoneNumber(user.getPhoneNumber());
        user2.setAbout(user.getAbout());
        user2.setProfilePic(user.getProfilePic());
        user2.setEnabled(user.isEnabled());
        user2.setEmailVerified(user.isEmailVerified());
        user2.setPhoneVerified(user.isPhoneVerified());
        user2.setProvider(user.getProvider());
        user2.setProviderUserId(user.getProviderUserId());

        // return Optional.of(userRepo.save(user2));
        User save= userRepo.save(user2);
        return Optional.ofNullable(save);
    }

    @Override
    public void deleteUser(String id) {
        User user2 = userRepo.findById(id)
        .orElseThrow(()-> new ResourceNotFoundException("User not found "));
        userRepo.delete(user2);
    }

    @Override
    public boolean isUserExist(String userId) {
        User user2 = userRepo.findById(userId).orElse(null);
        return user2!=null ? true : false;
    }

    @Override
    public boolean isUserExistByEmail(String email) {
        User user = userRepo.findByEmail(email).orElse(null);
        return user!=null ? true : false;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }

}
