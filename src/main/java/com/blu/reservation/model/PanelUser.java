package com.blu.reservation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class PanelUser {

    public PanelUser(String fullName, String phoneNumber, Role role) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String fullName;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private Role role;


}
