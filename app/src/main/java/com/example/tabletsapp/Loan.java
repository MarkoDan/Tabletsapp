package com.example.tabletsapp;

public class Loan {

    private int id;
    private String tableBrand;
    private String cableType;
    private String borrowerName;
    private String loanData;

    public Loan(int id, String tableBrand, String cableType, String borrowerName, String loanData) {
        this.id = id;
        this.tableBrand = tableBrand;
        this.cableType = cableType;
        this.borrowerName = borrowerName;
        this.loanData = loanData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTableBrand() {
        return tableBrand;
    }

    public void setTableBrand(String tableBrand) {
        this.tableBrand = tableBrand;
    }

    public String getCableType() {
        return cableType;
    }

    public void setCableType(String cableType) {
        this.cableType = cableType;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public String getLoanData() {
        return loanData;
    }

    public void setLoanData(String loanData) {
        this.loanData = loanData;
    }
}
