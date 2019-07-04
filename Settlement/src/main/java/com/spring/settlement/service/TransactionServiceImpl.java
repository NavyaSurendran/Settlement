package com.spring.settlement.service;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.spring.settlement.entity.Transaction;
import com.spring.settlement.exception.TransactionAlreadyExists;
import com.spring.settlement.repository.TransactionRepository;

/**
 * @author Navya Surendran
 *
 */
@Service
public class TransactionServiceImpl implements TransactionService {

	TransactionRepository transactionRepository;

	@Autowired
	public TransactionServiceImpl(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	@Override
	public <T> List<T> loadObjectList(Class<T> type, String fileName) {
		try {

			CsvSchema bootstrap = CsvSchema.builder().setUseHeader(true).setColumnSeparator(',')
								 
					.build().withHeader();

			CsvMapper mapper = new CsvMapper();
			File file = new ClassPathResource(fileName).getFile();
			MappingIterator<T> readValues = mapper.readerFor(type).with(bootstrap).readValues(file);
			return readValues.readAll();
		} catch (Exception e) {
			System.out.println("Error occurred while loading object list from file " + e);
			return Collections.emptyList();
		}
	}

	@Override
	public boolean saveAll(List<Transaction> transactionList) throws TransactionAlreadyExists {		
		final List<Transaction> transactions =
		transactionRepository.findAll(); 
		if(!transactions.isEmpty()) { 
			throw new TransactionAlreadyExists("Could not save, Transaction already exsits"); 
		}		 
		transactionRepository.saveAll(transactionList);
		return true;
	}



	@Override
	 public Map<String, Object> getRelativeAccountBalace(String accountId, Date fromDate, Date toDate) {
		
		List<Transaction> transList = transactionRepository.findAll();
		
		List<Transaction> transactions = removeReversedTransaction(transList);
		Double amount = 0.0;
		int count =0;
		Map<String, Object> result = new HashMap<>();
		for (Transaction transaction : transactions) {
			if(transaction.getCreateAt().after(fromDate) || transaction.getCreateAt().before(toDate)) {
				if (transaction.getToAccountId().equalsIgnoreCase(accountId)) {
					
					amount+= transaction.getAmount();
					count++;
				}else if(transaction.getFromAccountId().equalsIgnoreCase(accountId)) {
					
					amount-= transaction.getAmount();
					count++;
				}
			}
			
		}
		
		result.put("Relative Amount", amount);
		result.put("Number of Transactions Included", count);
		return result;
		

	}

	@Override
	public List<Transaction> removeReversedTransaction(List<Transaction> TransactionList) {
		List<Transaction> transactions = new ArrayList<>();
		
		for(Transaction transaction:TransactionList )  {
			
			if (transaction.getTransactionType().equalsIgnoreCase("REVERSAL")) {
				String relatedTransactionId = transaction.getRelatedTransaction();
				
				for(Transaction trans: transactions ) {
					if (trans.getTransactionId().equalsIgnoreCase(relatedTransactionId)) {
						transactions.remove(trans);
					}
				}

			} else {
				transactions.add(transaction);
			}
		}
		return transactions;
	}
}
