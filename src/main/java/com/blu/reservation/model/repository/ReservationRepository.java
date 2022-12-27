package com.blu.reservation.model.repository;

import com.blu.reservation.model.DoctorReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<DoctorReservation,Integer> {
    List<DoctorReservation> findAll();

    List<DoctorReservation> findAllByPatientNotNull();

    List<DoctorReservation> findAllByStartTimeAfterAndEndTimeBefore(LocalDateTime startDate, LocalDateTime endTime);

    List<DoctorReservation> findAllByPatient_PhoneNumber(String phoneNumber);

}
