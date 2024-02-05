package fr.pentagon.ugeoverflow.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static java.util.Collections.emptyList;

public class RevevueUserDetail implements UserDetails {

    private final long id;
    private final String password;
    private final String username;

    private final Collection<? extends GrantedAuthority> authorities;

    public RevevueUserDetail(long id, String password, String username) {
        this.id = id;
        this.password = password;
        this.username = username;
        this.authorities = emptyList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }


    public long id() {
        return id;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
