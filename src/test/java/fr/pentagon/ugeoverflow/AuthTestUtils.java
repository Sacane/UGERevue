package fr.pentagon.ugeoverflow;

import fr.pentagon.ugeoverflow.config.security.AuthFilter;
import fr.pentagon.ugeoverflow.exception.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Component
public class AuthTestUtils {

    @Autowired
    private RequestMappingHandlerMapping requireScanner;

    /**
     * assert either if the route contains the @RequireAuth annotation and user is connected or it does not contains it.
     * @param path path of the api route
     *
     * Ex:
     *             assertUserIsConnected("/api/reviews", "GET")
     */
    public void assertUserIsConnected(String path, String method) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath(path);
        request.setMethod(method);
        MockHttpServletResponse response = new MockHttpServletResponse();
        var filter = new AuthFilter(requireScanner);
        var mockFilterChain = new MockFilterChain();
        assertDoesNotThrow(() -> filter.doFilter(request, response, mockFilterChain));
    }

    /**
     * assert that the given route is annoted with @RequireAuthentication but the user is not connected
     * @param path
     * @param method
     */
    public void assertNotConnected(String path, String method) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath(path);
        request.setMethod(method);
        MockHttpServletResponse response = new MockHttpServletResponse();
        var filter = new AuthFilter(requireScanner);
        var mockFilterChain = new MockFilterChain();
        assertThrows(HttpException.class, () -> filter.doFilter(request, response, mockFilterChain));
    }
}
