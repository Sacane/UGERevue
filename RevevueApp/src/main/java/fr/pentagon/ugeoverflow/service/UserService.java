package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.algorithm.QuestionMatcher;
import fr.pentagon.ugeoverflow.config.authorization.Role;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserFollowInfoDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserInfoUpdateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserPasswordUpdateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserRegisterDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewContentDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.UserFollowingDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.UserIdDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.UserInfoDTO;
import fr.pentagon.revevue.common.exception.HttpException;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class UserService {
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
    }

    @Transactional
    public UserInfoDTO findById(long userId) {
        return userRepository.findById(userId)
                .map(u -> new UserInfoDTO(u.getUsername(), u.getLogin(), u.getEmail(), u.getRole()))
                .orElseThrow(() -> HttpException.notFound("The user does not exists"));
    }

    @Transactional
    public UserIdDTO register(UserRegisterDTO userDTO) {
        if (userRepository.existsByUsernameOrLogin(userDTO.username(), userDTO.login())) {
            throw HttpException.badRequest("User with this username or login already exist");
        }
        var user = new User(userDTO.username(),
                userDTO.login(),
                passwordEncoder.encode(userDTO.password()),
                userDTO.email(),
                Role.USER
        );
        var newUser = userRepository.save(user);
        return new UserIdDTO(newUser.getId(), newUser.getUsername(), newUser.getRole().toString());
    }

    @Transactional
    public void follow(long followerId, long followedId) {
        var follower = userRepository.findById(followerId).orElseThrow();
        var followed = userRepository.findById(followedId).orElseThrow(
                () -> HttpException.notFound("user " + followedId + " not found")
        );

        follower.follows(followed);
        userRepository.saveAll(List.of(follower, followed));
    }

    @Transactional
    public void unfollow(long followerId, long followedId) {
        var follower = userRepository.findById(followerId).orElseThrow();
        var followed = userRepository.findById(followedId).orElseThrow(
                () -> HttpException.notFound("user " + followedId + " not found")
        );
        follower.unfollows(followed);
        userRepository.saveAll(List.of(follower, followed));
    }

    @Transactional
    public List<UserFollowInfoDTO> userRegisteredList(Long userId) {
        var follows = userRepository.findFollowsById(userId);
        return userRepository.findAllUsers()
                .stream()
                .<UserFollowInfoDTO>mapMulti((user, consumer) -> {
                    if (user.getId() != userId && user.getRole() != Role.ADMIN) {
                        consumer.accept(user.toUserFollowInfoDTO(follows.contains(user)));
                    }
                })
                .toList();
    }

    @Transactional
    public List<UserFollowInfoDTO> userRegisteredList() {
        return userRepository.findAllUsers()
                .stream()
                .filter(u -> u.getRole() != Role.ADMIN)
                .map(user -> user.toUserFollowInfoDTO(false))
                .toList();
    }

    @Transactional
    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class)
    public void updateUser(long userId, UserInfoUpdateDTO userInfoUpdateDTO) {
        var user = userRepository.findById(userId).orElseThrow();
        user.setUsername(userInfoUpdateDTO.username());
        userRepository.save(user);
    }

    @Transactional
    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class)
    public void updateUserPassword(long userId, UserPasswordUpdateDTO userPasswordUpdateDTO) {
        var user = userRepository.findById(userId).orElseThrow();
        if (!passwordEncoder.matches(userPasswordUpdateDTO.oldPassword(), user.getPassword())) {
            throw HttpException.badRequest("Bad password");
        }
        user.setPassword(passwordEncoder.encode(userPasswordUpdateDTO.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public List<UserFollowingDTO> getUserFollowings(long userId) {
        var user = userRepository.findById(userId).orElseThrow();
        return userRepository.findFollowing(user).stream()
                .map(following -> new UserFollowingDTO(following.getId(), following.getUsername()))
                .toList();
    }

    @Transactional
    public List<ReviewContentDTO> getRecommendedReviewForQuestion(long userId, String questionContent) {
        var user = userRepository.findById(userId).orElseThrow();
        var reviewList = user.getReviews();
        return reviewList
                .stream()
                .filter(review -> QuestionMatcher.isPertinentRecommendation(questionContent,
                        review.getQuestion().getDescription()) && review.comparedWithAnotherReviewListBasedOnContent(reviewList))
                .map(review -> new ReviewContentDTO(review.getContent()))
                .toList();
    }
}
