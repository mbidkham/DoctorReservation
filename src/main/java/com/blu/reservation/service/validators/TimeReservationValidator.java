package com.blu.reservation.service.validators;

import com.blu.reservation.exception.DataNotFoundException;
import com.blu.reservation.exception.RestResponseException;
import com.blu.reservation.model.DoctorReservation;
import com.blu.reservation.model.repository.ReservationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
@Service
@AllArgsConstructor
public class TimeReservationValidator {

    private final ReservationRepository reservationRepository;

    public void addNewTimesValidation(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new RestResponseException("End date is entered before start date!");
        } else {
            long differenceInMinutes = ChronoUnit.MINUTES.between(start, end);
            if (differenceInMinutes < 30){
                throw new RestResponseException("No time added for you!");
            }
        }
    }

    public void deleteOldTimesValidation(int timeId) {
        DoctorReservation reservation = reservationRepository.findById(timeId)
            .orElseThrow(() -> new DataNotFoundException("This Reservation doesn't exist."));
        if (reservation.getPatient() != null) {
            throw new RestResponseException("Can not delete, This Reservation time has been taken by a user!");
        }

    }
}
