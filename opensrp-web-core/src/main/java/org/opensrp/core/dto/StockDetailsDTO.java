/**
 * @author proshanto (proshanto123@gmail.com)
 */
package org.opensrp.core.dto;

import java.util.Date;

public class StockDetailsDTO {
	
	private Long id;
	
	private Long productId;
	
	private int credit;
	
	private int debit;
	
	private int branchId;
	
	private Date date;
	
	private String referenceType;
	
	private String status;
	
	private String invoiceNumber;
	
	private int sellOrPassTo;
	
	private Date expireyDate;
	
	private Date startDate;
	
	private Date receiveDate;
	
	private int month;
	
	private int year;
	
	private String challan;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getProductId() {
		return productId;
	}
	
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
	public int getCredit() {
		return credit;
	}
	
	public void setCredit(int credit) {
		this.credit = credit;
	}
	
	public int getDebit() {
		return debit;
	}
	
	public void setDebit(int debit) {
		this.debit = debit;
	}
	
	public int getBranchId() {
		return branchId;
	}
	
	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getReferenceType() {
		return referenceType;
	}
	
	public void setReferenceType(String referenceType) {
		this.referenceType = referenceType;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public int getSellOrPassTo() {
		return sellOrPassTo;
	}
	
	public void setSellOrPassTo(int sellOrPassTo) {
		this.sellOrPassTo = sellOrPassTo;
	}
	
	public Date getExpireyDate() {
		return expireyDate;
	}
	
	public void setExpireyDate(Date expireyDate) {
		this.expireyDate = expireyDate;
	}
	
	public Date getReceiveDate() {
		return receiveDate;
	}
	
	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}
	
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	
	public int getMonth() {
		return month;
	}
	
	public void setMonth(int month) {
		this.month = month;
	}
	
	public int getYear() {
		return year;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public String getChallan() {
		return challan;
	}
	
	public void setChallan(String challan) {
		this.challan = challan;
	}
	
}
