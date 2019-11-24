package models;

import java.util.Date;
import java.util.Map;

public class Account {
	public String firstName;
	public String lastName;
	public long accountId;
	public double balance;
	public String accountType;
	public Date lastTransactionDate;
	public Boolean isSuspended;
	public String gender;
	public String password;

	public Account() {
	}

	public Account createAccount(long accountId,
			String firstName, 
			String lastName,
			String accountType,
			String gender,
			String password) {
		this.accountId = accountId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.accountType = accountType;
		this.gender = gender;
		this.password = password;
		return this;
	}
}
