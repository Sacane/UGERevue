package fr.pentagon.ugeoverflow.config.authorization;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole(T(fr.pentagon.ugeoverflow.config.authorization.Role).USER.roleName()) || hasRole(T(fr.pentagon.ugeoverflow.config.authorization.Role).ADMIN.roleName())")
public @interface RequireUser {
}
