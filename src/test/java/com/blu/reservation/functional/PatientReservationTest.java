package com.blu.reservation.functional;

import com.blu.reservation.AbstractIntegrationSpringTest;
import com.blu.reservation.controller.dto.ReservationDto;
import com.blu.reservation.controller.dto.ReservationPatientViewDto;
import com.blu.reservation.controller.dto.SearchReservationPatientDto;
import com.blu.reservation.controller.dto.TakeOpenTimeDto;
import com.blu.reservation.model.DoctorReservation;
import com.blu.reservation.model.PanelUser;
import com.blu.reservation.model.Role;
import com.blu.reservation.model.repository.ReservationRepository;
import com.blu.reservation.model.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PatientReservationTest extends AbstractIntegrationSpringTest {
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
        registerUser("PATIENT", "09123361386", Role.PATIENT);
    }


    @Test
    void ShouldReturnEmptyListWHENNoAppointmentIsOpen() throws Exception {
        //************************
        //          Given
        //************************
        PanelUser patient = userRepository.findAllByRole(Role.PATIENT).get(0);
        Assertions.assertTrue(reservationRepository.findAll().isEmpty());
        SearchReservationPatientDto input = new SearchReservationPatientDto();
        input.setSearchDate(LocalDateTime.now());
        //************************
        //          WHEN
        //************************
        String availableTimes = performPostRequest("/reservation/" + patient.getId(), input, 200);
        //************************
        //          THEN
        //************************
        JSONArray jsonArray = JsonPath.parse(availableTimes).json();
        Assertions.assertTrue(jsonArray.isEmpty());
    }

    @Test
    void ShouldReturn400WHENPhoneNumberIsNull() throws Exception {
        //************************
        //          Given
        //************************
        addReservationTimes(LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), false, 0);
        PanelUser patient = userRepository.findAllByRole(Role.PATIENT).get(0);
        DoctorReservation openReservation = reservationRepository.findAll().get(0);
        String body = "{\"id\":" + openReservation.getId() +
            ", \"fullName\":\"Mehraneh\"}";
        //************************
        //          WHEN
        //************************
        String restResponseMessage = performPostRequestByJson("/reservation/" + patient.getId(), body, 400);
        //************************
        //          THEN
        //************************
        Assertions.assertEquals("Enter Your Phone Number Pls.;", restResponseMessage);
    }


    @Test
    void ShouldReturnErrorWHENAppointmentIsTaken() throws Exception {
        //************************
        //          Given
        //************************
        PanelUser patient = userRepository.findAllByRole(Role.PATIENT).get(0);
        addReservationTimes(LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), true, patient.getId());
        DoctorReservation openReservation = reservationRepository.findAll().get(0);
        String body = "{\"id\":" + openReservation.getId() +
            ", \"fullName\":\"Mehraneh\"" +
            ",\"phoneNumber\":\"09381879001\"}";
        //************************
        //          WHEN
        //************************
        String restResponseMessage = performPostRequest("/reservation/" + patient.getId(), body, 400);
        //************************
        //          THEN
        //************************
        Assertions.assertEquals("This Time has been taken by another user!", restResponseMessage);
    }

    @Test
    void ShouldReturnEmptyListWHENUserHaveNoReservation() throws Exception {
        //************************
        //          Given
        //************************
        PanelUser patient = userRepository.findAllByRole(Role.PATIENT).get(0);
        //************************
        //          WHEN
        //************************
        String restResponseMessage =
            performGetRequest("/reservation/" + patient.getId() + "/" + patient.getPhoneNumber(), 200);
        //************************
        //          THEN
        //************************
        JSONArray jsonArray = JsonPath.parse(restResponseMessage).json();
        Assertions.assertTrue(jsonArray.isEmpty());
    }

    @Test
    void ShouldReturnUserReservationListPatientView() throws Exception {
        //************************
        //          Given
        //************************
        PanelUser patient = userRepository.findAllByRole(Role.PATIENT).get(0);
        addReservationTimes(LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), true, patient.getId());
        addReservationTimes(LocalDateTime.now().plusMinutes(30), LocalDateTime.now().plusMinutes(60),
            true, patient.getId());
        //************************
        //          WHEN
        //************************
        String reservedList =
            performGetRequest("/reservation/" + patient.getId() + "/" + patient.getPhoneNumber(), 200);
        //************************
        //          THEN
        //************************
        ObjectMapper mapper = new ObjectMapper();
        List<ReservationPatientViewDto> allReservations = new ArrayList<>();
        try {
            allReservations = mapper.readValue(reservedList, mapper.getTypeFactory().constructCollectionType(List.class,
                ReservationPatientViewDto.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(2, allReservations.size());
    }

}
