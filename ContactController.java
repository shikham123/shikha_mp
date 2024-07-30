package com.test.invoice_checker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.invoice_checker.request.ContactRequest;
import com.test.invoice_checker.response.ContactResponse;
import com.test.invoice_checker.service.ContactService;

@RestController
@RequestMapping("contacts")
public class ContactController {

	@Autowired
	private ContactService contactService;

	@PostMapping("/identify")
	public ResponseEntity<ContactResponse> identifyContact(@RequestBody ContactRequest contactRequest) {
		ContactResponse response = contactService.identifyContact(contactRequest.getEmail(),
				contactRequest.getPhoneNumber());
		return ResponseEntity.ok(response);
	}
}
