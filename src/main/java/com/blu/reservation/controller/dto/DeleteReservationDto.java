package com.blu.reservation.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.jackson.JsonComponent;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonComponent
public class DeleteReservationDto {
    private int id;
}
