package com.pb.contactapp.contact;

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
class FullDetailsContactDto {

    private Long id;

    private Long userId;

    private String phoneNumber;

    private String name;

    private String surname;

    private String createdAt;
}
