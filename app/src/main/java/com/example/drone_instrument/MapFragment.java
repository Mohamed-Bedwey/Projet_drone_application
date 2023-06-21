package com.example.drone_instrument;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MapFragment extends Fragment {

    String[][] data_tab = new String[7][14400];
    int nb_ligne;
    SupportMapFragment supportMapFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        read_data();

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {

                        MarkerOptions markerOptions = new MarkerOptions();

                        markerOptions.position(latLng).title(" V: "+data_tab[1][0]+" Te: "+data_tab[1][1]+" Lu: "+data_tab[1][2]+" Son: "+data_tab[1][3]+ " Alt: "+data_tab[1][6]);

                        googleMap.clear();

                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,5));

                        googleMap.addMarker(markerOptions);

                    }
                });
            }
        });

        return view;
    }

    private void read_data() {

        File file = new File(getActivity().getExternalFilesDir("Save_Data"),"Drone_Data.xls");
        FileInputStream fileInputStream= null;
        Workbook workbook;

        try {

            fileInputStream = new FileInputStream(file);
            workbook = new HSSFWorkbook(fileInputStream);
            Sheet sheet;
            sheet = workbook.getSheetAt(0);
            nb_ligne = sheet.getLastRowNum();

            for (int i =0;i<nb_ligne;i++)
            {
                Row row = sheet.getRow(i+1);

                Cell cell_vitesse = row.getCell(1);
                data_tab[0][i] = cell_vitesse.getStringCellValue();

                Cell cell_Temperature = row.getCell(2);
                data_tab[1][i] = cell_Temperature.getStringCellValue();

                Cell cell_Luminosite = row.getCell(3);
                data_tab[2][i] = cell_Luminosite.getStringCellValue();

                Cell cell_Son = row.getCell(4);
                data_tab[3][i] = cell_Son.getStringCellValue();

                Cell cell_Longitude = row.getCell(5);
                data_tab[4][i] = cell_Longitude.getStringCellValue();

                Cell cell_Latitude = row.getCell(6);
                data_tab[5][i] = cell_Latitude.getStringCellValue();

                Cell cell_Altitude = row.getCell(7);
                data_tab[6][i] = cell_Altitude.getStringCellValue();

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"error",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"error",Toast.LENGTH_SHORT).show();
        }
    }

}