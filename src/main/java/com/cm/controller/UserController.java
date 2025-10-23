package com.cm.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cm.entities.Contact;
import com.cm.entities.User;
import com.cm.helpers.Helper;
import com.cm.service.ContactService;
import com.cm.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final ContactService contactService;

    public UserController(UserService userService, ContactService contactService) {
        this.userService = userService;
        this.contactService = contactService;
    }

    // User dashboard page
    @RequestMapping("/dashboard")
    public String userDashboard(Model model, Authentication authentication) {
        
        try {
            // Get logged-in user email
            String username = Helper.getEmailOfLoggedInUser(authentication);
            logger.info("Dashboard accessed by user: {}", username);
            
            // Get user from database
            User user = userService.getUserByEmail(username);
            
            if (user != null) {
                // Get all contacts for this user using existing method
                List<Contact> allContacts = contactService.getByUserId(user.getUserId());
                
                // Calculate statistics
                int totalContacts = allContacts.size();
                
                // Count favorite contacts
                long favoriteContacts = allContacts.stream()
                    .filter(Contact::isFavorite)
                    .count();
                
                // Get recent contacts (last 5, sorted by newest first)
                List<Contact> recentContacts = allContacts.stream()
                    .sorted((c1, c2) -> {
                        // Sort by ID descending (assuming higher ID = newer)
                        if (c1.getId() != null && c2.getId() != null) {
                            return c2.getId().compareTo(c1.getId());
                        }
                        return 0;
                    })
                    .limit(5)
                    .collect(Collectors.toList());
                
                // Add data to model
                model.addAttribute("contactsCount", totalContacts);
                model.addAttribute("favoriteContactsCount", favoriteContacts);
                model.addAttribute("recentContacts", recentContacts);
                
                logger.info("Dashboard stats - Total: {}, Favorites: {}, Recent: {}", 
                    totalContacts, favoriteContacts, recentContacts.size());
            } else {
                // Default values if user not found
                model.addAttribute("contactsCount", 0);
                model.addAttribute("favoriteContactsCount", 0);
                model.addAttribute("recentContacts", List.of());
                
                logger.warn("User not found: {}", username);
            }
        } catch (Exception e) {
            logger.error("Error loading dashboard: ", e);
            // Set default values on error
            model.addAttribute("contactsCount", 0);
            model.addAttribute("favoriteContactsCount", 0);
            model.addAttribute("recentContacts", List.of());
        }
        
        return "user/dashboard";
    }

    @RequestMapping("/profile")
    public String userProfile(Model model, Authentication authentication) {
        return "user/profile";
    }

    

    // user add contacts page
    // user view contacts page
    // user edit contacts page
    // user delete contacts page
}
