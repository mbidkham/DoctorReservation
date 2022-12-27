package com.blu.reservation.functional;

import com.blu.reservation.AbstractIntegrationSpringTest;
import com.blu.reservation.controller.dto.DeleteReservationDto;
import com.blu.reservation.controller.dto.ReservationDto;
import com.blu.reservation.model.DoctorReservation;
import com.blu.reservation.model.PanelUser;
import com.blu.reservation.model.Role;
import com.blu.reservation.model.repository.ReservationRepository;
import com.blu.reservation.model.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DoctorTimesTest extends AbstractIntegrationSpringTest {
    @RegisterExtension
    static WireMockExtension wiremock = WireMockExtension.newInstance()
        .options(WireMockConfiguration.wireMockConfig().port(8089).usingFilesUnderClasspath("wiremock"))
        .build();

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void init() {
        registerUser("Doki", "09381879001", Role.DOCTOR);
        registerUser("PATIENT", "09123361386", Role.PATIENT);
    }

    @Test
    void ShouldThrowExceptionWHENStartIsfAfterEndTime() throws Exception {
        //************************
        //          Given
        //************************
        PanelUser doctor = userRepository.findAllByRole(Role.DOCTOR).get(0);
        ReservationDto input = new ReservationDto();
        input.setStartTime(LocalDateTime.now().plusHours(4));
        input.setEndTime(LocalDateTime.now());
        //************************
        //          WHEN
        //************************
        String restResponseMessage = performPostRequest("/appointment/" + doctor.getId(), input, 400);
        //************************
        //          THEN
        //************************
        Assertions.assertEquals("End date is entered before start date!", restResponseMessage);
    }

    @Test
    void NoTimeAddedWHENTimePeriodIsLessThan30Minutes() throws Exception {
        //************************
        //          Given
        //************************
        PanelUser doctor = userRepository.findAllByRole(Role.DOCTOR).get(0);
        ReservationDto input = new ReservationDto();
        input.setStartTime(LocalDateTime.now());
        input.setEndTime(LocalDateTime.now().plusMinutes(12));
        //************************
        //          WHEN
        //************************
        String restResponseMessage = performPostRequest("/appointment/" + doctor.getId(), input, 400);
        //************************
        //          THEN
        //************************
        Assertions.assertEquals("No time added for you!", restResponseMessage);
        Assertions.assertEquals(0, reservationRepository.findAll().size());

    }

    @Test
    void DoctorSeeEmptyListWhenNoTimeIsAvailable() throws Exception {
        //************************
        //          Given
        //************************
        PanelUser doctor = userRepository.findAllByRole(Role.DOCTOR).get(0);
        //************************
        //          WHEN
        //************************
        String availableTimes = performGetRequest("/appointment/" + doctor.getId(), 200);
        //************************
        //          THEN
        //************************
        JSONArray jsonArray = JsonPath.parse(availableTimes).json();
        Assertions.assertTrue(jsonArray.isEmpty());

    }

    @Test
    void DoctorSeeAllTimesAndUsersInfos() throws Exception {
        //************************
        //          Given
        //************************
        PanelUser doctor = userRepository.findAllByRole(Role.DOCTOR).get(0);
        addReservationTimes(LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), true, 2);
        addReservationTimes(LocalDateTime.now().plusMinutes(30), LocalDateTime.now().plusMinutes(60), false, 0);
        //************************
        //          WHEN
        //************************
        String availableTimes = performGetRequest("/appointment/" + doctor.getId(), 200);
        //************************
        //          THEN
        //************************
        ObjectMapper mapper = new ObjectMapper();
        List<ReservationDto> allReservations = new ArrayList<>();
        try {
            allReservations = mapper
                .readValue(availableTimes, mapper.getTypeFactory().constructCollectionType(List.class, ReservationDto.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(2, allReservations.size());
        Assertions.assertEquals("PATIENT", allReservations.get(0).getPatient().getFullName());
        Assertions.assertEquals("09123361386", allReservations.get(0).getPatient().getPhoneNumber());
    }

    @Test
    void ShouldGet404WHENDoctorDeleteAppointmentWhichIsNotExist() throws Exception {
        //************************
        //          Given
        //************************
        PanelUser doctor = userRepository.findAllByRole(Role.DOCTOR).get(0);
        addReservationTimes(LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), true, 2);
        addReservationTimes(LocalDateTime.now().plusMinutes(30), LocalDateTime.now().plusMinutes(60), false, 0);
        //************************
        //          WHEN
        //************************
        String restResponseMessage = performDeleteRequest("/appointment/" + doctor.getId() + "123", 404);
        //************************
        //          THEN
        //************************
        Assertions.assertEquals("This Reservation doesn't exist.", restResponseMessage);

    }

    @Test
    void ShouldGet406WHENDoctorDeleteTakenAppointment() throws Exception {
        //************************
        //          Given
        //************************
        PanelUser doctor = userRepository.findAllByRole(Role.DOCTOR).get(0);
        addReservationTimes(LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), true, 2);
        addReservationTimes(LocalDateTime.now().plusMinutes(30), LocalDateTime.now().plusMinutes(60),
            false, 0);
        int takenReservationId = reservationRepository.findAllByPatientNotNull().get(0).getId();
        //************************
        //          WHEN
        //************************
        String restResponseMessage = performDeleteRequest("/appointment/" + doctor.getId() + takenReservationId, 406);
        //************************
        //          THEN
        //************************
        Assertions.assertEquals("This Reservation time has been taken by a user!", restResponseMessage);
    }
}

