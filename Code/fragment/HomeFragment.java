package com.example.booksummary.fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.booksummary.Adapter.RecycleNewBookAdapter;
import com.example.booksummary.Adapter.RecycleViewHomeAdepter;
import com.example.booksummary.R;
import com.example.booksummary.modules.DisplayNewBook;
import com.example.booksummary.modules.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("MissingInflatedId")
    CircleImageView userpic;
    TextView name;
    ImageView cartAdd;
    LottieAnimationView animationView;
    DatabaseReference  databaseReference1, databaseReference2;
    RecyclerView.LayoutManager Recycler;
    ArrayList<DisplayNewBook> NewBookList = new ArrayList<>();
    ArrayList<DisplayNewBook> NewBookList1 = new ArrayList<>();
    RecycleViewHomeAdepter Adepter;
    RecycleNewBookAdapter newBookAdapter;
    private LinearLayout booksId;
    private ScrollView scrollView;
    RecyclerView recyclerView, NewBooksRecycleView;
    FirebaseAuth mAuth;
    String UserId1;
    private StorageReference storageReference;
    private Map<String, String> Books = new HashMap<String, String>() {{
        put("1", "Books/content (7).webp");
        put("2", "Books/content (5).jpeg");
        put("3", "Books/content (6).jpeg");
        put("4", "Books/content (1).webp");
        put("5", "Books/content (4).jpeg");
        put("6", "Books/content (3).jpeg");
        put("7", "Books/content (2).jpeg");
        put("8", "Books/content.jpeg");
    }};
    private Map<String, String> NewBooks = new HashMap<String, String>() {{
        put("1", "NewBooks/Book1.webp");
        put("2", "NewBooks/Book2.webp");
        put("3", "NewBooks/Book3.webp");
        put("4", "NewBooks/Book4.webp");
        put("5", "NewBooks/Book5.webp");
        put("6", "NewBooks/Book6.webp");
        put("7", "NewBooks/Book7.webp");
        put("8", "NewBooks/Book8.webp");
        put("9", "NewBooks/Book9.webp");
        put("10", "NewBooks/Book10.jpeg");
        put("11", "NewBooks/Book11.jpeg");
    }};// Map of keys and their corresponding image paths in Firebase Storage

    private Map<String, String> Audio = new HashMap<String, String>() {{
        put("1", "Audio/Ramayan.mp3");
        put("2", "Audio/Lateral.mp3");
        put("3", "Audio/Civil.mp3");
        put("4", "Audio/BhagwatGeeta.mp3");
        put("5", "Audio/Ramayan.mp3");
        put("6", "Audio/Lateral.mp3");
        put("7", "Audio/Civil.mp3");
        put("8", "Audio/BhagwatGeeta.mp3");

    }};
    private Map<String, String> NewAudio = new HashMap<String, String>() {{
        put("1", "Audio/CultOfOne.mp3");
        put("2", "Audio/DonotBelieve.mp3");
        put("3", "Audio/ArtOfWar.mp3");
        put("4", "Audio/PSubMind.mp3");
        put("5", "Audio/StopOverthinking.mp3");
        put("6", "Audio/FocusMatters.mp3");
        put("7", "Audio/StopOverthinking.mp3");
        put("8", "Audio/SilentPatient.mp3");
        put("9", "Audio/RulseOfLife.mp3");
        put("10", "Audio/SilentPatient.mp3");
        put("11", "Audio/SilentPatient.mp3");

    }};
    private final int GALLERY_REQ_CODE = 102;
    private static final int REQUEST_CODE = 100;

    @SuppressLint({"MissingInflatedId", "ResourceType"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment


        userpic = view.findViewById(R.id.userpic);
        name = view.findViewById(R.id.nameUser);
        mAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.BooksRecycleView);
        NewBooksRecycleView = view.findViewById(R.id.NewBooksRecycleView);
        booksId = view.findViewById(R.id.BooksId);
        scrollView = view.findViewById(R.id.scrollView);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference1 = FirebaseDatabase.getInstance().getReference("Books");
        databaseReference2 = FirebaseDatabase.getInstance().getReference("NewBook");

        saveImageUrlsToDatabase(Books);
        NewBookSaveImageUrlsToDatabase(NewBooks);
        saveAudioUrlsToDatabase(Audio,databaseReference1);
        saveAudioUrlsToDatabase(NewAudio,databaseReference2);
//        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        UserId1 = firebaseUser.getUid();

        recyclerView.setLayoutManager(Recycler);
        fetchBooksFromFirebase();
        Adepter = new RecycleViewHomeAdepter(getActivity(), NewBookList1, UserId1);
        LinearLayoutManager horizontalLayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayout);
        recyclerView.setAdapter(Adepter);

        NewBooksRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fetchNewBooksFromFirebase();
        newBookAdapter = new RecycleNewBookAdapter(getActivity(), NewBookList,UserId1);
        NewBooksRecycleView.setAdapter(newBookAdapter);





        animationView = view.findViewById(R.id.progressBar);
        animationView.setAnimation(R.raw.animation12);
        animationView.playAnimation();

        if (UserId1 != null) {
            fetchImageUrl(UserId1);
            //fetchImageUrl(UserId);
        }


        return view;
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
                        name.setText(user.getUsername());
                    } else {
                        userpic.setImageResource(R.drawable.user);
                        name.setText(user.getUsername());
                    }
                }
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
                .into(userpic);
    }

    private void saveImageUrlsToDatabase(Map<String, String> imagePaths) {
        for (Map.Entry<String, String> entry : imagePaths.entrySet()) {
            String key = entry.getKey();
            String imagePath = entry.getValue();
            StorageReference imageRef = storageReference.child(imagePath);

            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                databaseReference1.child(key).child("BookPic").setValue(downloadUrl)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    private void NewBookSaveImageUrlsToDatabase(Map<String, String> imagePaths) {
        for (Map.Entry<String, String> entry : imagePaths.entrySet()) {
            String key = entry.getKey();
            String imagePath = entry.getValue();
            StorageReference imageRef = storageReference.child(imagePath);

            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                databaseReference2.child(key).child("BookPic").setValue(downloadUrl)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    private void fetchBooksFromFirebase() {
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NewBookList1.clear();

                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                    String BAuthor = bookSnapshot.child("BookAuthor").getValue(String.class);
                    String BDate = bookSnapshot.child("BookDate").getValue(String.class);
                    String BName = bookSnapshot.child("BookName").getValue(String.class);
                    String BookPic = bookSnapshot.child("BookPic").getValue(String.class);
                    String BookAudio = bookSnapshot.child("BookAudio").getValue(String.class);
//                    String BId = bookSnapshot.child("bid").getValue(String.class);
//                    Integer AddNumber = bookSnapshot.child("cartAdd").getValue(Integer.class);

                    if (BName != null && BookPic != null && BAuthor != null) {
                        NewBookList1.add(new DisplayNewBook(BName, BookPic, BDate, BAuthor,BookAudio));
                    }
                }
                Adepter.notifyDataSetChanged();
                booksId.setVisibility(View.VISIBLE);
                checkIfDataLoaded();
                //populated with Book objects from Firebase
                // You can use it to update your UI or adapter here
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("MainActivity", "Failed to read value.", databaseError.toException());
                booksId.setVisibility(View.GONE);
                checkIfDataLoaded();
            }
        });
    }

    private void fetchNewBooksFromFirebase() {
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NewBookList.clear();

                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                    String BAuthor = bookSnapshot.child("BAuthor").getValue(String.class);
                    String BDate = bookSnapshot.child("BDate").getValue(String.class);
                    String BName = bookSnapshot.child("BName").getValue(String.class);
                    String BookPic = bookSnapshot.child("BookPic").getValue(String.class);
                    String BookAudio = bookSnapshot.child("BookAudio").getValue(String.class);
//                    String BId = bookSnapshot.child("bid").getValue(String.class);
//                    Integer AddNumber = bookSnapshot.child("cartAdd").getValue(Integer.class);

                    if (BName != null && BookPic != null && BAuthor != null) {
                        NewBookList.add(new DisplayNewBook(BName, BookPic, BDate, BAuthor,BookAudio));
                    }
                }
                newBookAdapter.notifyDataSetChanged();
                scrollView.setVisibility(View.VISIBLE);
                checkIfDataLoaded();
                //populated with Book objects from Firebase
                // You can use it to update your UI or adapter here
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("MainActivity", "Failed to read value.", databaseError.toException());
                scrollView.setVisibility(View.GONE);
                checkIfDataLoaded();
            }
        });
    }
    private void saveAudioUrlsToDatabase(Map<String, String> imagePaths, DatabaseReference databaseReference) {
        for (Map.Entry<String, String> entry : imagePaths.entrySet()) {
            String key = entry.getKey();
            String imagePath = entry.getValue();
            StorageReference imageRef = storageReference.child(imagePath);

            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                databaseReference.child(key).child("BookAudio").setValue(downloadUrl)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    private void checkIfDataLoaded() {
        if (booksId.getVisibility() == View.VISIBLE && scrollView.getVisibility() == View.VISIBLE) {

            animationView.setVisibility(View.GONE);
        }
    }
    private void favorite() {
    }
}