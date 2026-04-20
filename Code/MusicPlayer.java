package com.example.booksummary;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.airbnb.lottie.LottieAnimationView;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MusicPlayer extends AppCompatActivity {
TextView songName,AuthorName,TimeOfSong,lastTimeOfSong,BookDate;
ImageView Previous,Next,PlayAndPause,Repeat,arrowBack,imageOfBook;
DatabaseReference databaseReference1,databaseReference,databaseReference2;
    MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    RelativeLayout RelativeLayout1;
    boolean isRepeat = true;
    HeartView MusicHeart;
    FirebaseAuth mAuth;
SeekBar seekBar;
    LottieAnimationView animationView;
    int currentSongIndex = 0;
    boolean isPlaying = true;
    String UserId;
    List<DisplayNewBook> bookList = new ArrayList<>();
    List<DisplayNewBook> bookList1 = new ArrayList<>();
    List<DisplayNewBook> combinedList = new ArrayList<>();
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        arrowBack = findViewById(R.id.arrowBack);
        songName = findViewById(R.id.songName);
        AuthorName = findViewById(R.id.AuthorName);
        TimeOfSong = findViewById(R.id.TimeOfSong);
        lastTimeOfSong = findViewById(R.id.lastTimeOfSong);
        Previous = findViewById(R.id.Previous);
        Next = findViewById(R.id.Next);
        PlayAndPause = findViewById(R.id.PlayAndPause);
        Repeat = findViewById(R.id.Repeat);
        imageOfBook = findViewById(R.id.imageOfBook);
        seekBar = findViewById(R.id.SeekBar);
        RelativeLayout1 = findViewById(R.id.RelativeLayout1);
        MusicHeart = findViewById(R.id.musicheart);
        BookDate = findViewById(R.id.BookDate);

        databaseReference1 = FirebaseDatabase.getInstance().getReference("Books");
        databaseReference2 = FirebaseDatabase.getInstance().getReference("NewBook");

        animationView = findViewById(R.id.progressBar);
        animationView.setAnimation(R.raw.animation7);
        animationView.playAnimation();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        UserId = firebaseUser.getUid();

        String name = getIntent().getStringExtra("BName");
        String author = getIntent().getStringExtra("BAuthor");
        String date = getIntent().getStringExtra("BDate");
        String picUrl = getIntent().getStringExtra("BPicUrl1");
        String Audio = getIntent().getStringExtra("BAudio");

        songName.setText(name);
        AuthorName.setText(author);
        BookDate.setText(date);
        loadImage(picUrl);
        initializeMediaPlayer(Audio);
        loadSongsFromDatabase();
        loadNewSongFromDatabase();



        databaseReference = FirebaseDatabase.getInstance().getReference("Favorite").child(UserId);


        Query query = databaseReference.orderByChild("bname").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                   MusicHeart.setHeartColor(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        MusicHeart.setOnHeartStateChangeListener(new HeartView.OnHeartStateChangeListener() {
            @Override
            public void onHeartStateChanged(boolean isRed) {

                Query query = databaseReference.orderByChild("bname").equalTo(name);
                if (isRed) {

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                            } else {
                                // Book does not exist in the cart, add it
                                String key = databaseReference.push().getKey();
                                DisplayNewBook displayNewBook = new DisplayNewBook(name, picUrl, date, author, key,Audio);
                                databaseReference.child(key).setValue(displayNewBook)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                // Handle success
                                                if (getApplicationContext() instanceof HomePage) {
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

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle onCancelled
                        }
                    });
                }
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                TimeOfSong.setText(formatTime(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });


        PlayAndPause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                animatePlayPauseButton();
                if (isPlaying) {
                    mediaPlayer.pause();
                    PlayAndPause.setImageResource(R.drawable.playbuttonarrowhead); // Change icon to play
                } else {
                    mediaPlayer.start();
                    PlayAndPause.setImageResource(R.drawable.pause1); // Change icon to pause

                }
                isPlaying = !isPlaying;
            }
        });

        Repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRepeat = !isRepeat;
                mediaPlayer.setLooping(isRepeat);
                Repeat.setImageResource(isRepeat ? R.drawable.repeaton : R.drawable.multimediaoption);
                animateRepeatButton();
            }
        });

        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusicPlayer.this, HomePage.class);
                mediaPlayer.stop();
                intent.putExtra("fragment", "profile");
                startActivity(intent);
            }
        });
    }
    private void loadImage(String imageUrl) {
        // Load image into userpic ImageView using Picasso
        Picasso.get()
                .load(imageUrl)// Placeholder image// Error placeholder image/ Resize the image to avoid OutOfMemoryError (optional)// Crop the image to fit ImageView
                .into(imageOfBook);
    }
    private void initializeMediaPlayer(String audioUrl) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // Ready to play audio
                        mediaPlayer.start();// Automatically start playing when prepared
                        seekBar.setMax(mediaPlayer.getDuration());
                        Repeat.setImageResource(R.drawable.repeaton);
                        mediaPlayer.setLooping(isRepeat);
                        lastTimeOfSong.setText(formatTime(mediaPlayer.getDuration()));
                        updateSeekBar();

                        animationView.setVisibility(View.GONE);
                        RelativeLayout1.setVisibility(View.VISIBLE);
                }
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    TimeOfSong.setText(formatTime(mediaPlayer.getCurrentPosition()));
                    updateSeekBar();
                }
            }
        }, 1000);
    }

    private void playSong(DisplayNewBook newSong) {
        isPlaying = true;
        songName.setText(newSong.getBName());
        AuthorName.setText(newSong.getBAuthor());
        BookDate.setText(newSong.getBDate());
        loadImage(newSong.getBPicUrl1());
        animatePlayPauseButton();
        if (isPlaying) {
            mediaPlayer.start();
            PlayAndPause.setImageResource(R.drawable.pause1); // Change icon to pause
        }
        String audioUrl = newSong.getAudio();

        Query query = databaseReference.orderByChild("bname").equalTo(newSong.getBName());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    MusicHeart.setHeartColor(true);
                }else {
                    MusicHeart.setHeartColor(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        MusicHeart.setOnHeartStateChangeListener(new HeartView.OnHeartStateChangeListener() {
            @Override
            public void onHeartStateChanged(boolean isRed) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Favorite").child(UserId);
                Query query = databaseReference.orderByChild("bname").equalTo(newSong.getBName());
                if (isRed) {

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                            } else {
                                // Book does not exist in the cart, add it
                                String key = databaseReference.push().getKey();
                                DisplayNewBook displayNewBook = new DisplayNewBook(newSong.getBName(), newSong.getBPicUrl1(), newSong.getBDate(), newSong.getBAuthor(), key,newSong.getAudio());
                                databaseReference.child(key).setValue(displayNewBook)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                // Handle success
                                                if (getApplicationContext() instanceof HomePage) {
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

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle onCancelled
                        }
                    });
                }
            }
        });
        mediaPlayer.reset();
        animationView.setVisibility(View.VISIBLE);
        RelativeLayout1.setVisibility(View.GONE);
        initializeMediaPlayer(audioUrl);
    }
    private String formatTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    private void animatePlayPauseButton() {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(PlayAndPause, "rotation", 0f, 360f);
        rotation.setDuration(300);
        rotation.start();

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(PlayAndPause, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(PlayAndPause, "scaleY", 1f, 1.2f, 1f);
        scaleX.setDuration(300);
        scaleY.setDuration(300);
        scaleX.start();
        scaleY.start();
    }
    private void animateRepeatButton() {
        // Create a scale animation
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1f, 1.2f, // Start and end values for the X axis scaling
                1f, 1.2f, // Start and end values for the Y axis scaling
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        scaleAnimation.setDuration(300); // Animation duration
        scaleAnimation.setFillAfter(true); // Keep the result after the animation ends

        Repeat.startAnimation(scaleAnimation);
    }
    private void loadSongsFromDatabase() {
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookList.clear();
                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                    String BAuthor = bookSnapshot.child("BookAuthor").getValue(String.class);
                    String BDate = bookSnapshot.child("BookDate").getValue(String.class);
                    String BName = bookSnapshot.child("BookName").getValue(String.class);
                    String BookPic = bookSnapshot.child("BookPic").getValue(String.class);
                    String BookAudio = bookSnapshot.child("BookAudio").getValue(String.class);
                    bookList.add(new DisplayNewBook(BName, BookPic, BDate, BAuthor,BookAudio));
                }
                checkListsReady();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }
    private void loadNewSongFromDatabase() {
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookList1.clear();

                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                    String BAuthor = bookSnapshot.child("BAuthor").getValue(String.class);
                    String BDate = bookSnapshot.child("BDate").getValue(String.class);
                    String BName = bookSnapshot.child("BName").getValue(String.class);
                    String BookPic = bookSnapshot.child("BookPic").getValue(String.class);
                    String BookAudio = bookSnapshot.child("BookAudio").getValue(String.class);
//                    String BId = bookSnapshot.child("bid").getValue(String.class);
//                    Integer AddNumber = bookSnapshot.child("cartAdd").getValue(Integer.class);

                    if (BName != null && BookPic != null && BAuthor != null) {
                        bookList1.add(new DisplayNewBook(BName, BookPic, BDate, BAuthor,BookAudio));
                    }
                }
                checkListsReady();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("MainActivity", "Failed to read value.", databaseError.toException());
            }
        });
    }
    private void checkListsReady() {
        if (!bookList.isEmpty() && !bookList1.isEmpty()) {
            // Both lists are ready, combine them
            List<DisplayNewBook> combinedList = new ArrayList<>();
            combinedList.addAll(bookList);
            combinedList.addAll(bookList1);

            // Now that the combined list is ready, you can enable the Next and Previous buttons
            enableNavigationButtons(combinedList);
        }
    }
    private void enableNavigationButtons(List<DisplayNewBook> combinedList) {

        // Enable Next button
        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSongIndex = (currentSongIndex + 1) % combinedList.size();
                DisplayNewBook nextSong = combinedList.get(currentSongIndex);
                playSong(nextSong);
            }
        });

        // Enable Previous button
        Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSongIndex = (currentSongIndex - 1 + combinedList.size()) % combinedList.size();
                DisplayNewBook previousSong = combinedList.get(currentSongIndex);
                playSong(previousSong);
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
    }

}