package models;

import java.util.Date;

public class Transaction {
	public long transactionId;
	public double amount;
	public Date transactionDate;
	public String transactionType;
	public long accountId;

	public Transaction() {
		// 
	}

}
