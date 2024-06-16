package com.lautadev.microservice_transactions.service;

import com.lautadev.microservice_transactions.Throwable.TransactionValidator;
import com.lautadev.microservice_transactions.dto.AccountDTO;
import com.lautadev.microservice_transactions.dto.TransactionDTO;
import com.lautadev.microservice_transactions.dto.UpdateBalanceDTO;
import com.lautadev.microservice_transactions.model.Transaction;
import com.lautadev.microservice_transactions.repository.IAccountAPIClient;
import com.lautadev.microservice_transactions.repository.ITransactionsRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionsService implements ITransactionsService {

    private final ITransactionsRepository transactionRepo;
    private final IAccountAPIClient accountAPI;
    private final TransactionValidator validator;

    @Autowired
    public TransactionsService(ITransactionsRepository transactionRepo, IAccountAPIClient accountAPI, TransactionValidator validator){
        this.transactionRepo = transactionRepo;
        this.accountAPI = accountAPI;
        this.validator = validator;
    }

    public static final Logger logger = LoggerFactory.getLogger(TransactionsService.class);

    @Override
    @CircuitBreaker(name = "microservice-account",fallbackMethod = "fallBackUpdateBalanceAccount")
    @Retry(name = "microservice-account")
    @Transactional
    public void saveTransaction(Transaction transaction, String aliasOrCvu) {
        validator.validate(transaction);
        UpdateBalanceDTO updateBalanceDTO = new UpdateBalanceDTO(transaction.getAmount(), transaction.getTypeOfOperation());
        if(aliasOrCvu!=null) updateBalanceDTO.setAliasOrCvu(aliasOrCvu);
        accountAPI.updateBalance(transaction.getIdAccount(),updateBalanceDTO,"PATCH");
        transactionRepo.save(transaction);
    }

    public void fallBackUpdateBalanceAccount(Transaction transaction, String aliasOrCvu,Throwable throwable) {
        logger.error("Error updating balance for transaction with ID: {} and alias/CVU: {}. Error: {}",
                transaction.getIdAccount(), aliasOrCvu, throwable.getMessage(), throwable);
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

    public TransactionDTO fallBackFindTransactionAndAccount(Long idTransaction, Throwable throwable) {
        logger.error("Error finding transaction and account for transaction ID: {}. Error: {}",
                idTransaction, throwable.getMessage(), throwable);
        return new TransactionDTO(null, null);
    }

    @Override
    public Transaction findTransaction(Long idTransaction) {
        return transactionRepo.findById(idTransaction).orElse(null);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long idTransaction) {
        transactionRepo.deleteById(idTransaction);
    }

    @Override
    @Transactional
    public void editTransaction(Long idTransaction, Transaction transaction) {
        Transaction transactionEdit = transactionRepo.findById(idTransaction).orElse(null);
        assert transactionEdit != null;
        BeanUtils.copyProperties(transaction, transactionEdit,"idTransaction");
        validator.validate(transactionEdit);
        transactionRepo.save(transactionEdit);
    }
}
