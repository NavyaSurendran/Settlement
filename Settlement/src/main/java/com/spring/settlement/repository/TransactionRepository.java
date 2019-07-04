package com.spring.settlement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.settlement.entity.Transaction;


/**
 * @author Navya Surendran
 *
 */
public interface TransactionRepository extends JpaRepository<Transaction, String>{
	

}
