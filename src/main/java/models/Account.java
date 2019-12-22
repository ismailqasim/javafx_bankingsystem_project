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
	public Boolean isSuspended = false;
	public String gender;
	public String password;
	public Boolean approved = false;
	public Boolean admin = false;

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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	@Override
	public String toString() {
		return "Account [firstName=" + firstName + ", accountId=" + accountId + "]";
	}
	
	
	
}
