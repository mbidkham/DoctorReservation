package com.blu.reservation.controller.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TakeOpenTimeDto {

    private Integer id;
    @NotNull(message = "Enter Your Full Name Pls." )
    private String fullName;
    @NotNull(message = "Enter Your Phone Number Pls.")
    private String phoneNumber;
}
