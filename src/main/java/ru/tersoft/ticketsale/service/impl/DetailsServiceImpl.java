package ru.tersoft.ticketsale.service.impl;

import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tersoft.ticketsale.entity.Account;
import ru.tersoft.ticketsale.repository.AccountRepository;
import ru.tersoft.ticketsale.service.DetailsService;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.List;

@Service("DetailsService")
@Transactional(rollbackFor=LockAcquisitionException.class)
public class DetailsServiceImpl implements DetailsService {
    private final AccountRepository accountRepository;

    @Autowired
    public DetailsServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            username = URLDecoder.decode(username, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //TODO: Write exception handler
        }
        List<Account> accounts = (List<Account>)accountRepository.findByMail(username);
        if(accounts.size() != 0) {
            Account user = accounts.get(0);
            return new org.springframework.security.core.userdetails.User(user.getMail(),
                    user.getPassword(),
                    user.isEnabled(), true, true, true,
                    getGrantedAuthorities(user));
        }
        else {
            throw new UsernameNotFoundException("User " + username + " not found in database.");
        }
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
