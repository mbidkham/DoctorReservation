package com.blu.reservation.service;

import com.blu.reservation.controller.dto.ReservationPatientViewDto;
import com.blu.reservation.controller.dto.TakeOpenTimeDto;
import com.blu.reservation.exception.DataNotFoundException;
import com.blu.reservation.exception.RestResponseException;
import com.blu.reservation.model.DoctorReservation;
import com.blu.reservation.model.PanelUser;
import com.blu.reservation.model.repository.ReservationRepository;
import com.blu.reservation.model.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PatientReservationService {

    private final ModelMapper modelMapper;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;


    public List<ReservationPatientViewDto> getAllOpenTimes(LocalDateTime inputDate) {
        LocalDateTime startOfDay = inputDate.with(LocalTime.MIN);
        LocalDateTime endOfDay = inputDate.with(LocalTime.MAX);
        List<DoctorReservation> reservationList =
            reservationRepository.findAllByStartTimeAfterAndEndTimeBefore(startOfDay, endOfDay);
        if (reservationList.isEmpty()) {
            return Collections.emptyList();
        } else {
            return reservationList
                .stream()
                .map(doctorReservation -> modelMapper.map(doctorReservation, ReservationPatientViewDto.class))
                .collect(Collectors.toList());
        }
    }

    public List<ReservationPatientViewDto> getReservedTimesByUser(String phoneNumber) {
        List<DoctorReservation> reservationList = reservationRepository.findAllByPatient_PhoneNumber(phoneNumber);
        return reservationList
            .stream()
            .map(doctorReservation -> modelMapper.map(doctorReservation, ReservationPatientViewDto.class))
            .collect(Collectors.toList());
    }

    @Transactional()
    public void reserveOpenTime(TakeOpenTimeDto takeOpenTimeDto, Integer userId) {
        PanelUser currentUser = userRepository.findById(userId).orElseThrow();
        currentUser.setFullName(takeOpenTimeDto.getFullName());
        currentUser.setPhoneNumber(takeOpenTimeDto.getPhoneNumber());
        userRepository.save(currentUser);

        DoctorReservation reservation = reservationRepository.findById(takeOpenTimeDto.getId())
            .orElseThrow(() -> new DataNotFoundException("This Reservation doesn't exist or has been deleted."));
        if (Objects.nonNull(reservation.getPatient())) {
            throw new RestResponseException("This Time has been taken by another user!");
        }
        reservation.setPatient(currentUser);
        try {
            reservationRepository.save(reservation);
        }catch (InvalidDataAccessApiUsageException deletedObjectException){
            throw new RestResponseException("This Reservation doesn't exist or has been deleted.");
        }
    }

}
