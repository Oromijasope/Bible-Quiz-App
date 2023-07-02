package com.example.quiz;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SplashActivity extends FullScreenActivity {

    public static ArrayList<Modelclass> list;
    private String currentQuestionId;
    private List<String> currentQuestionOptions;
    DatabaseReference databaseReference;
    DocumentReference documentReference;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity_main);

        list = new ArrayList<>();


        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase database = FirebaseDatabase.getInstance();




        // Level 1
        databaseReference = database.getInstance().getReference("Questions");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Modelclass modelclass = dataSnapshot.getValue(Modelclass.class);
                    list.add(modelclass);
                }

                Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Assuming you have a reference to the options node in Firebase
        DatabaseReference optionsRef = FirebaseDatabase.getInstance().getReference("options");

// Create an ArrayList to store the options
        ArrayList<String> options = new ArrayList<>();

// Retrieve the options one by one
        optionsRef.child("oA").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String optionA = dataSnapshot.getValue(String.class);
                options.add(optionA);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });

        optionsRef.child("oB").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String optionB = dataSnapshot.getValue(String.class);
                options.add(optionB);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });

        optionsRef.child("oC").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String optionC = dataSnapshot.getValue(String.class);
                options.add(optionC);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });

        optionsRef.child("oD").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String optionD = dataSnapshot.getValue(String.class);
                options.add(optionD);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });





// Example implementation of the getCurrentQuestionOptions method
        /*private void getCurrentQuestionOptions() {
            DocumentReference questionRef = documentReference.collection("questions").document(currentQuestionId);
            questionRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        // Get the question data from Firestore
                        String question = documentSnapshot.getString("Question");
                        List<String> options = (List<String>) documentSnapshot.get("options");
                        String answer = documentSnapshot.getString("ans");

                        // Update the UI to show the question and options
                        questionTextView.setText(question);
                        option1Button.setText(options.get(0));
                        option2Button.setText(options.get(1));
                        option3Button.setText(options.get(2));
                        option4Button.setText(options.get(3));

                        // Save the correct answer for later use
                        currentQuestionOptions = options;
                    } else {
                        Log.d(TAG, "No such document");
                    }
                }
            });
        }*/

// Example implementation of the getCorrectAnswer method
       /* private String getCorrectAnswer() {
            DocumentReference questionRef = documentReference.collection("questions").document(currentQuestionId);
            questionRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        // Get the correct answer from Firestore
                        String answer = documentSnapshot.getString("answer");

                        // Save the correct answer for later use
                        currentCorrectAnswer = answer;
                    } else {
                        Log.d(TAG, "No such document");
                    }
                }
            });

            return currentCorrectAnswer;
        }*/

    }
}







