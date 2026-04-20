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
import com.example.booksummary.modules.Users;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
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


public class SignUp extends AppCompatActivity {
    Button signup, google;
    EditText EmailId, Username, Password;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    ProgressBar progressBar;
    GoogleSignInClient signInClient;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        signup = findViewById(R.id.SignUp);
        google = findViewById(R.id.Google);
        EmailId = findViewById(R.id.Email);
        Username = findViewById(R.id.username);
        Password = findViewById(R.id.password);
        progressBar = findViewById(R.id.ProgressBar);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        signup.setOnClickListener(view -> {
            if (EmailId.getText().toString().equals("") || Username.getText().toString().equals("") || Password.getText().toString().equals("")) {
                //CustomToast(SignUp.this,"Invalid Credentials");
                Toast.makeText(this, "Enter Credentials", Toast.LENGTH_SHORT).show();
            } else {
                String email = EmailId.getText().toString();
                String emailPattern =
                        "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
                if (email.matches(emailPattern) && email.length() > 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    signup.setVisibility(View.INVISIBLE);
                    mAuth.createUserWithEmailAndPassword(EmailId.getText().toString(), Password.getText().toString())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(this, "Verified Email", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.INVISIBLE);
                                                signup.setVisibility(View.VISIBLE);
                                                Users users = new Users(Username.getText().toString(), EmailId.getText().toString(), Password.getText().toString());
                                                String id = task.getResult().getUser().getUid();
                                                database.getReference().child("Users").child(id).setValue(users);
                                                Intent intent = new Intent(SignUp.this, MainActivity.class);
                                                startActivity(intent);
                                        }
                                    });
                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    signup.setVisibility(View.VISIBLE);
                                    //CustomToast(SignUp.this, task.getException().toString());
                                    Toast.makeText(this, "Enter Valid Credentials", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    //CustomToast(SignUp.this, "Invalid Email");
                    Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
                }
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this, gso);

        google.setOnClickListener(view -> SignIn());

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
                                    Intent intent = new Intent(SignUp.this,HomePage.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    google.setMail(Objects.requireNonNull(user).getEmail());
                                    google.setUsername(user.getDisplayName());
                                    google.setProfilePic(Objects.requireNonNull(user.getPhotoUrl()).toString());
                                    databaseReference.child(user.getUid()).setValue(google);

                                    // Create a new instance of HomeFragment
                                    Intent intent = new Intent(SignUp.this,HomePage.class);
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