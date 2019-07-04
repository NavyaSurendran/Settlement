package com.spring.settlement.service;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.spring.settlement.entity.Transaction;
import com.spring.settlement.exception.TransactionAlreadyExists;


public interface TransactionService {
	public  <T> List<T> loadObjectList(Class<T> type, String fileName);
	
	public boolean saveAll(List<Transaction> transactionList) throws TransactionAlreadyExists;
		
	public  Map<String, Object> getRelativeAccountBalace(String accountId, Date fromDate, Date toDate);
	
	public List<Transaction> removeReversedTransaction(List<Transaction> TransactionList);
	
	

}
