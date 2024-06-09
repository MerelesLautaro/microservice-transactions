package com.lautadev.microservice_transactions.Throwable;

public class TransactionException extends RuntimeException{
    public TransactionException (String message) {
        super(message);
    }
}
