package fr.pentagon.ugeoverflow.testutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pentagon.ugeoverflow.DatasourceTestConfig;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.CredentialsDTO;
import fr.pentagon.ugeoverflow.controllers.rest.LoginController;
import fr.pentagon.ugeoverflow.exception.HttpExceptionHandler;
import fr.pentagon.ugeoverflow.utils.Routes;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@Import(DatasourceTestConfig.class)
@Component
public class LoginTestService {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoginTestService(LoginController loginController) {
        this.mockMvc = MockMvcBuilders.standaloneSetup(loginController)
                .setControllerAdvice(new HttpExceptionHandler())
                .build();
    }

    public ResultActions login(CredentialsDTO credentialsDTO) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(Routes.Auth.LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentialsDTO)));
    }

    public ResultActions logout() throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(Routes.Auth.LOGOUT));
    }
}
