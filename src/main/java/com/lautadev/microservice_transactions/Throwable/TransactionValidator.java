package com.lautadev.microservice_transactions.Throwable;

import com.lautadev.microservice_transactions.model.Transaction;
import com.lautadev.microservice_transactions.model.TypeOfOperation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TransactionValidator {

    private final Validator validator;

    public TransactionValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public void validateTypeOfOperation(TypeOfOperation typeOfOperation) {
        // Implementa la lógica de validación para el tipo de operación
        if (typeOfOperation == null) {
            throw new IllegalArgumentException("El tipo de operación no puede ser nulo");
        }
        // Otros casos de validación según tus requerimientos
    }


    public void validate(Transaction transaction) {
        if (transaction == null) {
            throw new TransactionException("Transaction cannot be null");
        }

        // Validaciones estándar usando Hibernate Validator
        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Transaction> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new TransactionException(sb.toString());
        }

    }
}
