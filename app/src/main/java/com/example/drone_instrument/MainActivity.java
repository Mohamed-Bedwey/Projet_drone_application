package com.example.drone_instrument;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drone_instrument.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    DonneeFragment donneeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        donneeFragment = new DonneeFragment();

        replaceFragment(donneeFragment);

        //======================================= Affichage d'un fragment via la barre de navigation ========================================//
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.donnée:
                    donneeFragment.removeHandler();
                    replaceFragment(donneeFragment);
                    break;
                case R.id.map:
                    replaceFragment(new MapFragment());
                    break;
            }
            return true;
        });


    }

    private void replaceFragment (Fragment fragment) // Affichage d'un fragment
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }


}