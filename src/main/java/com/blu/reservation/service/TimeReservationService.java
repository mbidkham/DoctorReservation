package com.blu.reservation.service;

import com.blu.reservation.controller.dto.AddAvailableTimeDto;
import com.blu.reservation.controller.dto.ReservationDto;
import com.blu.reservation.exception.RestResponseException;
import com.blu.reservation.model.DoctorReservation;
import com.blu.reservation.model.repository.ReservationRepository;
import com.blu.reservation.service.validators.TimeReservationValidator;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TimeReservationService {

    private final ModelMapper modelMapper;
    private final ReservationRepository reservationRepository;
    private final TimeReservationValidator timeReservationValidator;

    public void addDoctorTimes(AddAvailableTimeDto input) {
        timeReservationValidator.addNewTimesValidation(input.getStartTime(), input.getEndTime());
        reservationRepository.saveAll(splitTimeIntoMultipleReservations(input.getStartTime(), input.getEndTime()));
    }
    public List<ReservationDto> getDoctorAvailableTimes(){
        List<DoctorReservation> reservationList = reservationRepository.findAll();
        if(reservationList.isEmpty()){
            return Collections.emptyList();
        }
        else {
            return reservationList
                .stream()
                .map(doctorReservation -> modelMapper.map(doctorReservation, ReservationDto.class))
                .collect(Collectors.toList());
        }
    }

    @Transactional()
    public void deleteNotTakenTimes(int timeId){
        timeReservationValidator.deleteOldTimesValidation(timeId);
        long hasDeleted = reservationRepository.deleteByIdAndPatientIsNull(timeId);
        if(hasDeleted != 1){
            throw new RestResponseException("Can not delete, This Reservation time has been taken by a user!");
        }
    }

    private ArrayList<DoctorReservation> splitTimeIntoMultipleReservations(LocalDateTime start, LocalDateTime end) {
        long splitSteps = ChronoUnit.MINUTES.between(start, end) / 30;
        ArrayList<DoctorReservation> reservations = new ArrayList<>();
        while (splitSteps > 0) {
            LocalDateTime after30Minutes = start.plusMinutes(30);
            reservations.add(DoctorReservation
                .builder()
                .startTime(start)
                .endTime(after30Minutes)
                .build());
            start = after30Minutes;
            splitSteps--;
        }
        return reservations;
    }


}
