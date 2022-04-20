package com.pb.contactapp.user;

public interface UserApi {
    UserEntity findUserByApiKey(char[] apiKey);

    void addUser(UserEntity userEntity);
}
