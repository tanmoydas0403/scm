package com.cm.controller;

import org.slf4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.cm.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    

    //user dashboard page

    @RequestMapping("/dashboard")
    public String userDashboard(){
        return "user/dashboard";
    }

    @RequestMapping("/profile")
    public String userProfile(Model model, Authentication authentication){
        
        

        return "user/profile";
    }

    //user add contacts page

    //user view contacts page

    //user edit contacts page

    //user delete contacts page
}
