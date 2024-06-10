package com.lautadev.microservice_transactions.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private Long idAccount;
    private LocalDate dateOfCreation;
    private double balance;
    private String cvu;
    private String alias;
    private UserDTO User;
}
