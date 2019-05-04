package com.example.thebookworm;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
                String currentColumn = myCell.toString().trim().toLowerCase().replaceAll("[^\\w\\s]", "");

                itemList.put(currentColumn, "");
                columns.add(currentColumn);
            }

            int columnIndex = 0;

            logit("onClick: loading products!");

            while (rowIter.hasNext()) {
                myRow = (HSSFRow) rowIter.next();
                cellIter = myRow.cellIterator();

                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    itemList.put(columns.get(columnIndex++ % columns.size()), myCell.toString().replaceAll("[^-\\w\\s]", ""));
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
        updateVendorStock();

    }


    private void logit(String message) {
        Log.d("SellerClass", message);
    }

    private void updateVendorStock() {
        DatabaseReference sellerRef = FirebaseDatabase.getInstance().getReference("/users/sellers/").child(userID).child("/inventory/");

        logit("Adding " + inventory.size() + " vendor products to market!");
        for (Product currentProduct : inventory) {

            sellerRef.child(currentProduct.getPID()).setValue(currentProduct.getPID()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    logit("onComplete: Another product added to Seller's acc");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    logit("Couldnt add products. Reason: " + e.getMessage());
                }
            });
        }

        updateMarketProductList();
    }

    private void updateMarketProductList() {
        DatabaseReference marketRef = FirebaseDatabase.getInstance().getReference("/market/");

        /*
        I am keeping market separate from buyer and seller to accommodate product/user deletions in the future. If a buyer or seller decides to delete their account its easier to remove them from the market rather than individually removing them from buyer and seller tables respectively.
         */

        marketRef.child("/users/sellers/").child(userID).setValue(userID).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                logit("onComplete: Another User added to Seller's acc");
            }
        });


        marketRef = marketRef.child("/products/");

        for (Product currentProduct : inventory) {
            marketRef.child(currentProduct.getPID()).setValue(currentProduct);
        }


    }


}
