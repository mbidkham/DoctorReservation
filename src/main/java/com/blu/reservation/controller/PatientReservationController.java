package com.blu.reservation.controller;

import com.blu.reservation.controller.dto.ReservationPatientViewDto;
import com.blu.reservation.controller.dto.SearchReservationPatientDto;
import com.blu.reservation.controller.dto.TakeOpenTimeDto;
import com.blu.reservation.service.PatientReservationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/reservation")
@AllArgsConstructor
public class PatientReservationController {

    private final PatientReservationService patientReservationService;

    @PostMapping("/available/{userId}")
    public ResponseEntity<List<ReservationPatientViewDto>> seeAvailableTimes(@PathVariable Integer userId,
                                                                             @RequestBody SearchReservationPatientDto inputDate) {
        return new ResponseEntity<>(patientReservationService.getAllOpenTimes(inputDate.getSearchDate()), HttpStatus.OK);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<String> addTimes(@Valid @RequestBody TakeOpenTimeDto inputData,
                                           @PathVariable Integer userId) {
        patientReservationService.reserveOpenTime(inputData, userId);
        return new ResponseEntity<>(RestMessages.RESERVE_TIME_MESSAGE, HttpStatus.OK);
    }
    @GetMapping("/{userId}/{phoneNumber}")
    public ResponseEntity<List<ReservationPatientViewDto>> getReservedTimes(@PathVariable Integer userId,
                                                                            @PathVariable String phoneNumber) {
        return new ResponseEntity<>(patientReservationService.getReservedTimesByUser(phoneNumber), HttpStatus.OK);
    }
}
