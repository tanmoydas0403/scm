package com.cm.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.cm.entities.Contact;
import com.cm.entities.User;

public interface ContactService {

    //save contact
    Contact save(Contact contact);

    //update contact
    Contact update(Contact contact);

    //get contacts
    List<Contact> getAll();

    //get contact by id
    Contact getById(String id);

    //delete contact
    void delete(String id);

    //search contact
    // In ContactService interface - update all search methods to include User parameter
    Page<Contact> searchByName(String nameKeyword, User user, int size, int page, String sortBy, String order);
    Page<Contact> searchByEmail(String emailKeyword, User user, int size, int page, String sortBy, String order);
    Page<Contact> searchByPhoneNumber(String phoneNumberKeyword, User user, int size, int page, String sortBy, String order);
    //get contacts by user id
    List<Contact> getByUserId(String userId);

    org.springframework.data.domain.Page<Contact> getByUser(User user, int page, int size, String sortField, String direction);
}
