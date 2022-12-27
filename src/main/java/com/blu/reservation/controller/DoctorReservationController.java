package com.blu.reservation.controller;

import com.blu.reservation.aspect.CheckAccess;
import com.blu.reservation.controller.dto.AddAvailableTimeDto;
import com.blu.reservation.controller.dto.ReservationDto;
import com.blu.reservation.model.Role;
import com.blu.reservation.service.TimeReservationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@AllArgsConstructor
@RequestMapping("/appointment")
public class DoctorReservationController {

    private final TimeReservationService timeReservationService;

    @CheckAccess(Role.Constants.DOCTOR)
    @PostMapping("/{userId}")
    public ResponseEntity<String> addTimes(@RequestBody AddAvailableTimeDto inputData, @PathVariable Integer userId) {
        timeReservationService.addDoctorTimes(inputData);
        return new ResponseEntity<>(RestMessages.ADD_AVAILABLE_TIME_MESSAGE, HttpStatus.OK);
    }

    @CheckAccess(Role.Constants.DOCTOR)
    @GetMapping("/{userId}")
    public ResponseEntity<List<ReservationDto>> seeAvailableTimes(@PathVariable Integer userId) {
        return new ResponseEntity<>(timeReservationService.getDoctorAvailableTimes(), HttpStatus.OK);
    }

    @CheckAccess(Role.Constants.DOCTOR)
    @DeleteMapping("/{userId}/{timeId}")
    public ResponseEntity<String> deleteAvailableTimes(@PathVariable Integer userId,
                                                       @PathVariable Integer timeId) {
        timeReservationService.deleteNotTakenTimes(timeId);
        return new ResponseEntity<>(RestMessages.DELETE_NOT_TAKEN_TIME_MESSAGE, HttpStatus.OK);
    }

}
