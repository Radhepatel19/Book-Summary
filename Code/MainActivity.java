package com.example.booksummary;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.booksummary.modules.Google;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
@SuppressLint("UseSwitchCompatOrMaterialCode")
Boolean nightMode;
Button Login;
TextView forgetPassword, Google, SignUp;
EditText UserEmail, PasswordP;
ProgressBar progressBar;
FirebaseAuth mAuth;
FirebaseDatabase database;
GoogleSignInClient signInClient;
SharedPreferences sharedPreferences;
SharedPreferences.Editor editor;
    String userId;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            Login = findViewById(R.id.logIn);
            forgetPassword = findViewById(R.id.forgetPassword);
            Google = findViewById(R.id.google);
            SignUp = findViewById(R.id.signUp);
            UserEmail = findViewById(R.id.username);
            progressBar = findViewById(R.id.ProgressBar);
            PasswordP = findViewById(R.id.password);


            mAuth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance();




            SignUp.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);
            });

            forgetPassword.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, ForgetPassword.class);
                startActivity(intent);
            });
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            signInClient = GoogleSignIn.getClient(this, gso);

            Google.setOnClickListener(view -> SignIn());

            Login.setOnClickListener(view -> {

                if (!UserEmail.getText().toString().isEmpty() && !PasswordP.getText().toString().isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    Login.setVisibility(View.INVISIBLE);


                    mAuth.signInWithEmailAndPassword(UserEmail.getText().toString(), PasswordP.getText().toString())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    if (mAuth.getCurrentUser().isEmailVerified()) {
                                        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                                        SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                        editor1.putBoolean("flag", true);
                                        editor1.apply();
                                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

                                        usersRef.orderByChild("mail").equalTo(UserEmail.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    progressBar.setVisibility(View.VISIBLE);
                                                    Login.setVisibility(View.INVISIBLE);
                                                    // Email exists in the database, proceed with the password update
                                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                                    // Update the user's password in Firebase Authentication
                                                    user.updatePassword(PasswordP.getText().toString())
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                        Login.setVisibility(View.VISIBLE);
                                                                        // Password updated successfully

                                                                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                                                            userId = userSnapshot.getKey();
                                                                            DatabaseReference userRef = usersRef.child(userId);
                                                                            userRef.child("password").setValue(PasswordP.getText().toString())
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                String currentUserID = mAuth.getCurrentUser().getUid();

                                                                                                if (currentUserID.equals(userId)) {
                                                                                                    // Password updated successfully in Realtime Database
                                                                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                                                                    Intent intent1 = new Intent(MainActivity.this, HomePage.class);
//                                                                                                intent1.putExtra("userId",userId);
                                                                                                    startActivity(intent1);
                                                                                                    finish();
                                                                                                }
                                                                                                // Inform the user about the successful password update
                                                                                            } else {
                                                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                                                Login.setVisibility(View.VISIBLE);
                                                                                                //CustomToast(MainActivity.this, "Error");
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                        // Inform the user about the successful password update
                                                                    } else {
                                                                        // Failed to update password
                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                        Login.setVisibility(View.VISIBLE);
                                                                        // Inform the user about the password update failure
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Login.setVisibility(View.VISIBLE);
                                                    // Email does not exist in the database
                                                    Toast.makeText(MainActivity.this, "Email does not exist in the database. Cannot update password.", Toast.LENGTH_SHORT).show();
                                                    // Inform the user that the email is not registered
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                // Handle errors
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Login.setVisibility(View.VISIBLE);
                                                //CustomToast(MainActivity.this, "Cancelled");
                                                Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    } else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Login.setVisibility(View.VISIBLE);
                                        //CustomToast(MainActivity.this, "Please Verified Email");
                                        Toast.makeText(this, "Please Verified Email", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Login.setVisibility(View.VISIBLE);
                                    //CustomToast(MainActivity.this, "Enter Valid Credentials");
                                    Toast.makeText(this, "Enter Valid Credentials", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                   // CustomToast(MainActivity.this, "Enter Credentials");
                    Toast.makeText(this, "Enter Credentials", Toast.LENGTH_SHORT).show();

                }
            });
    }
    private static final int REQ_ONE_TAP = 9001;
    private void SignIn(){
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent,REQ_ONE_TAP);
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_ONE_TAP) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Handle exception
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithCredential:success");
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Google google = new Google();


                        Query query = databaseReference.child(user.getUid()).orderByChild("mail").equalTo(google.getMail());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    Intent intent = new Intent(MainActivity.this,HomePage.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    google.setMail(Objects.requireNonNull(user).getEmail());
                                    google.setUsername(user.getDisplayName());
                                    google.setProfilePic(Objects.requireNonNull(user.getPhotoUrl()).toString());
                                    databaseReference.child(user.getUid()).setValue(google);

                                    // Create a new instance of HomeFragment
                                    Intent intent = new Intent(MainActivity.this,HomePage.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                    }
                });
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (mAuth.getCurrentUser() != null){
//            Intent intent = new Intent(MainActivity.this,HomePage.class);
//            startActivity(intent);
//        }else{
//            Toast.makeText(this, "User not login", Toast.LENGTH_SHORT).show();
//        }
//    }
}