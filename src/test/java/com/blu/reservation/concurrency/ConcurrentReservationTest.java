package com.blu.reservation.concurrency;

import com.blu.reservation.AbstractIntegrationSpringTest;
import com.blu.reservation.exception.RestResponseException;
import com.blu.reservation.model.DoctorReservation;
import com.blu.reservation.model.PanelUser;
import com.blu.reservation.model.Role;
import com.blu.reservation.model.repository.ReservationRepository;
import com.blu.reservation.model.repository.UserRepository;
import com.blu.reservation.service.TimeReservationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.annotation.DirtiesContext;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ConcurrentReservationTest extends AbstractIntegrationSpringTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TimeReservationService timeReservationService;

    @BeforeEach
    void initDB() {
        // CLEAR DB
        reservationRepository.deleteAll();
        // SETUP
        registerUser("Doki", "09382872001", Role.DOCTOR);
        registerUser("PATIENT1", "09123381246", Role.PATIENT);
        registerUser("PATIENT2", "09123456789", Role.PATIENT);
    }

    @Test
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    void Should_Return_Exception_WHEN_Doctor_Delete_Committed_Taken_Reservation() {
        //************************
        //          Given
        //************************
        PanelUser patient = userRepository.findAllByRole(Role.PATIENT).get(0);
        addReservationTimes(LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), false, 0);
        DoctorReservation openReservation = reservationRepository.findAll().get(0);
        //************************
        //          WHEN
        //************************
        openReservation.setPatient(patient);
        reservationRepository.save(openReservation);
        //************************
        //          THEN
        //************************
        int reserveId = openReservation.getId();
        Assertions.assertThrows(RestResponseException.class,
            () -> timeReservationService.deleteNotTakenTimes(reserveId));
        Assertions.assertEquals(reservationRepository.findAll().get(0).getPatient(), patient);
    }


    @Test
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    void Should_Return_Exception_WHEN_Concurrent_Reserve_And_Delete_Happens() {
        //************************
        //          Given
        //************************
        PanelUser patient = userRepository.findAllByRole(Role.PATIENT).get(0);
        addReservationTimes(LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), false, 0);
        DoctorReservation takenReservation = reservationRepository.findAll().get(0);
        DoctorReservation openReservation = reservationRepository.findAll().get(0);
        //************************
        //          WHEN
        //************************
        timeReservationService.deleteNotTakenTimes(openReservation.getId());
        takenReservation.setPatient(patient);
        //************************
        //          THEN
        //************************
        Assertions.assertThrows(InvalidDataAccessApiUsageException.class,
            () -> reservationRepository.save(takenReservation));
        Assertions.assertTrue(reservationRepository.findAll().isEmpty());
    }

    @Test
    @Transactional(value = Transactional.TxType.NEVER)
    void Should_Throw_Optimistic_Locking_When_Two_Patients_Reserve_Same_Times() {
        //************************
        //          Given
        //************************
        List<PanelUser> patiList = userRepository.findAllByRole(Role.PATIENT);
        PanelUser patient1 = patiList.get(0);
        PanelUser patient2 = patiList.get(1);
        addReservationTimes(LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), false, 0);
        DoctorReservation openReservation1 = reservationRepository.findAll().get(0);
        DoctorReservation openReservation2 = reservationRepository.findAll().get(0);
        //************************
        //          WHEN
        //************************
        openReservation1.setPatient(patient1);
        reservationRepository.save(openReservation1);

        openReservation2.setPatient(patient2);
        //************************
        //          THEN
        //************************
        Assertions.assertThrows(OptimisticLockingFailureException.class,
            () -> reservationRepository.save(openReservation2));
        openReservation1.setVersion(openReservation1.getVersion()+1);
        Assertions.assertEquals(reservationRepository.findAll().get(0), openReservation1);
    }
}
