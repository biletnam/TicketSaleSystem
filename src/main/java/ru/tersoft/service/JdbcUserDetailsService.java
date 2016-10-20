package ru.tersoft.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.tersoft.TicketsaleApplication;
import ru.tersoft.entity.Account;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Component
public class JdbcUserDetailsService implements UserDetailsService {
    private static final Logger LOG = LoggerFactory.getLogger(TicketsaleApplication.class);

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            username = URLDecoder.decode(username, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.info("### loadUserByUsername: " + e);
        }
        Account user = userService.findUserByMail(username);
        LOG.info("Hello " + user.getFirstname() + "");
        if (user == null) {
            LOG.info("User with username " + username + " not found in DB");
            throw new UsernameNotFoundException("User " + username + " not found in database.");
        }
        return new org.springframework.security.core.userdetails.User(user.getMail(),
                user.getPassword(),
                user.isEnabled(), true, true, true,
                AuthorityUtils.createAuthorityList("USER", "write"));
    }
}
