package com.pb.contactapp.contact;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/contacts")
class ContactController {

    private final static String HEADER_AUTH_KEY = "X-API-KEY";

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<FullDetailsContactDto> addContact(@RequestHeader(HEADER_AUTH_KEY) final char[] apiKey,
                                                            @RequestBody @Valid final AddContactDto addContactDto) {
        return new ResponseEntity<>(contactService.addContact(addContactDto, apiKey), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FullDetailsContactDto> updateContact(@PathVariable final Long id,
                                                               @RequestBody final UpdateContactDto updateContactDto,
                                                               @RequestHeader(HEADER_AUTH_KEY) final char[] apiKey) {
        return ResponseEntity.ok(contactService.updateContact(id, updateContactDto, apiKey));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable final Long id,
                                              @RequestHeader(HEADER_AUTH_KEY) final char[] apiKey) {
        contactService.deleteContact(id, apiKey);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<FullDetailsContactDto>> findAllContact(@RequestHeader(HEADER_AUTH_KEY) final char[] apiKey) {
        return ResponseEntity.ok(contactService.getAllContacts(apiKey));
    }

    @RequestMapping("/{id}")
    public ResponseEntity<FullDetailsContactDto> getContactById(@RequestHeader(HEADER_AUTH_KEY) final char[] apiKey,
                                                                @PathVariable final Long id){
        return ResponseEntity.ok(contactService.findContactById(id, apiKey));
    }
}
