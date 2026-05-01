package com.fintrack.application.port.out.transaction;

import com.fintrack.domain.model.transaction.Transaction;

import java.util.List;

public interface SaveTransactionPort {

    Transaction save(Transaction transaction);

    List<Transaction> saveAll(List<Transaction> transactions);
}