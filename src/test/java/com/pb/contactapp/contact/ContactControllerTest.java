package com.pb.contactapp.contact;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pb.contactapp.user.UserApi;
import com.pb.contactapp.user.UserEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserApi userApi;

    @Autowired
    private ContactRepository contactRepository;

    private final static String API_KEY = "LEGAL_API_KEY";

    private final static String API_KEY_NAME = "X-API-KEY";

    private final static long TEST_ID = 1L;

    private AddContactDto testContact =
            new AddContactDto("NAME", "SURNAME", "123123123");

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    public void setUserAndTestContact() {
        UserEntity userEntity = UserEntity.builder()
                .id(TEST_ID)
                .name("USER1")
                .apiKey(API_KEY.toCharArray())
                .build();

        userApi.addUser(userEntity);

        ContactEntity contactEntity = ContactEntity.builder()
                .id(TEST_ID)
                .name("NAME")
                .surname("SURNAME")
                .phoneNumber("123123123")
                .user(userEntity)
                .createdAt(LocalDateTime.now())
                .build();

        contactRepository.save(contactEntity);
    }

    @Test
    void shouldAddNewContact() throws Exception {
        //given
        AddContactDto addContactDto = testContact;

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/contacts")
                .content(asJsonString(addContactDto))
                .header(API_KEY_NAME, API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    void shouldThrowWhenDataIsIncorrect() throws Exception {
        //given
        AddContactDto invalidDataContact = AddContactDto.builder()
                .name("NAM1E")
                .surname("SUR1NAME")
                .phoneNumber("1231Z23123")
                .build();

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/contacts")
                .content(asJsonString(invalidDataContact))
                .header(API_KEY_NAME, API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowWhenApiKeyIsNotRecognized() throws Exception {
        //given
        AddContactDto addContactDto = testContact;

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/contacts")
                .content(asJsonString(addContactDto))
                .header(API_KEY_NAME, "WEIRDAPIKEY")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void shouldUpdateContact() throws Exception {
        //given
        String longerName = "LONGER NAME";
        UpdateContactDto updateContactDto = UpdateContactDto.builder()
                .surname(longerName)
                .build();

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .patch("/contacts/{id}", TEST_ID)
                .content(asJsonString(updateContactDto))
                .header(API_KEY_NAME, API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.surname").value(longerName));
    }

    @Test
    void shouldDeleteContact() throws Exception {
        //given
        UserEntity userEntity = userApi.findUserByApiKey(API_KEY.toCharArray());

        ContactEntity contactEntity = ContactEntity.builder()
                .name("NAME")
                .surname("SURNAME")
                .phoneNumber("123123123")
                .user(userEntity)
                .createdAt(LocalDateTime.now())
                .build();

        var c1 = contactRepository.save(contactEntity);
        long addedContactEntity = c1.getId();

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/contacts/{id}", addedContactEntity)
                .header(API_KEY_NAME, API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    void shouldGetAllContacts() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/contacts")
                                .header(API_KEY_NAME, API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void shouldGetContactById() throws Exception {
        //given
        long contactId = TEST_ID;
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/contacts/{id}", contactId)
                .header(API_KEY_NAME, API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void shouldThrowExceptionWhenTryToInteractWithNotOwnedContact() throws Exception {
        //given
        UserEntity nextUser = UserEntity.builder()
                .name("USER2")
                .apiKey("USER_2_KEY".toCharArray())
                .build();
        userApi.addUser(nextUser);

        long contactId = TEST_ID;
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/contacts/{id}", contactId)
                .header(API_KEY_NAME, Arrays.toString(nextUser.getApiKey()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isForbidden());
    }
}
