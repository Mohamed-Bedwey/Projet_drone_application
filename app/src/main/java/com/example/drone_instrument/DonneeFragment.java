package com.example.drone_instrument;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class DonneeFragment extends Fragment {

    TextView long_msg, lati_msg, vit_msg, temp_msg, lum_msg, son_msg, alti_msg,cmd_drone;
    ImageView img_vit, img_son, img_lum, img_temp;

    Workbook excel_file = new HSSFWorkbook();
    Cell cell = null;
    Row row = null;
    Sheet sheet = excel_file.createSheet("data");
    int cpt;
    String[][] save_data = new String[8][14400];
    File myExternfile = null;
    FileOutputStream fileOutputStream = null;

    Random random;

    Handler handler;
    Runnable run;
    LocationManager locationManager;

    String valLongitude = "20", valLatitude = "20";
    String cmd_longitude, cmd_latitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_donnee, container, false);

        //===================================== Initialisation des textView et des ImageView =============================================//

        long_msg = (TextView) view.findViewById(R.id.longitude);
        lati_msg = (TextView) view.findViewById(R.id.latitude);
        vit_msg = (TextView) view.findViewById(R.id.vitesse);
        temp_msg = (TextView) view.findViewById(R.id.temperature);
        lum_msg = (TextView) view.findViewById(R.id.luminosite);
        son_msg = (TextView) view.findViewById(R.id.son);
        alti_msg = (TextView) view.findViewById(R.id.altitude);
        cmd_drone = (TextView) view.findViewById(R.id.commande_drone);

        img_lum = (ImageView) view.findViewById(R.id.imageLuminosite);
        img_son = (ImageView) view.findViewById(R.id.imageSon);
        img_temp = (ImageView) view.findViewById(R.id.imageTemperature);
        img_vit = (ImageView) view.findViewById(R.id.imageVitesse);

        myExternfile = new File(getActivity().getExternalFilesDir("Save_Data"), "Drone_Data.xls"); // Chemin de la base de données Excel dans le stockage interne du téléphone
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        random = new Random();
        handler = new Handler();


        //========================== Affichage du graph en fonction de la donnée souhaiter ============================//

        img_vit.setOnClickListener(new View.OnClickListener() { // Affichage du graph de la vitesse
            @Override
            public void onClick(View v) {
                write_data();
                Graph_data("Vitesse");
            }
        });

        img_temp.setOnClickListener(new View.OnClickListener() { // Affichage du graph de la Temperature
            @Override
            public void onClick(View v) {
                write_data();
                Graph_data("Temperature");
            }
        });

        img_lum.setOnClickListener(new View.OnClickListener() { // Affichage du graph de la Luminosite
            @Override
            public void onClick(View v) {
                write_data();
                Graph_data("Luminosite");
            }
        });

        img_son.setOnClickListener(new View.OnClickListener() { // Affichage du graph du son
            @Override
            public void onClick(View v) {
                write_data();
                Graph_data("Son");
            }
        });

        //======================== Affichage des données recue en temps réel (aléatoire) ==========//
        run = new Runnable() {

            @Override
            public void run() {

                save_data[0][cpt] = String.valueOf(cpt); // Temps

                int val = random.nextInt(500); // Vitesse : génération de valeur aléatoire
                vit_msg.setText(String.valueOf(val)); // Affichage de la valeur sur l'application
                save_data[1][cpt] = String.valueOf(val); // Stockage de la valeur dans un tableau

                int val2 = random.nextInt(500); // Temperature
                temp_msg.setText(String.valueOf(val2));
                save_data[2][cpt] = String.valueOf(val2);

                int val3 = random.nextInt(500); // Luminosite
                lum_msg.setText(String.valueOf(val3));
                save_data[3][cpt] = String.valueOf(val3);

                int val4 = random.nextInt(500); // Son
                son_msg.setText(String.valueOf(val4));
                save_data[4][cpt] = String.valueOf(val4);

                int val5 = random.nextInt(500); // Longitude
                long_msg.setText(String.valueOf(val5));
                save_data[5][cpt] = String.valueOf(val5);

                int val6 = random.nextInt(500); // Latitude
                lati_msg.setText(String.valueOf(val6));
                save_data[6][cpt] = String.valueOf(val6);

                int val7 = random.nextInt(500); // Altitude
                alti_msg.setText(String.valueOf(val7));
                save_data[7][cpt] = String.valueOf(val7);


                cpt++;

                handler.postDelayed(this, 1000); // Rafraichissement toutes les secondes

            }
        };
        handler.post(run);

        commande_drone(); // Fonction de suivie de personne

        return view;
    }

    private void Graph_data(String val) // Affichage du graph_fragment
    {
        Bundle bundle = new Bundle();
        GraphFragment graphFragment = new GraphFragment();
        graphFragment.donneeFragment = this;
        bundle.putString("data", val); // Envoie de donnée au GraphFragment
        graphFragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, graphFragment);
        fragmentTransaction.commit();
    }

    void write_data() // // Ecriture des données dans la base de données Excel
    {

        // =========== Creation des colonnes ============== //
        row = sheet.createRow(0);

        cell = row.createCell(0);
        cell.setCellValue("Time");

        cell = row.createCell(1);
        cell.setCellValue("Vitesse");

        cell = row.createCell(2);
        cell.setCellValue("Temperature");

        cell = row.createCell(3);
        cell.setCellValue("Luminosite");

        cell = row.createCell(4);
        cell.setCellValue("Son");

        cell = row.createCell(5);
        cell.setCellValue("Longitude");

        cell = row.createCell(6);
        cell.setCellValue("Latitude");

        cell = row.createCell(7);
        cell.setCellValue("Altitude");

        // ================= taille des colones ============== //
        sheet.setColumnWidth(0, (20 * 200));
        sheet.setColumnWidth(1, (20 * 200));
        sheet.setColumnWidth(2, (20 * 200));
        sheet.setColumnWidth(3, (20 * 200));
        sheet.setColumnWidth(4, (20 * 300));
        sheet.setColumnWidth(5, (20 * 300));
        sheet.setColumnWidth(6, (20 * 300));
        sheet.setColumnWidth(7, (20 * 300));

        for (int i = 0; i < cpt; i++) // Ecriture des données du tableau dans la base de donnée Excel
        {
            row = sheet.createRow(i + 1);

            cell = row.createCell(0);
            cell.setCellValue(save_data[0][i]);

            cell = row.createCell(1);
            cell.setCellValue(save_data[1][i]);

            cell = row.createCell(2);
            cell.setCellValue(save_data[2][i]);

            cell = row.createCell(3);
            cell.setCellValue(save_data[3][i]);

            cell = row.createCell(4);
            cell.setCellValue(save_data[4][i]);

            cell = row.createCell(5);
            cell.setCellValue(save_data[5][i]);

            cell = row.createCell(6);
            cell.setCellValue(save_data[6][i]);

            cell = row.createCell(7);
            cell.setCellValue(save_data[7][i]);
        }

        try { // Enregistrement du fichier Excel dans le stockage interne du téléphone
            fileOutputStream = new FileOutputStream(myExternfile);
            excel_file.write(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeHandler() {
        handler.removeCallbacks(run);
    }

    private void commande_drone() {

        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                cmd_drone.setText(location.getLatitude() + " " + location.getLongitude());

                // Envoie de commande au drone pour suivre l'utilisateur
                if(Double.parseDouble(valLatitude) > location.getLatitude())
                {
                    cmd_latitude = "Ouest";
                }
                else if (Double.parseDouble(valLatitude) < location.getLatitude())
                {
                    cmd_latitude = "Est";
                }
                else
                {
                    cmd_latitude = " ";
                }

                if(Double.parseDouble(valLongitude) > location.getLongitude())
                {
                    cmd_longitude = "Nord";
                }
                else if (Double.parseDouble(valLongitude) < location.getLongitude())
                {
                    cmd_longitude = "Sud";
                }
                else
                {
                    cmd_longitude = " ";
                }

                cmd_drone.setText(cmd_longitude + " " + cmd_latitude);

            }
        });
    }


}