package com.example.drone_instrument;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class DonneeFragment extends Fragment {

    TextView long_msg,lati_msg,vit_msg,temp_msg,lum_msg,son_msg,alti_msg;

    Workbook excel_file = new HSSFWorkbook();
    Cell cell = null;
    Row row = null;
    Sheet sheet = excel_file.createSheet("data");
    int cpt;
    String[][] data_save = new String[8][14400];
    File myExternfile = null;
    FileOutputStream fileOutputStream = null;

    Random random;

    Handler handler;
    Runnable run;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_donnee, container, false);

        //===================================== Initialisation des textView =============================================//

        long_msg = (TextView) view.findViewById(R.id.longitude);
        lati_msg = (TextView) view.findViewById(R.id.latitude);
        vit_msg = (TextView) view.findViewById(R.id.vitesse);
        temp_msg = (TextView) view.findViewById(R.id.temperature);
        lum_msg = (TextView) view.findViewById(R.id.luminosite);
        son_msg = (TextView) view.findViewById(R.id.son);
        alti_msg = (TextView) view.findViewById(R.id.altitude);

        myExternfile = new File(getActivity().getExternalFilesDir("Save_Data"),"Drone_Data.xls");

        random = new Random();
        handler = new Handler();

        //========================== Affichage du graph en fonction de la donnée souhaiter ============================//

        vit_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_data();
                Graph_data("Vitesse");
            }
        });

        temp_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_data();
                Graph_data("Temperature");
            }
        });

        lum_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_data();
                Graph_data("Luminosite");
            }
        });

        son_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_data();
                Graph_data("Son");
            }
        });

        //======================== Affichage des données recue en temp réel (aléatoire) ==========//
        run = new Runnable() {

            @Override
            public void run() {

                data_save[0][cpt] = String.valueOf(cpt); // Temps

                int val = random.nextInt(500); // Vitesse
                vit_msg.setText(String.valueOf(val));
                data_save[1][cpt] = String.valueOf(val);

                int val2 = random.nextInt(500); // Temperature
                temp_msg.setText(String.valueOf(val2));
                data_save[2][cpt] = String.valueOf(val2);

                int val3 = random.nextInt(500); // Luminosite
                lum_msg.setText(String.valueOf(val3));
                data_save[3][cpt] = String.valueOf(val3);;

                int val4 = random.nextInt(500); // Son
                son_msg.setText(String.valueOf(val4));
                data_save[4][cpt] = String.valueOf(val4);

                int val5 = random.nextInt(500); // Longitude
                long_msg.setText(String.valueOf(val5));
                data_save[5][cpt] = String.valueOf(val5);

                int val6 = random.nextInt(500); // Latitude
                lati_msg.setText(String.valueOf(val6));
                data_save[6][cpt] = String.valueOf(val6);

                int val7 = random.nextInt(500); // Altitude
                alti_msg.setText(String.valueOf(val7));
                data_save[7][cpt] = String.valueOf(val7);

                cpt++;

                handler.postDelayed(this,1000);

            }
        };
        handler.post(run);

        return view;
    }

    private void Graph_data (String val) // Affichage du graph_fragment
    {
        Bundle bundle = new Bundle();
        GraphFragment graphFragment = new GraphFragment();
        graphFragment.donneeFragment = this;
        bundle.putString("data",val);
        graphFragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,graphFragment);
        fragmentTransaction.commit();
    }

    void write_data () //Ecriture de données dans un fichier excel
    {

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

        sheet.setColumnWidth(0,(20*200));
        sheet.setColumnWidth(1,(20*200));
        sheet.setColumnWidth(2,(20*200));
        sheet.setColumnWidth(3,(20*200));
        sheet.setColumnWidth(4,(20*300));
        sheet.setColumnWidth(5,(20*300));
        sheet.setColumnWidth(6,(20*300));
        sheet.setColumnWidth(7,(20*300));

        for (int i =0;i<cpt;i++)
        {
            row = sheet.createRow(i+1);

            cell = row.createCell(0);
            cell.setCellValue(data_save[0][i]);

            cell = row.createCell(1);
            cell.setCellValue(data_save[1][i]);

            cell = row.createCell(2);
            cell.setCellValue(data_save[2][i]);

            cell = row.createCell(3);
            cell.setCellValue(data_save[3][i]);

            cell = row.createCell(4);
            cell.setCellValue(data_save[4][i]);

            cell = row.createCell(5);
            cell.setCellValue(data_save[5][i]);

            cell = row.createCell(6);
            cell.setCellValue(data_save[6][i]);

            cell = row.createCell(7);
            cell.setCellValue(data_save[7][i]);
        }

        try {
            fileOutputStream = new FileOutputStream(myExternfile);
            excel_file.write(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeHandler()
    {
        handler.removeCallbacks(run);
    }

}