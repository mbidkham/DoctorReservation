package com.blu.reservation;

import com.blu.reservation.model.DoctorReservation;
import com.blu.reservation.model.PanelUser;
import com.blu.reservation.model.Role;
import com.blu.reservation.model.repository.ReservationRepository;
import com.blu.reservation.model.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.ANY)
@ContextConfiguration()
@ComponentScan(value = "com.blu.reservation")
public abstract class AbstractIntegrationSpringTest extends SpringMockMVCHelper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public void registerUser(String name, String phoneNumber, Role role) {
        PanelUser user = new PanelUser(name, phoneNumber, role);
        userRepository.save(user);
    }

    public void addReservationTimes(LocalDateTime start, LocalDateTime end, boolean isReserved, int patientId) {
        PanelUser patient = null;
        if (patientId != 0) {
            patient = userRepository.findById(patientId).orElseThrow();
        }
        DoctorReservation doctorReservation = DoctorReservation
            .builder()
            .startTime(start)
            .endTime(end)
            .reserved(isReserved)
            .patient(patient)
            .build();
        reservationRepository.save(doctorReservation);
    }
}
