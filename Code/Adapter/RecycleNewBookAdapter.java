package com.example.booksummary.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecycleNewBookAdapter extends RecyclerView.Adapter<RecycleNewBookAdapter.ViewHolder> {
    @NonNull
    Context context;
    ArrayList<DisplayNewBook> NewBookList;
    String UserId;

    public RecycleNewBookAdapter(@NonNull Context context, ArrayList<DisplayNewBook> newBookList, String UserId) {
        this.context = context;
        NewBookList = newBookList;
        this.UserId = UserId;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.searchlayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DisplayNewBook model = NewBookList.get(position);
        holder.BName.setText(model.getBName());
        holder.BAuthor.setText(model.getBAuthor());
        holder.BDate.setText(model.getBDate());
        Picasso.get().load(model.getBPicUrl1()).into(holder.BImage);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(UserId).child("Favorite");


        holder.BookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    DisplayNewBook clickedBook = NewBookList.get(adapterPosition);
                    Intent intent = new Intent(context, MusicPlayer.class);
                    intent.putExtra("BName", clickedBook.getBName());
                    intent.putExtra("BAuthor", clickedBook.getBAuthor());
                    intent.putExtra("BDate", clickedBook.getBDate());
                    intent.putExtra("BPicUrl1", clickedBook.getBPicUrl1());
                    intent.putExtra("BAudio",clickedBook.getAudio());
                    context.startActivity(intent);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return NewBookList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView BImage;
        TextView BName, BAuthor, BDate;
        LinearLayout BookLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            BookLayout = itemView.findViewById(R.id.BookLayout);
            BImage = itemView.findViewById(R.id.BImage);
            BName = itemView.findViewById(R.id.BookName);
            BAuthor = itemView.findViewById(R.id.BookAuthor);
            BDate = itemView.findViewById(R.id.BookReleaseDate);
        }
    }
}
