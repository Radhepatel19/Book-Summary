package com.example.booksummary.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.booksummary.HeartView;
import com.example.booksummary.HomePage;
import com.example.booksummary.MusicPlayer;
import com.example.booksummary.R;
import com.example.booksummary.modules.DisplayNewBook;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class RecycleViewHomeAdepter extends RecyclerView.Adapter<RecycleViewHomeAdepter.ViewHolder>{
    Context context;
    ArrayList<DisplayNewBook> arrayList;
    String UserId;

    public RecycleViewHomeAdepter(Context context, ArrayList<DisplayNewBook> arrayList, String userId) {
        this.context = context;
        this.arrayList = arrayList;
        this.UserId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.booklayout,parent,false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,@SuppressLint("RecyclerView") int position) {

            DisplayNewBook model = arrayList.get(position);
            holder.BookName.setText(model.getBName());
            Picasso.get().load(model.getBPicUrl1()).into(holder.BookImage);
            DatabaseReference databaseReference;
            databaseReference = FirebaseDatabase.getInstance().getReference("Favorite").child(UserId);


        holder.FrameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        DisplayNewBook clickedBook = arrayList.get(adapterPosition);
                        Intent  intent = new Intent(context, MusicPlayer.class);
                        intent.putExtra("BName", clickedBook.getBName());
                        intent.putExtra("BAuthor", clickedBook.getBAuthor());
                        intent.putExtra("BDate", clickedBook.getBDate());
                        intent.putExtra("BPicUrl1", clickedBook.getBPicUrl1());
                        intent.putExtra("BAudio",clickedBook.getAudio());
//                        intent.putExtra("Bid", clickedBook.getBid());
//                        intent.putExtra("cartAdd", String.valueOf(clickedBook.getCartAdd()));
                        context.startActivity(intent);
                    }
                }
            });
        int adapterPosition = holder.getAdapterPosition();
        DisplayNewBook clickedBook = arrayList.get(adapterPosition);
        Query query = databaseReference.orderByChild("bname").equalTo(clickedBook.getBName());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    holder.heart.setHeartColor(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        holder.heart.setOnHeartStateChangeListener(new HeartView.OnHeartStateChangeListener() {
            @Override
            public void onHeartStateChanged(boolean isRed) {
                int adapterPosition = holder.getAdapterPosition();
                DisplayNewBook clickedBook = arrayList.get(adapterPosition);
                Query query = databaseReference.orderByChild("bname").equalTo(clickedBook.getBName());
                if (isRed) {

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                            } else {
                                // Book does not exist in the cart, add it
                                String key = databaseReference.push().getKey();
                                DisplayNewBook displayNewBook = new DisplayNewBook(clickedBook.getBName(), clickedBook.getBPicUrl1(), clickedBook.getBDate(), clickedBook.getBAuthor(), key,clickedBook.getAudio());
                                databaseReference.child(key).setValue(displayNewBook)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                // Handle success
                                                if (context instanceof HomePage) {
                                                    // Navigate to the Add to Cart fragment
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Handle failure
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle onCancelled
                        }
                    });
                }else{
                    // Remove from favorites
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                snapshot.getRef().removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("HeartView", "Book removed from favorites: " + clickedBook.getBName());
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("HeartView", "Failed to remove book from favorites: " + clickedBook.getBName() + " Error: " + e.getMessage());
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle onCancelled
                            Log.e("HeartView", "DatabaseError: " + databaseError.getMessage());
                        }
                    });
                }
            }
            });
        }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView BookImage;
        TextView BookName;
        FrameLayout FrameLayout;
        HeartView heart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            heart = itemView.findViewById(R.id.Heart);
            FrameLayout = itemView.findViewById(R.id.FrameLayout);
            BookImage = itemView.findViewById(R.id.BookImage);
            BookName = itemView.findViewById(R.id.NameOfBook);
        }
    }
}
