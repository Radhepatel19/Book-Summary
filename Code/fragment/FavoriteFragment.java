package com.example.booksummary.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.booksummary.Adapter.RecycleFavoriteBookAdapter;
import com.example.booksummary.HeartView;
import com.example.booksummary.HomePage;
import com.example.booksummary.R;
import com.example.booksummary.modules.DisplayNewBook;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FavoriteFragment extends Fragment {



    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    FirebaseAuth mAuth;
    String UserId;
    RecyclerView Recyclerview;
    ArrayList<DisplayNewBook> AddToCartList = new ArrayList<>();
    RecycleFavoriteBookAdapter adapter;
    TextView EmptyCart;
    LottieAnimationView animationView;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        EmptyCart = view.findViewById(R.id.EmptyCart);
        Recyclerview = view.findViewById(R.id.AddToCartRecycleView);

        animationView = view.findViewById(R.id.progressBar);
        animationView.setAnimation(R.raw.animation12);
        animationView.playAnimation();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        UserId = firebaseUser.getUid();

        Recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        fetchAddToCartFromFirebase();
        adapter = new RecycleFavoriteBookAdapter(getActivity(), AddToCartList,UserId);
        Recyclerview.setAdapter(adapter);


        return view;
    }

    private void fetchAddToCartFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Favorite").child(UserId);


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AddToCartList.clear();
                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                    String BAudio = bookSnapshot.child("audio").getValue(String.class);
                    String BAuthor = bookSnapshot.child("bauthor").getValue(String.class);
                    String BDate = bookSnapshot.child("bdate").getValue(String.class);
                    String BId = bookSnapshot.child("bid").getValue(String.class);
                    String BName = bookSnapshot.child("bname").getValue(String.class);
                    String  bpicUrl1 = bookSnapshot.child("bpicUrl1").getValue(String.class);

                    if (BName != null &&  BAuthor != null && BAudio != null) {
                        AddToCartList.add(new DisplayNewBook(BName,bpicUrl1,BDate,BAuthor, BId,BAudio));
                        Recyclerview.setVisibility(View.VISIBLE);
                    }
                }
                adapter.notifyDataSetChanged();
                checkIfDataLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }

    private void checkIfDataLoaded() {
        if (Recyclerview.getVisibility() == View.VISIBLE || EmptyCart.getVisibility() == View.VISIBLE) {
            animationView.setVisibility(View.GONE);
        }else {
            animationView.setVisibility(View.GONE);
            EmptyCart.setVisibility(View.VISIBLE);
        }
    }
}