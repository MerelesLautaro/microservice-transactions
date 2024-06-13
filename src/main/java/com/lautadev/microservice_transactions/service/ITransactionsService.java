package com.lautadev.microservice_transactions.service;

import com.lautadev.microservice_transactions.dto.TransactionDTO;
import com.lautadev.microservice_transactions.model.Transaction;

import java.util.List;

public interface ITransactionsService {
    public void saveTransaction(Transaction transaction, String aliasOrCvu);
    public List<Transaction> getTransactions();
    public TransactionDTO findTransactionAndAccount(Long idTransaction);
    public Transaction findTransaction(Long idTransaction);
    public void deleteTransaction(Long idTransaction);
    public void editTransaction(Long idTransaction, Transaction transaction);
}
