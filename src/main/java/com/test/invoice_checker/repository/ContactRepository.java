package com.test.invoice_checker.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.invoice_checker.model.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    // Find by both email and phone number
    Contact findByEmailAndPhoneNumber(String email, String phoneNumber);

    // Find by email
    List<Contact> findByEmail(String email);

    // Find by phone number
    List<Contact> findByPhoneNumber(String phoneNumber);

	List<Contact> findAllByEmailAndPhoneNumber(String email, String phoneNumber);
}
