package com.cm.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cm.entities.Contact;
import com.cm.entities.User;
import com.cm.forms.ContactForm;
import com.cm.helpers.AppConstants;
import com.cm.helpers.Helper;
import com.cm.helpers.Message;
import com.cm.helpers.MessageType;
import com.cm.helpers.ResourceNotFoundException;
import com.cm.repository.ContactRepo;
import com.cm.service.ContactService;
import com.cm.service.ImageService;
import com.cm.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;
    @Autowired
    private UserService userService;

    @Autowired
    private ContactRepo contactRepo;

    @Autowired
    private  ImageService imageService;

    private Logger logger = LoggerFactory.getLogger(ContactController.class);
    
    //add contact page handler
    @RequestMapping("/add")
    public String addContactView(Model model){
        // Only create new form if not already present (to preserve form data with errors)
        if (!model.containsAttribute("contactForm")) {
            ContactForm contactForm = new ContactForm();
            model.addAttribute("contactForm", contactForm);
        }
        return "user/add_contact";
    }

    @PostMapping("/add")
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, 
                            BindingResult result, 
                            Authentication authentication, 
                            Model model,
                            HttpSession session) {

        // validate form data
        if (result.hasErrors()) {
            // Return to the same form with error messages
            session.setAttribute("message", Message.builder()
                                                        .content("Contact saving failed")
                                                        .type(MessageType.red)
                                                    .build());
            return "user/add_contact";
        }
        
        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);

        logger.info("=== saveContact method STARTED =========================");
        
        // SAFELY HANDLE IMAGE UPLOAD - FIX FOR 500 ERROR
        String fileURL;
        String filename;

        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            // Process the uploaded image
            logger.info("File information: {}", contactForm.getContactImage().getOriginalFilename());
            filename = UUID.randomUUID().toString();
            fileURL = imageService.uploadImage(contactForm.getContactImage(), filename);
            logger.info("Image uploaded successfully: {}", fileURL);
        } else {
            // Use default image when no file is uploaded
            logger.info("No image uploaded, using default picture");
            filename = "default";
            fileURL = "https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg";
            logger.info("Default image set: {}", fileURL);
        }
        
        Contact contact = new Contact();
        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setFavorite(contactForm.isFavorite());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setLinkedInLink(contactForm.getLinkedInLink());
        contact.setPicture(fileURL);
        contact.setCloudinaryImagePublicId(filename);
        contact.setUser(user);

        try {
            contactService.save(contact);
            logger.info("Contact saved successfully with ID: {}", contact.getId());
            
            session.setAttribute("message", Message.builder()
                                                        .content("Contact saved successfully")
                                                        .type(MessageType.green)
                                                    .build());

            return "redirect:/user/contacts/add?success";
        } catch (Exception e) {
            logger.error("Error saving contact: {}", e.getMessage(), e);
            session.setAttribute("message", Message.builder()
                                                        .content("Error saving contact: " + e.getMessage())
                                                        .type(MessageType.red)
                                                    .build());
            return "user/add_contact";
        }
    }


    //search handler

   @GetMapping("/search")
    public String searchHandler(
        @RequestParam("field") String field,
        @RequestParam("keyword") String value,
        @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
        @RequestParam(value = "direction", defaultValue = "asc") String direction,
        Authentication authentication,
        Model model
    ) {
        logger.info("Search - field: {}, keyword: {}, page: {}", field, value, page);
        
        // Get current user
        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);
        
        Page<Contact> pageContact = null;
        
        if (field.equalsIgnoreCase("name")) {
            pageContact = contactService.searchByName(value, user, size, page, sortBy, direction);
        } else if (field.equalsIgnoreCase("email")) {
            pageContact = contactService.searchByEmail(value, user, size, page, sortBy, direction);
        } else if (field.equalsIgnoreCase("phone")) {
            pageContact = contactService.searchByPhoneNumber(value, user, size, page, sortBy, direction);
        } else {
            // Default fallback - search in name
            pageContact = contactService.searchByName(value, user, size, page, sortBy, direction);
        }
        
        model.addAttribute("pageContact", pageContact);
        model.addAttribute("searchField", field);
        model.addAttribute("searchKeyword", value);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);
        
        logger.info("Search results: {} contacts found", pageContact.getTotalElements());
        
        return "user/contacts";
    }

    // Update the main contacts view to handle search
    @RequestMapping
    public String viewContacts(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
        @RequestParam(value = "sortField", defaultValue = "name") String sortField,
        @RequestParam(value = "direction", defaultValue = "asc") String direction,
        @RequestParam(value = "field", required = false) String searchField,
        @RequestParam(value = "keyword", required = false) String searchKeyword,
        Model model, 
        Authentication authentication
    ) {
        // Load the user
        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);

        Page<Contact> pageContacts;
        
        // If search parameters are provided, perform search
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            if (searchField == null || searchField.isEmpty()) {
                searchField = "name"; // default search field
            }
            
            if (searchField.equalsIgnoreCase("name")) {
                pageContacts = contactService.searchByName(searchKeyword, user, size, page, sortField, direction);
            } else if (searchField.equalsIgnoreCase("email")) {
                pageContacts = contactService.searchByEmail(searchKeyword, user, size, page, sortField, direction);
            } else if (searchField.equalsIgnoreCase("phone")) {
                pageContacts = contactService.searchByPhoneNumber(searchKeyword, user, size, page, sortField, direction);
            } else {
                pageContacts = contactService.searchByName(searchKeyword, user, size, page, sortField, direction);
            }
            
            model.addAttribute("searchField", searchField);
            model.addAttribute("searchKeyword", searchKeyword);
        } else {
            // Normal pagination without search
            pageContacts = contactService.getByUser(user, page, size, sortField, direction);
        }
        
        model.addAttribute("pageContact", pageContacts);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);
        return "user/contacts";
    }

    // Delete contact via API
    @DeleteMapping("/delete/{contactId}")
    public ResponseEntity<String> deleteContact(@PathVariable String contactId) {
        try {
            System.out.println("🚀 DELETE endpoint called for contact ID: " + contactId);
            
            // Check if contact exists first
            boolean exists = contactRepo.existsById(contactId);
            System.out.println("✅ Contact exists in database: " + exists);
            
            if (!exists) {
                System.out.println("❌ Contact not found in database");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contact not found");
            }
            
            System.out.println("🗑️ Calling contactService.delete()...");
            contactService.delete(contactId);
            System.out.println("✅ Contact service deletion completed");
            
            // Double check if contact was actually deleted
            boolean stillExists = contactRepo.existsById(contactId);
            System.out.println("📊 Contact still exists after delete: " + stillExists);
            
            return ResponseEntity.ok("Contact deleted successfully");
        } catch (ResourceNotFoundException ex) {
            System.out.println("❌ ResourceNotFoundException: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contact not found");
        } catch (Exception ex) {
            System.out.println("💥 ERROR deleting contact: " + ex.getMessage());
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting contact");
        }
    }

    // Add this method to your ContactController
    @GetMapping("/api/contact/{contactId}")
    public ResponseEntity<Contact> getContactById(@PathVariable String contactId) {
        try {
            Contact contact = contactService.getById(contactId);
            return ResponseEntity.ok(contact);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Add this method to your ContactController
    @PostMapping("/update/{contactId}")
    public String updateContact(@PathVariable String contactId,
                            @Valid @ModelAttribute ContactForm contactForm,
                            BindingResult result,
                            Authentication authentication,
                            Model model,
                            HttpSession session) {

        logger.info("=== updateContact method STARTED =========================");
        logger.info("Updating contact with ID: {}", contactId);

        if (result.hasErrors()) {
            session.setAttribute("message", Message.builder()
                    .content("Contact update failed - validation errors")
                    .type(MessageType.red)
                    .build());
            logger.error("Validation errors in update form");
            return "redirect:/user/contacts?error";
        }

        try {
            // Get existing contact
            Contact existingContact = contactService.getById(contactId);
            logger.info("Found existing contact: {}", existingContact.getName());
            
            // Get current user
            String username = Helper.getEmailOfLoggedInUser(authentication);
            User user = userService.getUserByEmail(username);
            
            // Handle image upload if new image is provided
            String fileURL = existingContact.getPicture();
            String filename = existingContact.getCloudinaryImagePublicId();

            if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
                logger.info("New image uploaded for contact update");
                // Process the uploaded image
                filename = UUID.randomUUID().toString();
                fileURL = imageService.uploadImage(contactForm.getContactImage(), filename);
                logger.info("New image uploaded successfully: {}", fileURL);
            }
            
            // Create updated contact object
            Contact updatedContact = new Contact();
            updatedContact.setId(contactId);
            updatedContact.setName(contactForm.getName());
            updatedContact.setEmail(contactForm.getEmail());
            updatedContact.setPhoneNumber(contactForm.getPhoneNumber());
            updatedContact.setAddress(contactForm.getAddress());
            updatedContact.setDescription(contactForm.getDescription());
            updatedContact.setFavorite(contactForm.isFavorite());
            updatedContact.setWebsiteLink(contactForm.getWebsiteLink());
            updatedContact.setLinkedInLink(contactForm.getLinkedInLink());
            updatedContact.setPicture(fileURL);
            updatedContact.setCloudinaryImagePublicId(filename);
            updatedContact.setUser(user);
            updatedContact.setLinks(existingContact.getLinks()); // Preserve existing links

            // Update the contact
            contactService.update(updatedContact);
            logger.info("Contact updated successfully with ID: {}", contactId);
            
            session.setAttribute("message", Message.builder()
                    .content("Contact updated successfully")
                    .type(MessageType.green)
                    .build());

            return "redirect:/user/contacts?success";

        } catch (ResourceNotFoundException ex) {
            logger.error("Contact not found for update: {}", contactId);
            session.setAttribute("message", Message.builder()
                    .content("Contact not found")
                    .type(MessageType.red)
                    .build());
            return "redirect:/user/contacts?error";
        } catch (Exception e) {
            logger.error("Error updating contact: {}", e.getMessage(), e);
            session.setAttribute("message", Message.builder()
                    .content("Error updating contact: " + e.getMessage())
                    .type(MessageType.red)
                    .build());
            return "redirect:/user/contacts?error";
        }
    }

    // Add this method to your ContactController
    
}