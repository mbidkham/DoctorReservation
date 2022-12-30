package com.blu.reservation;

import com.blu.reservation.model.PanelUser;
import com.blu.reservation.model.Role;
import com.blu.reservation.model.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DoctorReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoctorReservationApplication.class, args);
    }

    @Bean
    public CommandLineRunner demoData(UserRepository repo) {
        return args -> {
            repo.save(new PanelUser("Doki", "0938188902", Role.DOCTOR));
            repo.save(new PanelUser("Patient", "09123341231", Role.PATIENT));
        };
    }
}
