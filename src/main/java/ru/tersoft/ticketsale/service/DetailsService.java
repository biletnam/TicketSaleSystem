package ru.tersoft.ticketsale.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface DetailsService extends UserDetailsService {
    UserDetails loadUserByUsername(String username);
}
