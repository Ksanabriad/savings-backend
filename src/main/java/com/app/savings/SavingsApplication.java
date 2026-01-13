package com.app.savings;

import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.app.savings.entities.Estudiante;
import com.app.savings.entities.Pago;
import com.app.savings.enums.PagoStatus;
import com.app.savings.enums.TypePago;
import com.app.savings.repository.EstudianteRepository;
import com.app.savings.repository.PagoRepository;

@SpringBootApplication
public class SavingsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SavingsApplication.class, args);
	}

	@Bean
	// Example CommandLineRunner to execute code at startup
	CommandLineRunner commandLineRunner(EstudianteRepository estudianteRepository, PagoRepository pagoRepository) {
		return args -> {
			estudianteRepository.save(Estudiante.builder().id(UUID.randomUUID().toString()).nombre("Katherin")
					.apellido("Sanabria").codigo("12345").programaId("LTA1").build());
			estudianteRepository.save(Estudiante.builder().id(UUID.randomUUID().toString()).nombre("Jose")
					.apellido("Sanchez").codigo("7891").programaId("LTA1").build());
			estudianteRepository.save(Estudiante.builder().id(UUID.randomUUID().toString()).nombre("Manuel")
					.apellido("Sanabria").codigo("3543322").programaId("LTA2").build());
			estudianteRepository.save(Estudiante.builder().id(UUID.randomUUID().toString()).nombre("Ana")
					.apellido("Lopez").codigo("23461").programaId("MTC1").build());
			System.out.println("Savings Application started successfully!");

			TypePago tiposPagos[] = TypePago.values();
			Random random = new Random();
			// Cada estudiante tiene 10 pagos aleatorios
			estudianteRepository.findAll().forEach(estudiante -> {
				for (int i = 0; i < 10; i++) {
					int index = random.nextInt(tiposPagos.length);
					Pago pago = Pago.builder().cantidad(1000 + (int) (Math.random() * 2000))
							.type(tiposPagos[index])
							.status(PagoStatus.CREADO)
							.fecha(LocalDate.now())
							.estudiante(estudiante)
							.build();
					pagoRepository.save(pago);
				}
			});
		};
	}

}
