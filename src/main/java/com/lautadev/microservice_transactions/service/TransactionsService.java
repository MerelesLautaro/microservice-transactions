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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<Transaction> getTransactionsByAccount(Long idAccount) {
        List<Transaction> transactions = this.getTransactions();
        return transactions.stream()
                .filter(transaction -> transaction.getIdAccount().equals(idAccount))
                .collect(Collectors.toList());
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

    // START Methods for Filters

    @Override
    public List<Transaction> getTransactionsByOperationAndAccount(TypeOfOperation typeOfOperation, Long idAccount) {
        return transactionRepo.getTransactionsByOperationAndAccount(typeOfOperation, idAccount);
    }

    @Override
    public List<Transaction> getTransactionsForToday(Long idAccount) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return transactionRepo.findTransactionsWithinDateRange(startOfDay, endOfDay, idAccount);
    }

    @Override
    public List<Transaction> getTransactionsForYesterday(Long idAccount) {
        return transactionRepo.findTransactionsForYesterday(idAccount);
    }

    @Override
    public List<Transaction> getTransactionsForLast7Days(Long idAccount) {
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime startOfDay = endOfDay.minusDays(7);
        return transactionRepo.findTransactionsWithinDateRange(startOfDay, endOfDay, idAccount);
    }

    @Override
    public List<Transaction> getTransactionsForLast15Days(Long idAccount) {
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime startOfDay = endOfDay.minusDays(15);
        return transactionRepo.findTransactionsWithinDateRange(startOfDay, endOfDay, idAccount);
    }

    @Override
    public List<Transaction> getTransactionsForLastMonth(Long idAccount) {
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime startOfDay = endOfDay.minusMonths(1);
        return transactionRepo.findTransactionsWithinDateRange(startOfDay, endOfDay, idAccount);
    }

    @Override
    public List<Transaction> getTransactionsForLast3Months(Long idAccount) {
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime startOfDay = endOfDay.minusMonths(3);
        return transactionRepo.findTransactionsWithinDateRange(startOfDay, endOfDay, idAccount);
    }

    // Filters by Date, Type of Operation and Account
    @Override
    public List<Transaction> getTransactionsByDateAndOperationAndAccount(String dateFilter, TypeOfOperation typeOfOperation, Long idAccount) {
        LocalDateTime startDate;
        LocalDateTime endDate = LocalDate.now().plusDays(1).atStartOfDay(); // End of today

        switch (dateFilter) {
            case "Hoy":
                startDate = LocalDate.now().atStartOfDay(); // Start of today
                break;
            case "Ayer":
                startDate = LocalDate.now().minusDays(1).atStartOfDay(); // Start of yesterday
                endDate = LocalDate.now().atStartOfDay(); // End of yesterday
                break;
            case "Últimos 7 días":
                startDate = LocalDate.now().minusDays(7).atStartOfDay(); // Start of 7 days ago
                break;
            case "Últimos 15 días":
                startDate = LocalDate.now().minusDays(15).atStartOfDay(); // Start of 15 days ago
                break;
            case "Último mes":
                startDate = LocalDate.now().minusMonths(1).atStartOfDay(); // Start of last month
                break;
            case "Últimos 3 meses":
                startDate = LocalDate.now().minusMonths(3).atStartOfDay(); // Start of 3 months ago
                break;
            default:
                throw new IllegalArgumentException("Filtro de fecha no reconocido: " + dateFilter);
        }

        return transactionRepo.findTransactionsByDateAndOperationAndAccount(startDate, endDate, typeOfOperation, idAccount);
    }

    // END Methods for Filters

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
