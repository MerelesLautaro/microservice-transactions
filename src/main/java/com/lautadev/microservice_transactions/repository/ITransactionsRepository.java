package com.lautadev.microservice_transactions.repository;

import com.lautadev.microservice_transactions.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITransactionsRepository extends JpaRepository<Transaction, Long> {
}
