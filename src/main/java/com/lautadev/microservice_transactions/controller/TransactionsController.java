package com.lautadev.microservice_transactions.controller;

import com.lautadev.microservice_transactions.dto.TransactionDTO;
import com.lautadev.microservice_transactions.model.Transaction;
import com.lautadev.microservice_transactions.service.ITransactionsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class TransactionsController {
    @Autowired
    private ITransactionsService transactionServ;

    @PostMapping("/save")
    public ResponseEntity<?> saveTransaction(@Valid @RequestBody Transaction transaction){
        transactionServ.saveTransaction(transaction);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get")
    public ResponseEntity<List<Transaction>> getTransactions(){
        return ResponseEntity.ok(transactionServ.getTransactions());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Transaction> findTransaction(@PathVariable Long id){
        Transaction transaction = transactionServ.findTransaction(id);
        if(transaction == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/get/transactionAndAccount/{id}")
    public ResponseEntity<TransactionDTO> findTransactionAndAccount(@PathVariable Long id){
        TransactionDTO transactionDTO = transactionServ.findTransactionAndAccount(id);
        if (transactionDTO == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(transactionDTO);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id){
        transactionServ.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Transaction> editTransaction(@PathVariable Long id, @Valid @RequestBody Transaction transaction){
        transactionServ.editTransaction(id,transaction);
        return ResponseEntity.ok(transactionServ.findTransaction(id));
    }
}
