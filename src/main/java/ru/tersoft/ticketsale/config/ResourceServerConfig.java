package ru.tersoft.ticketsale.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableResourceServer
@ComponentScan({ "ru.tersoft.ticketsale" })
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    private final TokenStore tokenStore;

    @Autowired
    public ResourceServerConfig(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources)
            throws Exception {
        resources.tokenStore(tokenStore);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(OPTIONS, "/**").permitAll()
                .antMatchers(POST, "/api/accounts").permitAll()
                .antMatchers(GET, "/**").permitAll()
                .antMatchers(GET, "/api/accounts/check/**").permitAll()
                .antMatchers(GET, "/api/attractions/**").permitAll()
                .antMatchers(GET, "/apidocs/**").permitAll()
                .antMatchers(GET, "/webjars/springfox-swagger-ui/**").permitAll()
                .antMatchers(GET, "/v2/api-docs/**").permitAll()
                .antMatchers(GET, "/configuration/**").permitAll()
                .antMatchers(GET, "/swagger-resources/**").permitAll()
                .anyRequest().authenticated();

    }

}