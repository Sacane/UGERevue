package fr.pentagon.ugeoverflow.service;

import fr.pentagon.revevue.common.exception.HttpException;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserInfoUpdateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserPasswordUpdateDTO;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceWithFailure {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceWithFailure(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void updateUser(long userId, UserInfoUpdateDTO userInfoUpdateDTO) {
        var user = userRepository.findById(userId).orElseThrow();
        user.setUsername(userInfoUpdateDTO.username());
        userRepository.save(user);
    }

    @Transactional
    public void updateUserPassword(long userId, UserPasswordUpdateDTO userPasswordUpdateDTO) {
        var user = userRepository.findById(userId).orElseThrow();
        if (!passwordEncoder.matches(userPasswordUpdateDTO.oldPassword(), user.getPassword())) {
            throw HttpException.badRequest("Bad password");
        }
        user.setPassword(passwordEncoder.encode(userPasswordUpdateDTO.newPassword()));
        userRepository.save(user);
    }
}
