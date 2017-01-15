package ru.tersoft.ticketsale.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import ru.tersoft.ticketsale.service.DetailsService;

import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer
@ComponentScan({ "ru.tersoft.ticketsale" })
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {
    private final AuthenticationManager auth;
    private final DetailsService userDetailsService;
    private final DataSource dataSource;
    private final PasswordEncoder passwordEncoder;
    private final UserTokenEnhancer tokenEnhancer;

    @Autowired
    public OAuth2Config(@Qualifier("authenticationManagerBean") AuthenticationManager auth, PasswordEncoder passwordEncoder, DataSource dataSource, DetailsService userDetailsService, UserTokenEnhancer tokenEnhancer) {
        this.auth = auth;
        this.passwordEncoder = passwordEncoder;
        this.dataSource = dataSource;
        this.userDetailsService = userDetailsService;
        this.tokenEnhancer = tokenEnhancer;
    }

    @Bean
    public JdbcTokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Bean
    protected AuthorizationCodeServices authorizationCodeServices() {
        return new JdbcAuthorizationCodeServices(dataSource);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security)
            throws Exception {
        security.allowFormAuthenticationForClients();
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints)
            throws Exception {
        endpoints.authorizationCodeServices(authorizationCodeServices())
                .authenticationManager(auth)
                .tokenStore(tokenStore())
                .approvalStoreDisabled()
                .userDetailsService(userDetailsService)
                .tokenEnhancer(tokenEnhancer);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
                .jdbc(dataSource)
                .passwordEncoder(passwordEncoder);
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setTokenStore(tokenStore());
        tokenServices.setTokenEnhancer(tokenEnhancer);
        return tokenServices;
    }
}