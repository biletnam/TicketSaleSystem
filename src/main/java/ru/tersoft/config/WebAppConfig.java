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
        registry.addRedirectViewController("/apidocs/v2/api-docs", "/v2/api-docs");
        registry.addRedirectViewController("/apidocs/swagger-resources/configuration/ui","/swagger-resources/configuration/ui");
        registry.addRedirectViewController("/apidocs/swagger-resources/configuration/security","/swagger-resources/configuration/security");
        registry.addRedirectViewController("/apidocs/swagger-resources", "/swagger-resources");
        registry.addRedirectViewController("/apidocs", "/apidocs/swagger-ui.html");
        registry.addRedirectViewController("/apidocs/", "/apidocs/swagger-ui.html");
        registry.addRedirectViewController("/", "/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/apidocs/swagger-ui.html**").addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");
        registry.addResourceHandler("/apidocs/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/public/");
    }

}
