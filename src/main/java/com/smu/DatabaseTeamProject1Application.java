package com.smu;

import com.smu.repository.LeagueRepository;
import com.smu.service.LeagueService;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication(scanBasePackageClasses = {LeagueService.class, LeagueRepository.class})
@Theme(value = "DatabaseProject_Group8")
@PWA(name = "Database Project by Group8", shortName = "Database Project by Group8", offlineResources = {})
@NpmPackage(value = "line-awesome", version = "1.3.0")
@EnableMongoRepositories(basePackages = {"com.smu.repository"})
public class DatabaseTeamProject1Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(DatabaseTeamProject1Application.class, args);
    }

}
