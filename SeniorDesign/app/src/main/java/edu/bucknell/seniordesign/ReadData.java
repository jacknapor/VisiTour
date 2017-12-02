package edu.bucknell.seniordesign;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;


/**
 * ReadData.java
 * TraveList - Senior Design
 *
 * ReadData is a helper class that pushes default lists to the database. A default list must be an xls file with the following fields
 * in this particular order: Name, Latitude, Longitude.
 *
 * NOTE: Requires Excel sheets to be .xls (1997 - 2003) rather than .xlsx.
 *
 * Created by Caroline on 11/10/2017.
 */

public class ReadData extends AppCompatActivity {

    // Database reference
    private DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();

    // Constructor given a Context
    public void ReadData(Context context) {
        try {
            readXLSFile(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Read an XLS File
    public void readXLSFile(Context context) throws IOException {

        // Change the resources file to the proper resources file
        //InputStream stream = context.getResources().openRawResource(R.raw.lewisburg_museums_images);
        InputStream stream = context.getResources().openRawResource(R.raw.nat_parks_images);

        // Change the name and description of this list to reflect the proper name and description
        //List list = new List("All National Parks", "A list of all National Parks");
        List list = new List("Museums Near Lewisburg", "Museums in & around Lewisburg");

        ArrayList locations = new ArrayList();

        try {
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(stream);
            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            // Change the number of rows to reflect the proper list size
            //int numRows = 60; // For National Parks
            int numRows = 12; // For Lewisburg Museums
            for (int r = 1; r < numRows; r++) {
                Row row = sheet.getRow(r);

                String locName = row.getCell(0).getStringCellValue();
                double lat = row.getCell(1).getNumericCellValue();
                double lng = row.getCell(2).getNumericCellValue();
                String imageUrl = row.getCell(3).getStringCellValue();

                Location loc = new Location(locName, "", new TraveListLatLng(lat, lng), imageUrl);
                loc.setVisited(false);

                locations.add(loc);
            }

            list.setLocationArray(locations);
            mDb.child("DefaultLists").child(list.getListName()).setValue(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
