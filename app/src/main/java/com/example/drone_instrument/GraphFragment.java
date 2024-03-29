package com.example.drone_instrument;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GraphFragment extends Fragment {

    String message;
    int nb_ligne;
    ImageButton bp_refresh;

    GraphView graphView;
    String[][] data = new String[5][14400];
    LineGraphSeries<DataPoint> series;
    DonneeFragment donneeFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_graph, container, false);
        bp_refresh = (ImageButton) view.findViewById(R.id.button_refresh_graph);
        graphView = (GraphView) view.findViewById(R.id.graph);
        graphView.getViewport().setScalable(true);

        // Reception du message de DonneFragment
        Bundle bundle = getArguments();
        message = bundle.getString("data");

        read_data(); // Lecture des données enregistrée dans la base de données

        data_graph(); // Affichage du graph

        // ================= Actualisation de l'affichage du graphique ========================== //

        bp_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                donneeFragment.write_data(); // Ecriture des données dans la base de données Excel
                read_data(); // Lecture des données enregistrée dans la base de données
                data_graph(); // Affichage du graph
                Toast.makeText(getContext(), "Données actualisées", Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }

    void creation_graph(String title, int color, int type_data) // Creation d'un graphique type_data : permet de choisir la colonne de données a afichier
    {
        series = new LineGraphSeries<>();

        for (int i=0;i<nb_ligne;i++) // Ajout de points sur le graphique
        {
            series.appendData(new DataPoint(Double.parseDouble(data[0][i]), Double.parseDouble(data[type_data][i])),true,nb_ligne); // creation d'un point avec des cordonnées x et y
        }

        graphView.addSeries(series);

        series.setColor(color); // Couleur de la courbe
        graphView.setTitle(title); // Titre du graphique
        graphView.setTitleTextSize(90); // Taille du titre
        series.setDrawDataPoints(true); // Affichage des points qui composent la courbe

    }

    private void read_data()
    {
        File file = new File(getActivity().getExternalFilesDir("Save_Data"),"Drone_Data.xls"); // Chemin de la base de données Excel dans le stockage interne du téléphone
        FileInputStream fileInputStream= null;
        Workbook workbook;

        try {

            fileInputStream = new FileInputStream(file);
            workbook = new HSSFWorkbook(fileInputStream);
            Sheet sheet;
            sheet = workbook.getSheetAt(0);
            nb_ligne = sheet.getLastRowNum();

            for (int i =0;i<nb_ligne;i++) // Lecture du contenue du fichier Excel et stockage dans un tableau
            {
                Row row = sheet.getRow(i+1); // Le numéro de ligne du tableau Excel

                Cell cell_time = row.getCell(0); // Le numéro de colonne tableau Excel
                data[0][i] = cell_time.getStringCellValue(); // Recupération du contenu

                Cell cell_vitesse = row.getCell(1);
                data[1][i] = cell_vitesse.getStringCellValue();

                Cell cell_Temperature = row.getCell(2);
                data[2][i] = cell_Temperature.getStringCellValue();

                Cell cell_Luminosite = row.getCell(3);
                data[3][i] = cell_Luminosite.getStringCellValue();

                Cell cell_Son = row.getCell(4);
                data[4][i] = cell_Son.getStringCellValue();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"error",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"error",Toast.LENGTH_SHORT).show();
        }

    }

    private void data_graph()
    {
        switch (message) // Affichage du graphique en fonction de la donnée demander
        {
            case "Vitesse":
                creation_graph(message,Color.YELLOW,1);
                break;

            case "Temperature":
                creation_graph(message,Color.BLUE,2);
                break;

            case "Luminosite":
                creation_graph(message,Color.CYAN,3);
                break;

            case "Son":
                creation_graph(message,Color.RED,4);
                break;
        }

    }

}

