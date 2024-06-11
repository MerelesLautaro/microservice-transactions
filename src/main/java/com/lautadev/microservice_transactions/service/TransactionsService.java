package com.lautadev.microservice_transactions.service;

import com.lautadev.microservice_transactions.Throwable.TransactionValidator;
import com.lautadev.microservice_transactions.dto.AccountDTO;
import com.lautadev.microservice_transactions.dto.TransactionDTO;
import com.lautadev.microservice_transactions.dto.UpdateBalanceDTO;
import com.lautadev.microservice_transactions.model.Transaction;
import com.lautadev.microservice_transactions.model.TypeOfOperation;
import com.lautadev.microservice_transactions.repository.IAccountAPIClient;
import com.lautadev.microservice_transactions.repository.ITransactionsRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionsService implements ITransactionsService {
    @Autowired
    private ITransactionsRepository transactionRepo;

    @Autowired
    private IAccountAPIClient accountAPI;

    @Autowired
    private TransactionValidator validator;

    @Override
    public void saveTransaction(Transaction transaction) {
        validator.validate(transaction);

        if(transaction.getTypeOfOperation().equals(TypeOfOperation.MoneyReceived)
                || transaction.getTypeOfOperation().equals(TypeOfOperation.BalanceTopUp)){
            UpdateBalanceDTO updateBalanceDTO = new UpdateBalanceDTO(transaction.getAmount(), transaction.getTypeOfOperation());
            accountAPI.updateBalance(transaction.getIdAccount(),updateBalanceDTO);
        }

        validator.validate(transaction);
        transactionRepo.save(transaction);
    }

    @Override
    public List<Transaction> getTransactions() {
        return transactionRepo.findAll();
    }

    @Override
    @CircuitBreaker(name = "microservice-account",fallbackMethod = "fallBackFindTransactionAndAccount")
    @Retry(name = "microservice-account")
    public TransactionDTO findTransactionAndAccount(Long idTransaction) {
        Transaction transaction = transactionRepo.findById(idTransaction).orElse(null);
        assert transaction != null;
        AccountDTO accountDTO = accountAPI.findAccountAndUser(transaction.getIdAccount());
        return new TransactionDTO(accountDTO,transaction);
    }

    public String fallBackFindTransactionAndAccount(Throwable throwable){
        return "algo salio mal";
    }

    @Override
    public Transaction findTransaction(Long idTransaction) {
        return transactionRepo.findById(idTransaction).orElse(null);
    }

    @Override
    public void deleteTransaction(Long idTransaction) {
        transactionRepo.deleteById(idTransaction);
    }

    @Override
    public void editTransaction(Long idTransaction, Transaction transaction) {
        Transaction transactionEdit = transactionRepo.findById(idTransaction).orElse(null);

        assert transactionEdit != null;
        transactionEdit.setTypeOfOperation(transaction.getTypeOfOperation());
        transactionEdit.setAmount(transaction.getAmount());
        transactionEdit.setDateOfOperation(transaction.getDateOfOperation());
        transactionEdit.setIdAccount(transaction.getIdAccount());

        this.saveTransaction(transactionEdit);
    }
}
