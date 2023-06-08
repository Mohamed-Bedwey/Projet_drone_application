package com.example.drone_instrument;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.example.drone_instrument.databinding.ActivityMainBinding;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    DonneeFragment donneeFragment;
    Handler handler2;
    Runnable run2;
    Bundle bundle;

    int cpt;
    String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        handler2 = new Handler();
        bundle = new Bundle();

        donneeFragment = new DonneeFragment();

        replaceFragment(donneeFragment);

        donneeFragment.setArguments(bundle);

        //======================================= Ouverture de fragment via la barre de navigation ========================================//
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

//        run2 = new Runnable() {
//
//            @Override
//            public void run() {
//
//                cpt ++;
//                bundle.putString("data_test",String.valueOf(cpt));
//                handler2.postDelayed(this,1000);
//
//            }
//        };
//        handler2.post(run2);
    }

    private void replaceFragment (Fragment fragment) // Ouverture d'un fragment
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }


}