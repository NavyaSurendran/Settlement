package com.spring.settlement.exception;

public class TransactionAlreadyExists extends Exception{

	public TransactionAlreadyExists(String message) {
		super(message);
	}
}
