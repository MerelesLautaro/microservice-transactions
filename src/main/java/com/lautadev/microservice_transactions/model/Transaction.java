package com.lautadev.microservice_transactions.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTransaction;
    @NotNull
    private TypeOfOperation typeOfOperation;
    @NotNull
    private double amount;
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private LocalDateTime dateOfOperation;
    @NotNull
    private Long idAccount;
}