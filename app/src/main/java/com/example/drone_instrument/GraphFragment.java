package com.example.drone_instrument;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

public class GraphFragment extends Fragment {

    String message, message2;
    TextView msg;
    int cpt;

    GraphView graphView;
    String[][] data_tab = new String[5][14400];



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        msg = (TextView) view.findViewById(R.id.msg_graph);
        graphView = (GraphView) view.findViewById(R.id.graph);
        graphView.getViewport().setScalable(true);

        //================================== Reception du message de donnee fragment ======================================================//
        Bundle bundle = getArguments();
        message = bundle.getString("data");
        message2 = bundle.getString("data2");
        cpt = Integer.parseInt(message2);

        //================================================== Lecture des données enregistré ===============================================//

        read_data();

        //============================================ Affichage du graphe ================================================================//

        switch (message)
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
        return view;
    }

    void creation_graph(String title, int color, int data) // Creation d'un graphique
    {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

        for (int i=0;i<cpt;i++)
        {
            series.appendData(new DataPoint(i, Double.parseDouble(data_tab[data][i])),true,cpt+1);
        }

        graphView.addSeries(series);

        series.setColor(color);
        graphView.setTitle(title);
        graphView.setTitleTextSize(90);
        series.setDrawDataPoints(true);


    }

    private void read_data()
    {
        File file = new File(getActivity().getExternalFilesDir("Save_Data"),"Drone_Data.xls");
        FileInputStream fileInputStream= null;
        Workbook workbook;

        try {

            fileInputStream = new FileInputStream(file);
            workbook = new HSSFWorkbook(fileInputStream);
            Sheet sheet;
            sheet = workbook.getSheetAt(0);

            for (int i =0;i<cpt;i++)
            {
                Row row = sheet.getRow(i+1);

                Cell cell_vitesse = row.getCell(1);
                data_tab[1][i] = cell_vitesse.getStringCellValue();

                Cell cell_Temperature = row.getCell(2);
                data_tab[2][i] = cell_Temperature.getStringCellValue();

                Cell cell_Luminosite = row.getCell(3);
                data_tab[3][i] = cell_Luminosite.getStringCellValue();

                Cell cell_Son = row.getCell(4);
                data_tab[4][i] = cell_Son.getStringCellValue();
            }

            //msg.setText(data_tab[1][19]);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"error",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"error",Toast.LENGTH_SHORT).show();
        }

    }


}