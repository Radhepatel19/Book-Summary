package com.example.booksummary.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booksummary.HeartView;
import com.example.booksummary.MusicPlayer;
import com.example.booksummary.R;
import com.example.booksummary.modules.DisplayNewBook;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchAdepter extends RecyclerView.Adapter<SearchAdepter.ViewHolder> {

    Context context;
    private List<DisplayNewBook> itemList;
    private List<DisplayNewBook> itemListFull;
    String UserId;

    public SearchAdepter(List<DisplayNewBook> itemList, Context context,String UserId) {
        this.itemList = itemList;
        this.context = context;
        this.UserId = UserId;
        itemListFull = new ArrayList<>(itemList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.newbooklayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DisplayNewBook model = itemList.get(position);
        holder.BName.setText(model.getBName());
        holder.BAuthor.setText(model.getBAuthor());
        holder.BDate.setText(model.getBDate());
        Picasso.get().load(model.getBPicUrl1()).into(holder.BImage);


        holder.BookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    DisplayNewBook clickedBook = itemList.get(adapterPosition);
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
        return itemList.size();
    }
    public void filter(String text) {
        itemList.clear();
        if (text.isEmpty()) {
            itemList.addAll(itemListFull);
        } else {
            text = text.toLowerCase();
            for (DisplayNewBook item : itemListFull) {
                if (item.getBName().toLowerCase().contains(text)) {
                    itemList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView BImage;
        TextView BName, BAuthor , BDate;
        LinearLayout BookLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            BookLayout = itemView.findViewById(R.id.BookLayout);
            BImage = itemView.findViewById(R.id.BImage);
            BName = itemView.findViewById(R.id.BookName);
            BAuthor = itemView.findViewById(R.id.BookAuthor);
            BDate = itemView.findViewById(R.id.BookReleaseDate);
        }
    }
    public void updateList(List<DisplayNewBook> newList) {
        itemListFull.clear();
        itemListFull.addAll(newList);
        filter(""); // Reset the filter to show the new list
    }
}
