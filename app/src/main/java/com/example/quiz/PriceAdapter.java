package com.example.quiz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PriceAdapter  extends RecyclerView.Adapter<PriceAdapter.ViewHolder> {
    private List<String> prices;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout priceLayout;
        public TextView amountTextView;
        public TextView coinsSoldTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            priceLayout = itemView.findViewById(R.id.buyCoin);
            amountTextView = priceLayout.findViewById(R.id.priceText);
            coinsSoldTextView = priceLayout.findViewById(R.id.amount_textview);
        }
    }

    public PriceAdapter(List<String> prices) {
        this.prices = prices;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.price_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String price = prices.get(position);
        holder.amountTextView.setText(price);
        holder.coinsSoldTextView.setText("X " + (position + 1) * 100);
    }

    @Override
    public int getItemCount() {
        return prices.size();
    }
}