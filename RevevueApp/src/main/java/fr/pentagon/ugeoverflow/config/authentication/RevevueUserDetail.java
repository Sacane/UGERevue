package fr.pentagon.ugeoverflow.config.authentication;

import fr.pentagon.ugeoverflow.config.authorization.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class RevevueUserDetail implements UserDetails {

  private final long id;
  private final String password;
  private final String login;
  private final List<GrantedAuthority> authorities;

  public RevevueUserDetail(long id, String password, String login, Role role) {
    this.id = id;
    this.password = password;
    this.login = login;
    authorities = List.of(new SimpleGrantedAuthority(role.authorityName()));
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
    return login;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
