package ru.tersoft.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.tersoft.entity.Account;

import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;

@Component
@Transactional
public class JdbcUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Autowired
    public JdbcUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            username = URLDecoder.decode(username, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //TODO: Write exception handler
        }
        Account user = userService.findUserByMail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User " + username + " not found in database.");
        }
        return new org.springframework.security.core.userdetails.User(user.getMail(),
                user.getPassword(),
                user.isEnabled(), true, true, true,
                getGrantedAuthorities(user));
    }

    private Collection<? extends GrantedAuthority> getGrantedAuthorities(Account user) {
        Collection<? extends GrantedAuthority> authorities;
        if (user.isAdmin()) {
            authorities = AuthorityUtils.createAuthorityList("ADMIN", "USER");
        } else {
            authorities = AuthorityUtils.createAuthorityList("USER");
        }
        return authorities;
    }
}
