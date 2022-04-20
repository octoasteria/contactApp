package com.pb.contactapp.contact;

import com.pb.contactapp.user.UserApi;
import com.pb.contactapp.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class ContactService {
    private final ModelMapper modelMapper;
    private final UserApi userApi;
    private final ContactRepository contactRepository;
    private final String datePattern = "yyyy.MM.dd HH:mm.ss";

    ContactEntity convertToEntity(final FullDetailsContactDto fullDetailsContactDto) {
        return modelMapper.map(fullDetailsContactDto, ContactEntity.class);
    }

    ContactEntity convertToEntity(final AddContactDto addContactDto) {
        return modelMapper.map(addContactDto, ContactEntity.class);
    }

    FullDetailsContactDto convertToDto(final ContactEntity contactEntity) {
        FullDetailsContactDto contactDto = modelMapper.map(contactEntity, FullDetailsContactDto.class);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
        contactDto.setCreatedAt(formatter.format(contactEntity.getCreatedAt()));
        return contactDto;
    }

    FullDetailsContactDto addContact(final AddContactDto addContactDto,
                                     final char[] apiKey) {
        UserEntity user = userApi.findUserByApiKey(apiKey);
        ContactEntity contactEntity = convertToEntity(addContactDto);
        contactEntity.setCreatedAt(LocalDateTime.now());
        contactEntity.setUser(user);
        contactRepository.save(contactEntity);
        return convertToDto(contactEntity);
    }

    FullDetailsContactDto updateContact(final Long id,
                                        final UpdateContactDto updateContactDto,
                                        final char[] apiKey) {
        ContactEntity contactEntity = findById(id);
        validateContactWithUserApiKey(apiKey, contactEntity);
        updateNotNullFields(updateContactDto, contactEntity);
        return convertToDto(contactEntity);
    }

    void validateContactWithUserApiKey(final char[] apiKey,
                                       final ContactEntity contactEntity) {
        if (!Arrays.equals(apiKey, contactEntity.getUser().getApiKey())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User can interact only with his own contacts");
        }
    }

    void deleteContact(final Long contactId,
                       final char[] apiKey) {
        ContactEntity contact = findById(contactId);
        validateContactWithUserApiKey(apiKey, contact);
        contactRepository.deleteById(contactId);
    }

    private ContactEntity findById(final Long id) {
        ContactEntity contactEntity = contactRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact with id: " + id + " not found"));
        return contactEntity;
    }

    private void updateNotNullFields(final UpdateContactDto updateContactDto,
                                     final ContactEntity contactEntity) {
        String phoneNumber = updateContactDto.getPhoneNumber();
        String surname = updateContactDto.getSurname();
        String name = updateContactDto.getName();
        if (phoneNumber != null) {
            contactEntity.setPhoneNumber(phoneNumber);
        }
        if (surname != null) {
            contactEntity.setSurname(surname);
        }
        if (name != null) {
            contactEntity.setName(name);
        }
    }

    List<FullDetailsContactDto> getAllContacts(final char[] apiKey) {
        UserEntity user = userApi.findUserByApiKey(apiKey);
        return contactRepository.findByUserId(user.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    FullDetailsContactDto findContactById(final Long contactId,
                                          final char[] apiKey) {
        var contact = findById(contactId);
        validateContactWithUserApiKey(apiKey, contact);
        return convertToDto(contact);
    }
}
