package models;

import java.util.Date;

public class Loan {
	private String loanId;
	public long accountId;
	public Date requestDate;
	public Date adminActionDate;
	public int adminStatus;
	public double amount;
	public Loan() {
	}
	public String getLoanId() {
		return loanId;
	}
	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}
	public long getAccountId() {
		return accountId;
	}
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}
	public Date getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}
	public Date getAdminActionDate() {
		return adminActionDate;
	}
	public void setAdminActionDate(Date adminActionDate) {
		this.adminActionDate = adminActionDate;
	}
	public int getAdminStatus() {
		return adminStatus;
	}
	public void setAdminStatus(int adminStatus) {
		this.adminStatus = adminStatus;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public String getStatus() {
		if (adminStatus == 1) {
			return "Approved";
		} else if (adminStatus == 2) {
			return "Rejected";
		} else {
			return "Pending";
		}
	}
}
