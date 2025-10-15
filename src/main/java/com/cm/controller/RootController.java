package com.cm.controller;

import org.slf4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.cm.entities.User;
import com.cm.helpers.Helper;
import com.cm.service.UserService;

@ControllerAdvice
public class RootController {

    Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    private final UserService userService;

    public RootController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void addLoggedInUserInformation(Model model, Authentication authentication) {
        
        if (authentication == null) {
            return;
        }
        
        System.out.println("Adding logged in user information to the model");
        
        String username = Helper.getEmailOfLoggedInUser(authentication);

        logger.info("User logged in: {}", username);
        
        User user = userService.getUserByEmail(username);
        
        System.out.println(user);
        System.out.println(user.getName());
        System.out.println(user.getEmail());

        model.addAttribute("loggedInUser", user);
    }
}
