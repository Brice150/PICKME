package com.packages.backend.model.user;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserDTOMapperHiddenRole implements Function<User, UserDTO> {
    @Override
    public UserDTO apply(User user) {
        return new UserDTO(
                user.getId(),
                UserRole.HIDDEN,
                user.getBirthDate(),
                user.getGold(),
                user.getPictures(),
                user.getMainPicture(),
                user.getNickname(),
                user.getJob(),
                user.getCity(),
                user.getHeight(),
                user.getGender(),
                user.getGenderSearch(),
                user.getMinAge(),
                user.getMaxAge(),
                user.getEmail(),
                user.getDescription(),
                user.getAlcoholDrinking(),
                user.getSmokes(),
                user.getOrganised(),
                user.getPersonality(),
                user.getSportPractice(),
                user.getAnimals(),
                user.getParenthood(),
                user.getGamer()
        );
    }
}
