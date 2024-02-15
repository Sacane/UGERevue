package fr.pentagon.ugeoverflow.config.security;

import fr.pentagon.ugeoverflow.exception.HttpException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class AuthFilter extends OncePerRequestFilter {
    private final static Logger LOGGER = Logger.getLogger(AuthFilter.class.getName());

    private final RequestMappingHandlerMapping handlerMapping;
    public AuthFilter(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.handlerMapping = requestMappingHandlerMapping;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(isMethodAnnotated(request)) {
            if(SecurityContextHolder.getContext().getAuthentication() == null) {
                LOGGER.severe("user is not authenticated");
                throw HttpException.forbidden("user is not authenticated");
            }
            LOGGER.info("annoted and connected!");
        } else {
            LOGGER.info("not annoted!");
        }
        filterChain.doFilter(request, response);
    }

    private boolean isMethodAnnotated(HttpServletRequest request) {
        HandlerMethod handlerMethod = getHandlerMethod(request);
        if (handlerMethod != null) {
            return handlerMethod.getMethod().isAnnotationPresent(RequireAuthentication.class);
        }
        return false;
    }

    private HandlerMethod getHandlerMethod(HttpServletRequest request) {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo mappingInfo = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();
            if (
                    mappingInfo.getDirectPaths().contains(request.getServletPath()) &&
                            mappingInfo.getMethodsCondition().getMethods().stream().anyMatch(r -> request.getMethod().contains(r.name()))
            ) {
                return handlerMethod;
            }
        }
        return null;
    }

}
