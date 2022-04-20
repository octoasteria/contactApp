package com.pb.contactapp.contact;

import com.pb.contactapp.user.UserApi;
import com.pb.contactapp.user.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Spy
    private ModelMapper modelMapper;

    @Mock
    private UserApi userApi;

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactService contactService;
    private static final long ID = 1L;
    private static final String NAME = "John";
    private static final String SURNAME = "Doe";
    private static final String PHONE_NUMBER = "111222333";
    private static final AddContactDto addContact = new AddContactDto(PHONE_NUMBER, NAME, SURNAME);
    private static final char[] API_KEY = new char[]{'a', 'p', 'i'};
    private static final UserEntity USER = new UserEntity(1L, "someUser", API_KEY, Collections.emptyList());

    private static final FullDetailsContactDto FULL_DETAILS_CONTACT_DTO =
            FullDetailsContactDto.builder()
                    .id(ID)
                    .name(NAME)
                    .surname(SURNAME)
                    .phoneNumber(PHONE_NUMBER)
                    .build();

    private static final ContactEntity CONTACT_ENTITY =
            ContactEntity.builder()
                    .id(ID)
                    .name(NAME)
                    .surname(SURNAME)
                    .phoneNumber(PHONE_NUMBER)
                    .user(USER)
                    .createdAt(LocalDateTime.now())
                    .build();

    @Test
    public void shouldConvertFullDetailsContactDtoEntity() {
        //given
        FullDetailsContactDto fullDetailsContactDto = FULL_DETAILS_CONTACT_DTO;
        //when
        ContactEntity contactEntity = contactService.convertToEntity(fullDetailsContactDto);
        //then
        assertEquals(contactEntity.getClass(), ContactEntity.class);
        assertEquals(contactEntity.getId(), fullDetailsContactDto.getId());
        assertEquals(contactEntity.getPhoneNumber(), fullDetailsContactDto.getPhoneNumber());
        assertEquals(contactEntity.getName(), fullDetailsContactDto.getName());
    }

    @Test
    public void shouldConvertAddContactDtoEntityToDto() {
        //given
        AddContactDto addContactDto = addContact;
        //when
        ContactEntity contactEntity = contactService.convertToEntity(addContactDto);
        //then
        assertEquals(ContactEntity.class, contactEntity.getClass());
        assertEquals(addContactDto.getPhoneNumber(), contactEntity.getPhoneNumber());
        assertEquals(addContactDto.getName(), contactEntity.getName());
    }

    @Test
    public void shouldConvertDtoToEntity() {
        //given
        ContactEntity contactEntity = CONTACT_ENTITY;
        //when
        FullDetailsContactDto fullDetailsContactDto = contactService.convertToDto(contactEntity);
        //then
        assertEquals(fullDetailsContactDto.getClass(), FullDetailsContactDto.class);
        assertEquals(fullDetailsContactDto.getId(), contactEntity.getId());
        assertNotNull(fullDetailsContactDto.getCreatedAt());
        assertEquals(fullDetailsContactDto.getPhoneNumber(), contactEntity.getPhoneNumber());
        assertEquals(fullDetailsContactDto.getName(), contactEntity.getName());
    }

    @Test
    public void shouldEntityHandleNullWhenMapping() {
        //given
        FullDetailsContactDto contactDto = FullDetailsContactDto.builder()
                .name(null)
                .userId(null)
                .phoneNumber(null)
                .name(null)
                .surname(null)
                .build();
        //when
        ContactEntity contactEntity = contactService.convertToEntity(contactDto);

        //then
        assertNull(contactEntity.getName());
        assertNull(contactEntity.getUser());
        assertNull(contactEntity.getPhoneNumber());
        assertNull(contactEntity.getSurname());
    }

    @Test
    public void shouldAddNewContact() {
        //given
        char[] apiKey = API_KEY;
        UserEntity user = USER;
        given(userApi.findUserByApiKey(apiKey)).willReturn(user);
        AddContactDto addContactDto = addContact;

        //when
        FullDetailsContactDto addedContact = contactService.addContact(addContactDto, apiKey);

        //then
        assertEquals(user.getId(), addedContact.getUserId());
        assertNotNull(addedContact.getCreatedAt());
    }

    @Test
    public void shouldUpdateContactOnlyWithOneFiled() {
        //given
        long updatedContactId = 1L;
        String updatedName = "Thomas";
        char[] apiKey = API_KEY;
        given(contactRepository.findById(1L)).willReturn(Optional.of(CONTACT_ENTITY));
        UpdateContactDto updateContactDto = UpdateContactDto.builder()
                .name(updatedName)
                .build();

        //when
        FullDetailsContactDto addedContact =
                contactService.updateContact(updatedContactId, updateContactDto, apiKey);

        //then
        assertEquals(updatedName, addedContact.getName());
        assertEquals(CONTACT_ENTITY.getSurname(), addedContact.getSurname());
        assertEquals(CONTACT_ENTITY.getPhoneNumber(), addedContact.getPhoneNumber());
        assertNotNull(addedContact.getCreatedAt());
    }

    @Test
    public void shouldUpdateAllFields() {
        //given
        long updatedContactId = 1L;
        String updatedName = "Thomas";
        String updateSurname = "Smith";
        String updatedPhoneNumber = "951753852";
        char[] apiKey = API_KEY;
        given(contactRepository.findById(1L)).willReturn(Optional.of(CONTACT_ENTITY));
        UpdateContactDto updateContactDto = UpdateContactDto.builder()
                .name(updatedName)
                .phoneNumber(updatedPhoneNumber)
                .surname(updateSurname)
                .build();

        //when
        FullDetailsContactDto addedContact =
                contactService.updateContact(updatedContactId, updateContactDto, apiKey);

        //then
        assertEquals(updatedName, addedContact.getName());
        assertEquals(updateSurname, addedContact.getSurname());
        assertEquals(updatedPhoneNumber, addedContact.getPhoneNumber());
        assertNotNull(addedContact.getCreatedAt());
    }

    @Test
    void shouldValidateApiKey() {
        //given
        ContactEntity contactEntity = ContactEntity.builder()
                .user(USER)
                .build();
        //when
        char[] apiKey = new char[]{'w', 'r', 'o', 'g', 'k', 'e', 'y'};
        //when
        assertThrows(ResponseStatusException.class,
                () -> contactService.validateContactWithUserApiKey(apiKey, contactEntity));
    }

    @Test
    void shouldReturn403WhenForbidden() {
        //given
        ContactEntity contactEntity = ContactEntity.builder()
                .user(USER)
                .build();
        //when
        char[] apiKey = new char[]{'w', 'r', 'o', 'g', 'k', 'e', 'y'};
        ResponseStatusException exception = null;

        //then
        try {
            contactService.validateContactWithUserApiKey(apiKey, contactEntity);
        } catch (ResponseStatusException ex) {
            exception = ex;
        }
        assertNotNull(exception);
        assertEquals(ResponseStatusException.class, exception.getClass());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    public void shouldUpdateNotNullFields() {
        //given
        long contactId = 1L;
        ContactEntity contactEntity = CONTACT_ENTITY;
        UpdateContactDto updateContactDto = UpdateContactDto.builder()
                .phoneNumber("98989898")
                .build();
        given(contactRepository.findById(contactId)).willReturn(Optional.of(contactEntity));
        //when
        FullDetailsContactDto fullContact = contactService.updateContact(contactId, updateContactDto, contactEntity.getUser().getApiKey());
        //then
        assertEquals(updateContactDto.getPhoneNumber(), fullContact.getPhoneNumber());
    }


    @Test
    public void shouldListOutAllContactsForUser() {
        //given
        UserEntity user = USER;
        ContactEntity contactEntity = CONTACT_ENTITY;
        given(userApi.findUserByApiKey(user.getApiKey())).willReturn(USER);
        given(contactRepository.findByUserId(user.getId())).willReturn(List.of(contactEntity));
        //when
        List<FullDetailsContactDto> allContacts = contactService.getAllContacts(user.getApiKey());
        //then
        assertEquals(1, allContacts.size());
        assertEquals(allContacts.get(0).getUserId(), user.getId());

    }

    @Test
    public void shouldThrowExceptionWhenContactIsForbiddenForUser() {
        //given
        long user2ContactId = 1L;
        char[] apiKeyUser1 = ("F15HY_K3Y").toCharArray();
        UserEntity hacker = UserEntity.builder()
                .apiKey(apiKeyUser1)
                .build();
        ContactEntity contactEntity = CONTACT_ENTITY;
        given(contactRepository.findById(user2ContactId)).willReturn(Optional.of(contactEntity));
        //when
        //then
        assertThrows(ResponseStatusException.class, () -> contactService.findContactById(user2ContactId, hacker.getApiKey()));
    }
}