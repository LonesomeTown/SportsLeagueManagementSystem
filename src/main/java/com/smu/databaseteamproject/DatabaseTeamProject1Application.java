package com.smu.databaseteamproject;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme(value = "DatabaseProject_Group8")
@PWA(name = "Database Project by Group8", shortName = "Database Project by Group8", offlineResources = {})
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class DatabaseTeamProject1Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(DatabaseTeamProject1Application.class, args);
    }

}
