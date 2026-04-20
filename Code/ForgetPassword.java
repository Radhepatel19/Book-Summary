package com.example.booksummary;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ForgetPassword extends AppCompatActivity {
Button send;
EditText Email;
ProgressBar progressBar;
private FirebaseAuth mAuth;
    FirebaseDatabase database;
    GoogleSignInClient signInClient;
//private String mVerificationId;

    TextView google;
private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forget_password);

        send = findViewById(R.id.Send);
        Email = findViewById(R.id.Email);
        progressBar = findViewById(R.id.ProgressBar);
        google = findViewById(R.id.google);

        mAuth = FirebaseAuth.getInstance();
        // otp send and receive


//        //mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

//            @Override
//            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
//                progressBar.setVisibility(View.GONE);
//                send.setVisibility(View.VISIBLE);
//
//            }
//
//            @Override
//            public void onVerificationFailed(@NonNull FirebaseException e) {
//                progressBar.setVisibility(View.GONE);
//                send.setVisibility(View.VISIBLE);
//                CustomToast(ForgetPassword.this, "Verification Failed");
//            }
//
//            @Override
//            public void onCodeSent(@NonNull String verificationId,
//                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
//                super.onCodeSent(verificationId, token);
//                progressBar.setVisibility(View.GONE);
//                send.setVisibility(View.VISIBLE);
//                Intent intent = new Intent(ForgetPassword.this, VerifyCode.class);
//                mVerificationId = verificationId;
//                intent.putExtra("number",number.getText().toString());
//                intent.putExtra("OTP",mVerificationId);
//                startActivity(intent);
//            }
//        };
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this, gso);
        google.setOnClickListener(view -> SignIn());

        send.setOnClickListener(view -> {
                if (!Email.getText().toString().isEmpty()){
                    progressBar.setVisibility(View.VISIBLE);
                    send.setVisibility(View.INVISIBLE);

                    // Reset Password

                    String emailToCheck = Email.getText().toString();
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

                    usersRef.orderByChild("mail").equalTo(emailToCheck).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Email already exists in the database
                                mAuth.sendPasswordResetEmail(emailToCheck).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Intent intent = new Intent(ForgetPassword.this,MainActivity.class);
                                        Toast.makeText(ForgetPassword.this, "Email is Send Create new Password", Toast.LENGTH_SHORT).show();
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            } else {
                                // Email does not exist in the database
                                progressBar.setVisibility(View.INVISIBLE);
                                send.setVisibility(View.VISIBLE);
                                //CustomToast(ForgetPassword.this,"Email Doesn't Exist");
                                Toast.makeText(ForgetPassword.this, "Email Doesn't Exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressBar.setVisibility(View.INVISIBLE);
                            send.setVisibility(View.VISIBLE);
                            //CustomToast(ForgetPassword.this,error.getMessage());
                            Toast.makeText(ForgetPassword.this, "Email Doesn't Exist", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(ForgetPassword.this, "Enter Email", Toast.LENGTH_SHORT).show();
                }
        });
    }

    private void VerificationCode(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)// ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private static final int REQ_ONE_TAP = 3;
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
                                    Intent intent = new Intent(ForgetPassword.this,HomePage.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    google.setMail(Objects.requireNonNull(user).getEmail());
                                    google.setUsername(user.getDisplayName());
                                    google.setProfilePic(Objects.requireNonNull(user.getPhotoUrl()).toString());
                                    databaseReference.child(user.getUid()).setValue(google);

                                    // Create a new instance of HomeFragment
                                    Intent intent = new Intent(ForgetPassword.this,HomePage.class);
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


}