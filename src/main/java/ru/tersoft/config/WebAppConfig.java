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
        registry.addRedirectViewController("/api/v2/api-docs", "/v2/api-docs");
        registry.addRedirectViewController("/api/swagger-resources/configuration/ui","/swagger-resources/configuration/ui");
        registry.addRedirectViewController("/api/swagger-resources/configuration/security","/swagger-resources/configuration/security");
        registry.addRedirectViewController("/api/swagger-resources", "/swagger-resources");
        registry.addRedirectViewController("/api", "/api/swagger-ui.html");
        registry.addRedirectViewController("/api/", "/api/swagger-ui.html");
        registry.addRedirectViewController("/", "/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/swagger-ui.html**").addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");
        registry.addResourceHandler("/api/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/public/");
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
