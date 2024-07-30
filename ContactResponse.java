package com.test.invoice_checker.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContactResponse {

	private Integer primaryContactId;
	private List<String> emails;
	private List<String> phoneNumbers;
	private List<Integer> secondaryContactIds;

}
