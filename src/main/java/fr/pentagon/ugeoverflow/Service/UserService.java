package fr.pentagon.ugeoverflow.Service;

import fr.pentagon.ugeoverflow.DTO.UserIdDTO;
import fr.pentagon.ugeoverflow.DTO.UserRegisterDTO;
import fr.pentagon.ugeoverflow.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Objects;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder){
        this.repository = Objects.requireNonNull(repository);
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
    }


    @Transactional
    public ResponseEntity<UserIdDTO> register(UserRegisterDTO user){
        if(repository.existsByUsername(user.username())){
            //TODO Créer propre classe d'exception HTTP
        }
        return null;
    }

}
