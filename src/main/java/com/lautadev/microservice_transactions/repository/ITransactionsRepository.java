package com.lautadev.microservice_transactions.repository;

import com.lautadev.microservice_transactions.model.Transaction;
import com.lautadev.microservice_transactions.model.TypeOfOperation;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ITransactionsRepository extends JpaRepository<Transaction, Long> {
    //Filters by Type of Operation
    @Query("SELECT t FROM Transaction t WHERE t.typeOfOperation = :typeOfOperation AND t.idAccount = :idAccount")
    List<Transaction> getTransactionsByOperationAndAccount(TypeOfOperation typeOfOperation, Long idAccount);

    // Filters by Date and Account
    @Query("SELECT t FROM Transaction t WHERE t.dateOfOperation >= :startDate AND t.dateOfOperation < :endDate AND t.idAccount = :idAccount")
    List<Transaction> findTransactionsWithinDateRange(@Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate,
                                                      @Param("idAccount") Long idAccount);

    // Yesterday's Transactions by Account
    @Query(value = "SELECT * FROM Transaction t WHERE t.date_of_operation >= CURDATE() - INTERVAL 1 DAY AND t.date_of_operation < CURDATE() AND t.id_account = :idAccount", nativeQuery = true)
    List<Transaction> findTransactionsForYesterday(@Param("idAccount") Long idAccount);

    // Filters by Date, Type of Operation and Account
    @Query("SELECT t FROM Transaction t WHERE t.dateOfOperation >= :startDate AND t.dateOfOperation < :endDate AND t.typeOfOperation = :typeOfOperation AND t.idAccount = :idAccount")
    List<Transaction> findTransactionsByDateAndOperationAndAccount(@Param("startDate") LocalDateTime startDate,
                                                                   @Param("endDate") LocalDateTime endDate,
                                                                   @Param("typeOfOperation") TypeOfOperation typeOfOperation,
                                                                   @Param("idAccount") Long idAccount);
}
