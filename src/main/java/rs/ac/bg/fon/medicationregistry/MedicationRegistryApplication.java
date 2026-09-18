package rs.ac.bg.fon.medicationregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MedicationRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedicationRegistryApplication.class, args);
	}

}
