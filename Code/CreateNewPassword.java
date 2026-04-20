package com.example.booksummary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateNewPassword extends AppCompatActivity {
EditText newPassword, confirmPassword;
Button Save;
ProgressBar progressBar;
String Password, EmailId;
 @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     requestWindowFeature(Window.FEATURE_NO_TITLE);
     this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_create_new_password);

        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.ConfirmPassword);
        Save = findViewById(R.id.Save);
        progressBar = findViewById(R.id.ProgressBar);

        Intent intent = getIntent();
        EmailId = intent.getStringExtra("EmailId");
        Password = newPassword.getText().toString();

        Save.setOnClickListener(view -> {
            if (EmailId != null && !EmailId.isEmpty()) {
                if (newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
                    // Get the email from the user input
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

                    usersRef.orderByChild("mail").equalTo(EmailId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                progressBar.setVisibility(View.VISIBLE);
                                Save.setVisibility(View.INVISIBLE);
                                // Email exists in the database, proceed with the password update
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                // Update the user's password in Firebase Authentication
                                user.updatePassword(newPassword.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Save.setVisibility(View.VISIBLE);
                                                    // Password updated successfully

                                                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                                        String userId = userSnapshot.getKey();
                                                        DatabaseReference userRef = usersRef.child(userId);
                                                        userRef.child("password").setValue(newPassword.getText().toString())
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            // Password updated successfully in Realtime Database
                                                                            Intent intent1 = new Intent(CreateNewPassword.this, MainActivity.class);
                                                                            startActivity(intent1);
                                                                            finish();
                                                                            // Inform the user about the successful password update
                                                                        }else {
                                                                            progressBar.setVisibility(View.INVISIBLE);
                                                                            Save.setVisibility(View.VISIBLE);
                                                                            //CustomToast(CreateNewPassword.this,"Error");
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                    // Inform the user about the successful password update
                                                } else {
                                                    // Failed to update password
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Save.setVisibility(View.VISIBLE);
                                                    // Inform the user about the password update failure
                                                }
                                            }
                                        });
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Save.setVisibility(View.VISIBLE);
                                // Email does not exist in the database
                                //CustomToast(CreateNewPassword.this, "Email does not exist in the database. Cannot update password.");
                                // Inform the user that the email is not registered
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle errors
                            //CustomToast(CreateNewPassword.this, "Cancelled");
                        }
                    });

                } else {
                    //CustomToast(CreateNewPassword.this, "Must be Same");
                }
            }else {
                //CustomToast(CreateNewPassword.this,"Email is null");
            }
        });
    }
}