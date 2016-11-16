package ru.tersoft.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
public class WebAppConfig extends WebMvcConfigurerAdapter {
    private final Environment env;

    @Autowired
    public WebAppConfig(Environment env) {
        this.env = env;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/api", "/swagger-ui.html");
        registry.addRedirectViewController("/api/", "/swagger-ui.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        String prefix = env.getProperty("ticketsale.images-folder-prefix");
        if(prefix != null) {
            registry.addResourceHandler("/images/**")
                    .addResourceLocations(prefix + ":"
                            + env.getProperty("ticketsale.images-folder"));
        } else {
            registry.addResourceHandler("/images/**")
                    .addResourceLocations(env.getProperty("ticketsale.images-folder"));
        }
    }

}
