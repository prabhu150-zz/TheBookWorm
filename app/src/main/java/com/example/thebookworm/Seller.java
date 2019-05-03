package com.example.thebookworm;

import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class Seller {

    String userID, name, email, type;
    String TAG = "SellerClass";
    //     List<Buyer> customers;
//     List<Order> orders;
    List<Product> inventory;


    public Seller(String userID, String name, String email) {
        this.userID = userID;
        this.name = name;
        this.email = email;

    }


    public boolean loadInventory(InputStream myInput) {

        try {
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            Iterator<Row> rowIter = mySheet.rowIterator();


            Log.d(TAG, "onClick: loading products!");

            while (rowIter.hasNext()) {
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator<Cell> cellIter = myRow.cellIterator();

                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    Log.d("FileUtils", "Cell Value: " + myCell.toString() + " Index :" + myCell.getColumnIndex());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }

}
