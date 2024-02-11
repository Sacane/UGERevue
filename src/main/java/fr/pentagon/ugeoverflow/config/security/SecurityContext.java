package fr.pentagon.ugeoverflow.config.security;

import fr.pentagon.ugeoverflow.config.auth.RevevueUserDetail;
import fr.pentagon.ugeoverflow.exception.HttpException;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityContext {

    private SecurityContext(){
        throw new AssertionError("Cannot instantiate this class.");
    }

    public static RevevueUserDetail checkAuthentication() throws HttpException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw HttpException.unauthorized("");
        }
        return (RevevueUserDetail) auth.getPrincipal();
    }

}
