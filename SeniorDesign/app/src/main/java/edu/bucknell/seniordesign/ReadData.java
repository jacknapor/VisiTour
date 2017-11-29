package edu.bucknell.seniordesign;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.WorkSource;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;


/**
 * ReadData is a helper class that pushes default lists to the database. A default list must be an xls file with the following fields
 * in this particular order: Name, Latitude, Longitude.
 *
 * Created by Caroline on 11/10/2017.
 */

public class ReadData extends AppCompatActivity {

    private String TAG = "Read Data";

    private DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();


    public void ReadData(Context context) {
        try {
            readXLSFile(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readXLSFile(Context context) throws IOException {

        // Change the resources file to the proper resources file
        //InputStream stream = context.getResources().openRawResource(R.raw.nat_parks);
        InputStream stream = context.getResources().openRawResource(R.raw.lewisburg_local_museums);

        // Change the name and description of this list to reflect the proper name and description
        //List list = new List("All National Parks", "A list of all National Parks");
        List list = new List("Museums Near Lewisburg", "Museums in & around Lewisburg");

        ArrayList locations = new ArrayList();

        try {

            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(stream);
            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            int numRows = sheet.getPhysicalNumberOfRows();
            Log.i(TAG, "# rows: " + numRows);
            for (int r = 1; r < numRows; r++) {
                Row row = sheet.getRow(r);

                String locName = row.getCell(0).getStringCellValue();
                double lat = row.getCell(1).getNumericCellValue();
                double lng = row.getCell(2).getNumericCellValue();

                Location loc = new Location(locName, "", new TraveListLatLng(lat, lng));
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
