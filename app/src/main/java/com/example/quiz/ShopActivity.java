package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        // Get a reference to the parent layout
        RelativeLayout relativeLayout = findViewById(R.id.buyCoin);

        // Create a new RecyclerView and set its LayoutManager
        RecyclerView priceRecyclerView = new RecyclerView(this);
        priceRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inflate the price_layout.xml file and get a reference to the root view
        View priceLayout = getLayoutInflater().inflate(R.layout.price_layout, relativeLayout, false);

        // Add the RecyclerView to the inflated layout
        ((RelativeLayout) priceLayout).addView(priceRecyclerView);

        // Set the adapter for the RecyclerView
        List<String> prices = Arrays.asList("$100", "$200", "$300", "$400");
        PriceAdapter adapter = new PriceAdapter(prices);
        priceRecyclerView.setAdapter(adapter);

        // Add the inflated layout to the parent layout
        relativeLayout.addView(priceLayout);
    }

}
