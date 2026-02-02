package com.app.savings.config;

import com.app.savings.services.InformeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InformeInitializer implements CommandLineRunner {

    @Autowired
    private InformeService informeService;

    @Override
    public void run(String... args) throws Exception {
        informeService.generarInformesFaltantes();
    }
}
