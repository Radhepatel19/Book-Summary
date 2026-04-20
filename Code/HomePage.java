package com.example.booksummary;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;


import com.example.booksummary.fragment.FavoriteFragment;
import com.example.booksummary.fragment.HomeFragment;
import com.example.booksummary.fragment.ProfileFragment;
import com.example.booksummary.fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePage extends AppCompatActivity {

    BottomNavigationView bn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        bn = findViewById(R.id.Bn);

        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.putBoolean("flag", true);
        editor1.apply();

        Intent intent = getIntent();
        String fragment = intent.getStringExtra("fragment");
        if ("profile".equals(fragment)) {
            bn.setSelectedItemId(R.id.nav_home);
            // Load the ProfileFragment
            loadFrag(new HomeFragment(),false);
        } else if ("EditProfile".equals(fragment)) {
            bn.setSelectedItemId(R.id.nav_Profile);
            // Load the ProfileFragment
            loadFrag(new ProfileFragment(),false);
        }else {
            Log.d("Tag", "This message");
            loadFrag(new HomeFragment(), false);
        }

        bn.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    loadFrag(new HomeFragment(), false);
                } else if (item.getItemId() == R.id.nav_search) {
                    loadFrag(new SearchFragment(), false);
                }else if (item.getItemId() == R.id.nav_bookmark) {
                    loadFrag(new FavoriteFragment(), false);
                } else if (item.getItemId() == R.id.nav_Profile) {
                    loadFrag(new ProfileFragment(), false);
                }
                return true;
            }
        });
    }
    public void loadFrag(Fragment f, boolean flag){

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if(!flag) {
            ft.replace(R.id.container123,f);
        }
        ft.commit();

    }

    @Override
    public void onBackPressed() {
        if (bn.getSelectedItemId() == R.id.nav_home){
            super.onBackPressed();
            finish();
        }else {
            bn.setSelectedItemId(R.id.nav_home);
        }
    }
}