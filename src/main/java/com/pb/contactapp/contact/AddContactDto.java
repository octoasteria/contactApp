package com.pb.contactapp.contact;

import com.pb.contactapp.common.OnlyCharactersNotEmpty;
import com.pb.contactapp.common.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
class AddContactDto {

    @PhoneNumber
    private String phoneNumber;

    @OnlyCharactersNotEmpty
    private String name;

    @OnlyCharactersNotEmpty
    private String surname;

}


