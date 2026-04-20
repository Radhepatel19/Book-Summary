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
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booksummary.HeartView;
import com.example.booksummary.MusicPlayer;
import com.example.booksummary.R;
import com.example.booksummary.modules.DisplayNewBook;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecycleFavoriteBookAdapter extends RecyclerView.Adapter<RecycleFavoriteBookAdapter.ViewHolder> {
    Context context;
    ArrayList<DisplayNewBook> arrayList;
    String User;
    public RecycleFavoriteBookAdapter(@NonNull Context context, ArrayList<DisplayNewBook> arrayList, String User) {
        this.context = context;
        this.arrayList = arrayList;
        this.User = User;
    }

    @NonNull
    @Override
    public RecycleFavoriteBookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.searchlayout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleFavoriteBookAdapter.ViewHolder holder, int position) {
        DisplayNewBook model = arrayList.get(position);
        holder.BName.setText(model.getBName());
        holder.BAuthor.setText(model.getBAuthor());
        holder.BDate.setText(model.getBDate());
        Picasso.get().load(model.getBPicUrl1()).into(holder.BImage);


        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Favorite").child(User);
        holder.BookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    DisplayNewBook clickedBook = arrayList.get(adapterPosition);
                    Intent intent = new Intent(context, MusicPlayer.class);
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
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
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
