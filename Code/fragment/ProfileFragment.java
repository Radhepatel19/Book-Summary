package com.example.booksummary.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.booksummary.AboutUs;
import com.example.booksummary.EditProfile;
import com.example.booksummary.Faqs;
import com.example.booksummary.HomePage;
import com.example.booksummary.MainActivity;
import com.example.booksummary.R;
import com.example.booksummary.RatingApp;
import com.example.booksummary.modules.Users;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    ImageView UserPic;
    TextView UserName, UserEmail, AboutP;
    AppCompatButton EditProfile;
    LinearLayout Search,Home,Favorite,AboutUs,Faqs,Rate,LinearAbout;
    FirebaseAuth mAuth;
    String UserId;

    @Override
    @SuppressLint("MissingInflatedId")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        AppCompatButton logout = view.findViewById(R.id.Logout);
        UserPic = view.findViewById(R.id.Userpic);
        AboutP = view.findViewById(R.id.AboutP);
        UserName = view.findViewById(R.id.UserName);
        LinearAbout = view.findViewById(R.id.LinearAbout);
        UserEmail = view.findViewById(R.id.UserEmail);
        EditProfile = view.findViewById(R.id.EditProfile);
        Search = view.findViewById(R.id.SearchBooks);
        Home = view.findViewById(R.id.HomeFragment);
        Favorite = view.findViewById(R.id.favroriteBook);
        AboutUs = view.findViewById(R.id.AboutUs);
        Faqs = view.findViewById(R.id.faqs);
        Rate = view.findViewById(R.id.Rate);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences;
                sharedPreferences = getContext().getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                editor1.putBoolean("flag", false);
                editor1.apply();
                // Optionally update UI or navigate to login screen after sign-out
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        UserId = firebaseUser.getUid();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        databaseRef.child(UserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Users user = dataSnapshot.getValue(Users.class);
                    if (user != null && user.getProfilePic() != null) {
                        loadImage(user.getProfilePic());
                    }else{
                        UserPic.setImageResource(R.drawable.user);

                    }
                    UserName.setText(user.getUsername());
                    UserEmail.setText(user.getMail());
                    if (user.getAboutUs() != null && !user.getAboutUs().isEmpty()) {
                        AboutP.setText(user.getAboutUs().toString());
                        LinearAbout.setVisibility(View.VISIBLE);
                        AboutP.setVisibility(View.VISIBLE);
                    } else {
                        LinearAbout.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomePage activity = (HomePage) getActivity();
                BottomNavigationView bottomNavigationView = activity.findViewById(R.id.Bn);
                bottomNavigationView.setSelectedItemId(R.id.nav_search);
            }
        });
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomePage activity = (HomePage) getActivity();
                BottomNavigationView bottomNavigationView = activity.findViewById(R.id.Bn);
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
            }
        });
        Favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomePage activity = (HomePage) getActivity();
                BottomNavigationView bottomNavigationView = activity.findViewById(R.id.Bn);
                bottomNavigationView.setSelectedItemId(R.id.nav_bookmark);
            }
        });
        EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),EditProfile.class));

            }
        });
        AboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),com.example.booksummary.AboutUs.class));
            }
        });
        Rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RatingApp.class));

            }
        });
        Faqs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),com.example.booksummary.Faqs.class));

            }
        });

        return view;
    }
    private void loadImage(String imageUrl) {
        // Load image into Userpic ImageView using Picasso
        Picasso.get()
                .load(imageUrl)// Placeholder image
                .into(UserPic);
    }

}