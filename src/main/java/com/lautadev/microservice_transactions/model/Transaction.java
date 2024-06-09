package com.lautadev.microservice_transactions.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTransaction;
    @ElementCollection(targetClass = TypeOfOperation.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "type_of_operation", joinColumns = @JoinColumn(name = "operation_id"))
    @Column(name = "type_of_operation")
    @Size(min = 1)
    private TypeOfOperation typeOfOperation;
    @NotNull
    private double amount;
    @Temporal(TemporalType.DATE)
    @NotNull
    @PastOrPresent
    private LocalDate dateOfOperation;
    @NotNull
    private Long idAccount;
}
