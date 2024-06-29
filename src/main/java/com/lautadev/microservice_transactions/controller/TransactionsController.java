package com.lautadev.microservice_transactions.controller;

import com.lautadev.microservice_transactions.dto.TransactionDTO;
import com.lautadev.microservice_transactions.model.Transaction;
import com.lautadev.microservice_transactions.model.TypeOfOperation;
import com.lautadev.microservice_transactions.service.ITransactionsService;
import com.lautadev.microservice_transactions.service.TransactionsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class TransactionsController {
    @Autowired
    private ITransactionsService transactionServ;

    @PostMapping("/save")
    public ResponseEntity<?> saveTransaction(@Valid @RequestBody Transaction transaction,
                                             @RequestParam(required = false) String aliasOrCvu){
        transactionServ.saveTransaction(transaction, aliasOrCvu);
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

    @GetMapping("/get/account/{id}")
    public ResponseEntity<List<Transaction>> getTransactionsByAccount(@PathVariable Long id){
        return ResponseEntity.ok(transactionServ.getTransactionsByAccount(id));
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

    //Endpoints for filters
    @GetMapping("/get/operation")
    public ResponseEntity<List<Transaction>> getTransactionsByOperationAndAccount(@RequestParam TypeOfOperation typeOfOperation,
                                                                                  @RequestParam Long idAccount){
        return ResponseEntity.ok(transactionServ.getTransactionsByOperationAndAccount(typeOfOperation, idAccount));
    }

    @GetMapping("/filter/today")
    public ResponseEntity<List<Transaction>> getTransactionsForToday(@RequestParam Long idAccount) {
        return ResponseEntity.ok(transactionServ.getTransactionsForToday(idAccount));
    }

    @GetMapping("/filter/yesterday")
    public ResponseEntity<List<Transaction>> getTransactionsForYesterday(@RequestParam Long idAccount) {
        return ResponseEntity.ok(transactionServ.getTransactionsForYesterday(idAccount));
    }

    @GetMapping("/filter/last7days")
    public ResponseEntity<List<Transaction>> getTransactionsForLast7Days(@RequestParam Long idAccount) {
        return ResponseEntity.ok(transactionServ.getTransactionsForLast7Days(idAccount));
    }

    @GetMapping("/filter/last15days")
    public ResponseEntity<List<Transaction>> getTransactionsForLast15Days(@RequestParam Long idAccount) {
        return ResponseEntity.ok(transactionServ.getTransactionsForLast15Days(idAccount));
    }

    @GetMapping("/filter/lastmonth")
    public ResponseEntity<List<Transaction>> getTransactionsForLastMonth(@RequestParam Long idAccount) {
        return ResponseEntity.ok(transactionServ.getTransactionsForLastMonth(idAccount));
    }

    @GetMapping("/filter/last3months")
    public ResponseEntity<List<Transaction>> getTransactionsForLast3Months(@RequestParam Long idAccount) {
        return ResponseEntity.ok(transactionServ.getTransactionsForLast3Months(idAccount));
    }

    @GetMapping("/filter/custom")
    public ResponseEntity<List<Transaction>> getTransactionsByDateAndOperationAndAccount(
            @RequestParam String dateFilter,
            @RequestParam TypeOfOperation typeOfOperation,
            @RequestParam Long idAccount) {
        return ResponseEntity.ok(transactionServ.getTransactionsByDateAndOperationAndAccount(dateFilter, typeOfOperation, idAccount));
    }
}
