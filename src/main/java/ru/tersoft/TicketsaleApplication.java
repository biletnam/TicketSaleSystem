package ru.tersoft;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import javax.sql.DataSource;


@SpringBootApplication
public class TicketsaleApplication {

    @Autowired
    private DataSource dataSource;

	public static void main(String[] args) {
		SpringApplication.run(TicketsaleApplication.class, args);
	}

    @Autowired
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        // @formatter:off
        auth.jdbcAuthentication().dataSource(dataSource).withUser("dave")
                .password("secret").roles("USER");
        // @formatter:on
    }
}
