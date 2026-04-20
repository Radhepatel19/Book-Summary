package com.example.booksummary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import com.example.booksummary.modules.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    ImageView ArrowBack;
    CircleImageView ProfileUserpic;
    EditText username, AboutMe, phoneNumber;
    TextView ChangePicture, ForgetPassword;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    String UserId;
    AppCompatButton Update;
    FirebaseDatabase database;
    FirebaseUser firebaseUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ArrowBack = findViewById(R.id.ArrowBack);
        ProfileUserpic = findViewById(R.id.ProfileUserpic);
        username = findViewById(R.id.username);
        AboutMe = findViewById(R.id.AboutMe);
        phoneNumber = findViewById(R.id.phoneNumber);
        ChangePicture = findViewById(R.id.ChangePicture);
        Update = findViewById(R.id.Update);
        ForgetPassword = findViewById(R.id.ForgetPassword);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        UserId = firebaseUser.getUid();
        database = FirebaseDatabase.getInstance();

        fetchImageUrl(UserId);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(UserId);

        ForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfile.this, ForgetPassword.class));
            }
        });
        ChangePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();

            }
        });
        //String Email = email.getText().toString();

        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
        ArrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfile.this, HomePage.class);
                intent.putExtra("fragment", "EditProfile");
                startActivity(intent);
            }
        });
    }
    private static final int PICK_IMAGE_REQUEST = 1;
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadImageToFirebase(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("UserPic/" + UserId + "/" + System.currentTimeMillis() + ".jpg");

            storageReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            storeImageUrlInDatabase(imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle unsuccessful uploads
                    });
        }
    }

    private void storeImageUrlInDatabase(String imageUrl) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users").child(UserId).child("profilePic");
        myRef.setValue(imageUrl)
                .addOnSuccessListener(aVoid -> {
                    loadImage(imageUrl);
                    // Image URL successfully stored in the database
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful database operations
                });
    }

    private void updateProfile() {
        String Username = username.getText().toString().trim();
        String About = AboutMe.getText().toString();
        String PhoneNumber = phoneNumber.getText().toString().trim();

        if (TextUtils.isEmpty(Username)) {
            username.setError("Username is required");
            return;
        }

        if (TextUtils.isEmpty(PhoneNumber)) {
            phoneNumber.setError("Phone number is required");
            return;
        }
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.child("AboutUs").setValue(About);
                databaseReference.child("username").setValue(Username);
                databaseReference.child("phoneNumber").setValue(PhoneNumber);
                Intent intent = new Intent(EditProfile.this, HomePage.class);
                intent.putExtra("fragment", "EditProfile");
                startActivity(intent);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void loadImage(String imageUrl) {
        // Load image into userpic ImageView using Picasso
        Picasso.get()
                .load(imageUrl)// Placeholder image// Error placeholder image/ Resize the image to avoid OutOfMemoryError (optional)// Crop the image to fit ImageView
                .into(ProfileUserpic);
    }
    private void fetchImageUrl(String userId) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        databaseRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Users user = dataSnapshot.getValue(Users.class);
                    if (user != null && user.getProfilePic() != null) {
                        loadImage(user.getProfilePic());
                    } else {
                        ProfileUserpic.setImageResource(R.drawable.user);
                    }
                    username.setText(user.getUsername().toString());
                    AboutMe.setText(user.getAboutUs().toString());
                    phoneNumber.setText(user.getPhoneNumber().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}