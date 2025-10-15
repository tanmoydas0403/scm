package com.cm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cm.entities.Contact;
import com.cm.helpers.ResourceNotFoundException;
import com.cm.repository.ContactRepo;
import com.cm.service.ContactService;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final ContactService contactService;

    public ApiController(ContactService contactService, ContactRepo contactRepo) {
        this.contactService = contactService;
    }

    //get contact of user

    @GetMapping("/contact/{contactId}")
    public ResponseEntity<Contact> getContact(@PathVariable String contactId) {
        try {
            System.out.println("Contact ID: " + contactId);
            Contact contact = contactService.getById(contactId);
            return ResponseEntity.ok(contact);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    
}
