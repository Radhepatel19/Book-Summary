package com.example.booksummary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RatingApp extends AppCompatActivity {

    private RatingBar ratingBar;
    private Button submitRating;
    FirebaseAuth mAuth;
    String UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_app);
        ratingBar = findViewById(R.id.ratingBar);
        submitRating = findViewById(R.id.submitRating);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        UserId = firebaseUser.getUid();

        submitRating.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ratingRef = database.getReference("ratings").child(UserId);
            ratingRef.setValue(rating);
            Intent intent = new Intent(RatingApp.this, HomePage.class);
            intent.putExtra("fragment", "EditProfile");
            startActivity(intent);

            // Handle the rating submission logic here
            // You can send the rating to a server or save it in a database
        });
    }
}