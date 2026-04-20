package com.example.booksummary.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.booksummary.Adapter.SearchAdepter;
import com.example.booksummary.R;
import com.example.booksummary.modules.DisplayNewBook;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.DataInput;
import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference1 = FirebaseDatabase.getInstance().getReference("Books");
        databaseReference2 = FirebaseDatabase.getInstance().getReference("NewBook");
    }

    private RecyclerView recyclerView;
    private com.example.booksummary.Adapter.SearchAdepter SearchAdepter;
    private List<DisplayNewBook> itemList;
    private android.widget.SearchView searchView;
    private DatabaseReference databaseReference1;
    private DatabaseReference databaseReference2;
    FirebaseAuth mAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.recyclerView);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String UserId = firebaseUser.getUid();


        itemList = new ArrayList<>();
        fetchDataFromFirebase();
        SearchAdepter = new SearchAdepter(itemList,getContext(),UserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(SearchAdepter);

        // Initialize the Firebase references
        // Fetch data from both references

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                SearchAdepter.filter(s);
                return false;
            }
        });


        return view;
    }

    private void fetchDataFromFirebase() {
        // Clear the list before fetching new data
        itemList.clear();

        // Fetch data from the first reference
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<DisplayNewBook> tempList = new ArrayList<>();
                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                    String BAuthor = bookSnapshot.child("BookAuthor").getValue(String.class);
                    String BDate = bookSnapshot.child("BookDate").getValue(String.class);
                    String BName = bookSnapshot.child("BookName").getValue(String.class);
                    String BookPic = bookSnapshot.child("BookPic").getValue(String.class);
                    String BookAudio = bookSnapshot.child("BookAudio").getValue(String.class);
                    if (BName != null && BookPic != null  && BAuthor != null) {
                        tempList.add(new DisplayNewBook(BName,BookPic,BDate,BAuthor,BookAudio));
                    }
                }
                // Fetch data from the second reference after the first one completes
                fetchDataFromSecondReference(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchDataFromSecondReference(final List<DisplayNewBook> tempList) {
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                    String BAuthor = bookSnapshot.child("BAuthor").getValue(String.class);
                    String BDate = bookSnapshot.child("BDate").getValue(String.class);
                    String BName = bookSnapshot.child("BName").getValue(String.class);
                    String BookPic = bookSnapshot.child("BookPic").getValue(String.class);
                    String BookAudio = bookSnapshot.child("BookAudio").getValue(String.class);
                    if (BName != null && BookPic != null  && BAuthor != null) {
                        tempList.add(new DisplayNewBook(BName,BookPic,BDate,BAuthor,BookAudio));
                    }
                }
                // Update the adapter with the combined list
                itemList.addAll(tempList);
                SearchAdepter.updateList(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}