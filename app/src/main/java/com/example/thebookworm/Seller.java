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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Seller {

    String userID, name, email, profilePic = "https://firebasestorage.googleapis.com/v0/b/bookworm-cb649.appspot.com/o/profile-pics%2Fbarnes.png?alt=media&token=750c05c8-67e2-436c-ad04-79d8f1fa9e3d";
    String TAG = "SellerClass";
    //     List<Buyer> customers;
//  //   List<Order> orders;
    List<Product> inventory;


    public Seller(String userID, String name, String email) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        inventory = new ArrayList<>();
    }

    public Seller() {
        inventory = new ArrayList<>();
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public void loadInventory(InputStream myInput) {

        Map<String, String> itemList = new LinkedHashMap<>();
        List<String> columns = new ArrayList<>();

        try {
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            Iterator<Row> rowIter = mySheet.rowIterator();

            HSSFRow myRow = (HSSFRow) rowIter.next();
            Iterator<Cell> cellIter = myRow.cellIterator();

            while (cellIter.hasNext()) {
                HSSFCell myCell = (HSSFCell) cellIter.next();
                String currentColumn = myCell.toString().trim().toLowerCase();
                itemList.put(currentColumn, "");
                columns.add(currentColumn);
            }

            int columnIndex = 0;

            Log.d(TAG, "onClick: loading products!");

            while (rowIter.hasNext()) {
                myRow = (HSSFRow) rowIter.next();
                cellIter = myRow.cellIterator();

                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
//                    String currentColumn = columns.get(columnIndex++ % columns.size());
//                    List<String> temp = itemList.get(currentColumn);
//                    temp.add(myCell.toString());
                    itemList.put(columns.get(columnIndex++ % columns.size()), myCell.toString());
                }

                String productType = itemList.get("product");

                switch (productType) {
                    case "Book":
                        Product currentProduct = new Book(itemList.get("title"), itemList.get("description"), "", Double.parseDouble(itemList.get("price")), itemList.get("isbn"), (int) Double.parseDouble(itemList.get("quantity")));

                        ((Book) currentProduct).setDetails(itemList.get("author"), itemList.get("genre"), itemList.get("publisher"), (int) Double.parseDouble(itemList.get("pages")), itemList.get("date published"));
                        inventory.add(currentProduct);
                        break;

                    // can add new products to e commerce stores. Wont be limited to only books in the future

                }


                itemList.clear();


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
