package com.cm.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cm.entities.Contact;
import com.cm.entities.User;
import com.cm.helpers.ResourceNotFoundException;
import com.cm.repository.ContactRepo;
import com.cm.service.ContactService;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepo contactRepo;

    public ContactServiceImpl(ContactRepo contactRepo) {
        this.contactRepo = contactRepo;
    }

    @Override
    public Contact save(Contact contact) {

        String contactId = UUID.randomUUID().toString();
        contact.setId(contactId);
        return contactRepo.save(contact);
    
    }

    @Override
    public Contact update(Contact contact) {
        // Check if contact exists
        Contact existingContact = contactRepo.findById(contact.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + contact.getId()));
        
        // Update the existing contact with new data
        existingContact.setName(contact.getName());
        existingContact.setEmail(contact.getEmail());
        existingContact.setPhoneNumber(contact.getPhoneNumber());
        existingContact.setAddress(contact.getAddress());
        existingContact.setDescription(contact.getDescription());
        existingContact.setFavorite(contact.isFavorite());
        existingContact.setWebsiteLink(contact.getWebsiteLink());
        existingContact.setLinkedInLink(contact.getLinkedInLink());
        
        // Only update picture if a new one is provided

        if (contact.getPicture() != null && !contact.getPicture().equals(existingContact.getPicture())) {
            existingContact.setPicture(contact.getPicture());
            existingContact.setCloudinaryImagePublicId(contact.getCloudinaryImagePublicId());
        }
        
        return contactRepo.save(existingContact);
    }

    @Override
    public List<Contact> getAll() {
        return contactRepo.findAll();
    }

    @Override
    public Contact getById(String id) {
        return contactRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
    }

    @Override
    @Transactional
    public void delete(String id) {
        System.out.println("=== DELETING CONTACT WITH QUERY ===");
        
        // Check if contact exists
        boolean exists = contactRepo.existsById(id);
        if (!exists) {
            System.out.println("❌ Contact not found, cannot delete.");
            throw new ResourceNotFoundException("Contact not found");
        }
        
        // Option 1: Use a custom delete query (often the cleanest)
        contactRepo.hardDeleteById(id);
        
        // Option 2: If you need to use the standard method, 
        // find and delete within the same transactional method.
        // Contact contact = contactRepo.findById(id).orElseThrow(...);
        // contactRepo.delete(contact);
        
        // Force a flush to the database
        contactRepo.flush();
        
        System.out.println("✅ Contact deleted successfully via query.");
    }


    @Override
    public List<Contact> getByUserId(String userId) {
        return contactRepo.findByUserId(userId);
    }

    @Override
    public Page<Contact> getByUser(User user, int page, int size, String sortField, String direction) {
        
        Sort sort = direction.equals("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        var pageble = PageRequest.of(page, size, sort);
        
        return contactRepo.findByUser(user , pageble);
    }

    @Override
public Page<Contact> searchByName(String nameKeyword, User user, int size, int page, String sortBy, String order) {
    Sort sort = order.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
    var pageable = PageRequest.of(page, size, sort);
    
    return contactRepo.findByNameContainingAndUser(nameKeyword, user, pageable);
}

@Override
public Page<Contact> searchByEmail(String emailKeyword, User user, int size, int page, String sortBy, String order) {
    Sort sort = order.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
    var pageable = PageRequest.of(page, size, sort);
    
    return contactRepo.findByEmailContainingAndUser(emailKeyword, user, pageable);
}

@Override
public Page<Contact> searchByPhoneNumber(String phoneNumberKeyword, User user, int size, int page, String sortBy, String order) {
    Sort sort = order.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
    var pageable = PageRequest.of(page, size, sort);
    
    return contactRepo.findByPhoneNumberContainingAndUser(phoneNumberKeyword, user, pageable);
}

    

}
