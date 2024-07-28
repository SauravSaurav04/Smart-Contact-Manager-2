package com.scm.services;

import com.scm.entities.Contact;
import com.scm.entities.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ContactService {

    Contact save(Contact contact);
    Contact update(Contact contact);
    List<Contact> getAll();
    Contact getById(String id);
    void delete(String id);
    List<Contact> getByUserId(String userId);
    Page<Contact> getByUser(User user, int page, int size, String sortField, String sortDirection);
    Page<Contact> searchByName(String nameKeyword, int size, int page, String sortBy, String order, User user);
    Page<Contact> searchByEmail(String emailKeyword, int size, int page, String sortBy, String order, User user);
    Page<Contact> searchByPhoneNumber(String phoneNumberKeyword, int size, int page, String sortBy, String order, User user);
}
