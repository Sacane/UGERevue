package fr.pentagon.ugeoverflow.config.security;

import fr.pentagon.ugeoverflow.AuthTestUtils;
import fr.pentagon.ugeoverflow.DatasourceTestConfig;
import fr.pentagon.ugeoverflow.exception.HttpException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Import(DatasourceTestConfig.class)
public class AuthFilterTest {
    @Autowired
    private RequestMappingHandlerMapping requireScanner;
    @Autowired
    private AuthTestUtils authTestUtils;
    @Test
    @DisplayName("Should throw when controller has annotation but user is not authenticated")
    public void annotationTest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/reviews");
        request.setMethod("POST");
        MockHttpServletResponse response = new MockHttpServletResponse();
        var filter = new AuthFilter(requireScanner);
        var mockFilterChain = new MockFilterChain();
        assertThrows(HttpException.class, () -> filter.doFilter(request, response, mockFilterChain));
    }
    @Test
    @DisplayName("Should not throw when annotation is absent")
    public void annotationTest2() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/reviews");
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();
        var filter = new AuthFilter(requireScanner);
        var mockFilterChain = new MockFilterChain();
        assertDoesNotThrow(() -> filter.doFilter(request, response, mockFilterChain));
    }

    @Test
    @DisplayName("Should not throw when annotation is present and user is connected")
    public void annotationTest3() {

        SecurityContextHolder.getContext().setAuthentication(new Authentication() {
            @Override
            public boolean equals(Object another) {
                return false;
            }

            @Override
            public String toString() {
                return null;
            }

            @Override
            public int hashCode() {
                return 0;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }
        });

        authTestUtils.assertUserIsConnected("/api/reviews", "POST");
    }

}
