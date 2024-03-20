package fr.pentagon.ugeoverflow.config.security;

import fr.pentagon.revevue.common.exception.HttpException;
import fr.pentagon.ugeoverflow.config.authentication.RevevueUserDetail;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class AuthenticationChecker {

    private AuthenticationChecker() {
        throw new AssertionError("Cannot instantiate this class.");
    }

    public static RevevueUserDetail checkAuthentication() throws HttpException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName().equalsIgnoreCase("anonymousUser") || !auth.isAuthenticated()) {
            throw HttpException.unauthorized("User is not authenticated");
        }
        return (RevevueUserDetail) auth.getPrincipal();
    }

    public static Optional<RevevueUserDetail> authentication(){
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }
        return Optional.of((RevevueUserDetail) auth.getPrincipal());
    }

}
