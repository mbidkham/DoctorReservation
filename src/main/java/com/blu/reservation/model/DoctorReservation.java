package com.blu.reservation.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Version
    private Integer version;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean reserved;
    @OneToOne
    @JoinColumn(name = "PATIENT_ID")
    private PanelUser patient;

    public DoctorReservation(LocalDateTime startTime, LocalDateTime endTime, boolean reserved, PanelUser patient) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.reserved = reserved;
        this.patient = patient;
    }
}
