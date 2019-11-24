package models;

import java.util.Date;

public class Loan {
	public long loanId;
	public long accountId;
	public Date requestDate;
	public Date adminActionDate;
	public int adminStatus;
	public double amount;
	public Loan() {
	}
}
