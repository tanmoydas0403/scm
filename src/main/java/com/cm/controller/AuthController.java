package com.cm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cm.entities.User;
import com.cm.helpers.Message;
import com.cm.helpers.MessageType;
import com.cm.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    //verify email
    @GetMapping("/verify-email")
    public String verifyEmail(
        @RequestParam String token,
        HttpSession session
        ) {
        User user = userRepo.findByEmailToken(token).orElse(null);

        if (user != null) {
            if (user.getEmailToken().equals(token)) {
                user.setEmailVerified(true);
                user.setEnabled(true);
                userRepo.save(user);

                session.setAttribute("message", 
                Message.builder()
                .type(MessageType.green)
                .content("Email verified successfully")
                .build()
                );
                return "success_page";
            } 

            session.setAttribute("message", 
            Message.builder()
            .type(MessageType.red)
            .content("Invalid token")
            .build()
            );

            return "error_page";  
        }

        session.setAttribute("message", 
        Message.builder()
        .type(MessageType.red)
        .content("Invalid token")
        .build()
        );

        return "error_page";
    }

}
