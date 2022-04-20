package com.pb.contactapp.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
class UserApiImpl implements UserApi{

    private final UserRepository userRepository;

    @Override
    public UserEntity findUserByApiKey(final char[] apiKey) {
        return userRepository.findByApiKey(apiKey)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User not found!"));
    }

    @Override
    public void addUser(UserEntity userEntity) {
        userRepository.save(userEntity);
    }
}
