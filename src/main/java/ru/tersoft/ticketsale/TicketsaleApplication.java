package ru.tersoft.ticketsale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import ru.tersoft.ticketsale.service.CleaningService;


@SpringBootApplication
public class TicketsaleApplication extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(TicketsaleApplication.class);
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(TicketsaleApplication.class, args);
        CleaningService cleaningService = ctx.getBean(CleaningService.class);
        cleaningService.cleanMaintenances();
        cleaningService.disableTickets();
	}
}
