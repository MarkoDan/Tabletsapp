package com.example.tabletsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class for displaying loan details in a RecyclerView.
 */
public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.LoanViewHolder> {

    private List<Loan> loanList; // List of loans to be displayed
    private OnLoanReturnListener listener; // Listener for handling loan return actions

    /**
     * Constructor to initialize the loan list and listener.
     *
     * @param loanList List of loans to be displayed.
     * @param listener Listener to handle loan return actions.
     */
    public LoanAdapter(List<Loan> loanList, OnLoanReturnListener listener) {
        this.loanList = loanList;
        this.listener = listener;
    }

    /**
     * Inflates the layout for each loan item.
     *
     * @param parent   The parent view group.
     * @param viewType The view type of the new View.
     * @return A LoanViewHolder object.
     */
    @Override
    public LoanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for individual loan items
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.loan_item, parent, false);
        return new LoanViewHolder(itemView);
    }

    /**
     * Binds loan data to the ViewHolder.
     *
     * @param holder   The ViewHolder.
     * @param position The position of the current item in the list.
     */
    @Override
    public void onBindViewHolder(LoanViewHolder holder, int position) {
        // Get the current loan item
        Loan loan = loanList.get(position);

        // Set the loan details in the TextViews
        holder.tvTabletBrand.setText("Tablet Brand: " + loan.getTableBrand());
        holder.tvCableType.setText("Cable Type: " + loan.getCableType());
        holder.tvBorrowerName.setText("Borrower Name: " + loan.getBorrowerName());
        holder.tvLoanDate.setText("Loan Date: " + loan.getLoanData());

        // Set a click listener for the "Mark as Returned" button
        holder.btnReturnLoan.setOnClickListener(v -> {
            if (listener != null) {
                // Notify the listener (activity) when the button is clicked
                listener.onLoanReturned(loan, position);
            }
        });
    }

    /**
     * Returns the total number of items in the loan list.
     *
     * @return The size of the loan list.
     */
    @Override
    public int getItemCount() {
        return loanList.size();
    }

    /**
     * Updates the data in the adapter and refreshes the RecyclerView.
     *
     * @param loanList The updated list of loans.
     */
    public void updateData(List<Loan> loanList) {
        this.loanList = loanList;
        notifyDataSetChanged(); // Notify the RecyclerView to refresh the UI
    }

    /**
     * ViewHolder class for managing individual loan items.
     */
    public static class LoanViewHolder extends RecyclerView.ViewHolder {
        TextView tvTabletBrand, tvCableType, tvBorrowerName, tvLoanDate; // TextViews for loan details
        Button btnReturnLoan; // Button for marking a loan as returned

        /**
         * Constructor to initialize the views.
         *
         * @param itemView The individual item view.
         */
        public LoanViewHolder(View itemView) {
            super(itemView);

            // Bind the views to their respective UI elements in the layout
            tvTabletBrand = itemView.findViewById(R.id.tvTabletBrand);
            tvCableType = itemView.findViewById(R.id.tvCableType);
            tvBorrowerName = itemView.findViewById(R.id.tvBorrowerName);
            tvLoanDate = itemView.findViewById(R.id.tvLoanDate);
            btnReturnLoan = itemView.findViewById(R.id.btnReturnLoan);
        }
    }

    /**
     * Interface for handling loan return actions in the activity.
     */
    public interface OnLoanReturnListener {
        /**
         * Callback for when a loan is marked as returned.
         *
         * @param loan     The loan object.
         * @param position The position of the loan in the list.
         */
        void onLoanReturned(Loan loan, int position);
    }
}
