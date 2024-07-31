package com.test.invoice_checker.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.invoice_checker.model.Contact;
import com.test.invoice_checker.model.LinkPrecedence;
import com.test.invoice_checker.repository.ContactRepository;
import com.test.invoice_checker.response.ContactResponse;

import jakarta.transaction.Transactional;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Transactional
    public ContactResponse identifyContact(String email, String phoneNumber) {
        // Check for an existing contact with both email and phone number


        // Find existing contacts by email or phone number
        List<Contact> existingContacts = new ArrayList<>();
        Contact primaryContact = contactRepository.findByEmailAndPhoneNumber(email, phoneNumber);
        if (primaryContact != null) {
        	existingContacts.add(primaryContact);
        }
        existingContacts.addAll(contactRepository.findByEmail(email));
        existingContacts.addAll(contactRepository.findByPhoneNumber(phoneNumber));
        existingContacts.removeIf(c -> c.getEmail().equals(email) && c.getPhoneNumber().equals(phoneNumber));

        if (existingContacts.isEmpty()) {
            // No existing contacts found, create a new primary contact
            Contact newContact = new Contact();
            newContact.setEmail(email);
            newContact.setPhoneNumber(phoneNumber);
            newContact.setLinkPrecedence(LinkPrecedence.PRIMARY);
            contactRepository.save(newContact);
            return buildContactResponse(newContact);
        }

        // Find the primary contact from existing contacts or create a new one
        Contact primaryContactFromExisting = existingContacts.stream()
            .filter(c -> c.getLinkPrecedence() == LinkPrecedence.PRIMARY)
            .findFirst()
            .orElseGet(() -> {
                Contact newPrimaryContact = new Contact();
                newPrimaryContact.setEmail(email);
                newPrimaryContact.setPhoneNumber(phoneNumber);
                newPrimaryContact.setLinkPrecedence(LinkPrecedence.PRIMARY);
                return contactRepository.save(newPrimaryContact);
            });

        if (primaryContactFromExisting.getLinkPrecedence() == LinkPrecedence.PRIMARY) {
            // Create a new secondary contact linked to the existing primary contact
            Contact newSecondaryContact = new Contact();
            newSecondaryContact.setEmail(email);
            newSecondaryContact.setPhoneNumber(phoneNumber);
            newSecondaryContact.setLinkPrecedence(LinkPrecedence.SECONDARY);
            newSecondaryContact.setLinkedId(primaryContactFromExisting.getId());
            contactRepository.save(newSecondaryContact);
        }

        return buildContactResponse(primaryContactFromExisting, existingContacts);
    }

    private ContactResponse buildContactResponse(Contact primaryContact) {
        List<Contact> linkedContacts = contactRepository.findByPhoneNumber(primaryContact.getPhoneNumber());
        return buildContactResponse(primaryContact, linkedContacts);
    }

    private ContactResponse buildContactResponse(Contact primaryContact, List<Contact> linkedContacts) {
        Set<String> emails = new LinkedHashSet<>();
        Set<String> phoneNumbers = new LinkedHashSet<>();
        List<Integer> secondaryContactIds = new ArrayList<>();

        emails.add(primaryContact.getEmail());
        phoneNumbers.add(primaryContact.getPhoneNumber());

        for (Contact contact : linkedContacts) {
            if (contact.getLinkPrecedence() == LinkPrecedence.SECONDARY) {
                secondaryContactIds.add(contact.getId());
                emails.add(contact.getEmail());
                phoneNumbers.add(contact.getPhoneNumber());
            }
        }

        return new ContactResponse(
            primaryContact.getId(),
            new ArrayList<>(emails),
            new ArrayList<>(phoneNumbers),
            secondaryContactIds
        );
    }
}
