package com.pb.contactapp.contact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
class UpdateContactDto {
    @Pattern(regexp = "^\\+?\\d*$", message = "phone number should contains only numbers and \"+\" sign")
    private String phoneNumber;

    @Pattern(regexp = "^[a-zA-ZżŻźŹćĆńŃąĄśŚłŁęĘóÓ]*$", message = "Field should contains only characters")
    private String name;

    @Pattern(regexp = "^[a-zA-ZżŻźŹćĆńŃąĄśŚłŁęĘóÓ]*$", message = "Field should contains only characters")
    private String surname;
}
