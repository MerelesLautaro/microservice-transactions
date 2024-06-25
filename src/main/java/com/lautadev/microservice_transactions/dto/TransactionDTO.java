package com.lautadev.microservice_transactions.dto;

import com.lautadev.microservice_transactions.model.Transaction;
import com.lautadev.microservice_transactions.model.TypeOfOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long idTransaction;
    private TypeOfOperation typeOfOperation;
    private double amount;
    private LocalDateTime dateOfOperation;
    private AccountDTO accountDTO;

    public TransactionDTO(AccountDTO accountDTO, Transaction transaction) {
        this.idTransaction = transaction.getIdTransaction();
        this.typeOfOperation = transaction.getTypeOfOperation();
        this.amount = transaction.getAmount();
        this.dateOfOperation = transaction.getDateOfOperation();
        this.accountDTO = accountDTO;
    }
}
