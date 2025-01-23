package com.example.tabletsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.LoanViewHolder >{

    private final List<Loan> loanList;

    public LoanAdapter(List<Loan> loanList) {
        this.loanList = loanList;
    }


    @NonNull
    @Override
    public LoanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loan_item, parent, false);
        return new LoanViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull LoanViewHolder holder, int position) {
        Loan loan = loanList.get(position);
        holder.tvTabletBrand.setText("Brand: " + loan.getTableBrand());
        holder.tvBorrowerName.setText("Borrower: " + loan.getBorrowerName());
        holder.tvLoanDate.setText("Date: " + loan.getLoanData());
    }

    @Override
    public int getItemCount() {
        return loanList.size();
    }

    static class LoanViewHolder extends RecyclerView.ViewHolder {
        TextView tvTabletBrand, tvBorrowerName, tvLoanDate;

        public LoanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTabletBrand = itemView.findViewById(R.id.tvTabletBrand);
            tvBorrowerName = itemView.findViewById(R.id.tvBorrowerName);
            tvLoanDate = itemView.findViewById(R.id.tvLoanDate);
        }
    }
}
