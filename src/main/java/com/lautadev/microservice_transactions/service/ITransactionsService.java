package com.lautadev.microservice_transactions.service;

import com.lautadev.microservice_transactions.dto.TransactionDTO;
import com.lautadev.microservice_transactions.model.Transaction;
import com.lautadev.microservice_transactions.model.TypeOfOperation;

import java.util.List;

public interface ITransactionsService {
    public void saveTransaction(Transaction transaction, String aliasOrCvu);
    public List<Transaction> getTransactions();
    public List<Transaction> getTransactionsByAccount(Long idAccount);
    public TransactionDTO findTransactionAndAccount(Long idTransaction);
    public Transaction findTransaction(Long idTransaction);
    public void deleteTransaction(Long idTransaction);
    public void editTransaction(Long idTransaction, Transaction transaction);
    //Methos for fitlers
    public List<Transaction> getTransactionsByOperationAndAccount(TypeOfOperation typeOfOperation, Long idAccount);
    List<Transaction> getTransactionsForToday(Long idAccount);
    List<Transaction> getTransactionsForYesterday(Long idAccount);
    List<Transaction> getTransactionsForLast7Days(Long idAccount);
    List<Transaction> getTransactionsForLast15Days(Long idAccount);
    List<Transaction> getTransactionsForLastMonth(Long idAccount);
    List<Transaction> getTransactionsForLast3Months(Long idAccount);

}
