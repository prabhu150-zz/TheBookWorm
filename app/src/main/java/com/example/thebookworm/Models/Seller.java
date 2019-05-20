package com.example.thebookworm.Models;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Seller {

    public String getUserID() {
        return userID;
    }

    String userID, name, email;
    String profilePic = "https://firebasestorage.googleapis.com/v0/b/bookworm-cb649.appspot.com/o/profile-pics%2Fbarnes.png?alt=media&token=750c05c8-67e2-436c-ad04-79d8f1fa9e3d";
    List<Buyer> customers; // all of sellers customers
    List<Product> inventory; // list of ids of all products that current seller has
    List<Order> orders; // all of their orders


    public String getName() {
        return name;
    }


    public Seller(Seller copy) {
        this.userID = copy.userID;
        this.name = copy.name;
        this.email = copy.email;
        this.profilePic = copy.profilePic;
        this.customers = new ArrayList<>(copy.customers);
        this.inventory = new ArrayList<>(copy.inventory);

    }

    public String getEmail() {
        return email;
    }

    public Seller(String userID, String name, String email) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        customers = new ArrayList<>();
        inventory = new ArrayList<>();
    }

    public String getProfilePic() {

        return profilePic;
    }

    public Seller() {
//        inventory = new ArrayList<>();
    }

    public void addBuyer(Buyer buyer) {
        customers.add(buyer);
    }


    public void addProduct(Product product) {
        inventory.add(product);
    }


    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }


    public void loadInventory(Context currentActivity) {
        AssetManager assetManager = currentActivity.getAssets();

        try {
            InputStream myInput;
            myInput = assetManager.open("Books_amazon.xls");
            loadItemsFromFile(myInput);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void loadItemsFromFile(InputStream myInput) {

        String productType = "";
        Map<String, String> itemList = new LinkedHashMap<>();
        List<String> columns = new ArrayList<>();

        try {
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            Iterator<Row> rowIter = mySheet.rowIterator();

            HSSFRow myRow = (HSSFRow) rowIter.next();
            Iterator<Cell> cellIter = myRow.cellIterator();
            DataFormatter formatter = new DataFormatter();

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
                    itemList.put(columns.get(columnIndex++ % columns.size()), formatter.formatCellValue(myCell));
                }

                if (itemList.get("product") == null)
                    continue;

                productType = itemList.get("product").trim().toLowerCase().replaceAll("[^\\w\\s]", "");

                switch (productType) {
                    case "book":
                        Product currentProduct = new Book(itemList.get("title"), itemList.get("description"), itemList.get("book cover"), Double.parseDouble(itemList.get("price")), itemList.get("isbn") + userID, (int) Double.parseDouble(itemList.get("quantity")), name, productType, userID);


                        ((Book) currentProduct).setDetails(itemList.get("author"), itemList.get("genre"), itemList.get("publisher"), (int) Double.parseDouble(itemList.get("pages")), itemList.get("date published"));
                        inventory.add(currentProduct);
                        break;

                    // can add new products to e commerce stores. Wont be limited to only books in the future

                    default:
                        throw new IllegalArgumentException("This product " + productType + " is currently unsupported!");
                }

                itemList.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateMarketProductList(productType);
    }

    private void logit(String message) {
        Log.d("SellerClass", message);
    }


    private void updateMarketProductList(String productType) {

        DatabaseReference marketRef = FirebaseDatabase.getInstance().getReference("/market/");

        final DatabaseReference inventoryRef = FirebaseDatabase.getInstance().getReference("/users/sellers/" + userID + "/inventory/" + productType);


        final Queue<String> imageUrls = new LinkedList<String>();

        /*
        I am keeping market separate from buyer and seller to accommodate product/user deletions in the future. If a buyer or seller decides to delete their account its easier to remove them from the market rather than individually removing them from buyer and seller tables respectively.
         */

        marketRef.child("/users/sellers/").child(userID).setValue(userID).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                logit("onComplete: User added: " + name);
            }
        });

        DatabaseReference productsRef = marketRef.child("/products/" + productType);


        productsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // if any new products are added in the market then the corresponding inventory of that seller
                // should be updated!

                Product newProduct = dataSnapshot.getValue(Book.class);
                String path = dataSnapshot.child("pid").getValue().toString();
                inventoryRef.child(path).setValue(newProduct);
                if (imageUrls.size() > 0)
                inventoryRef.child(path + "/imageURL").setValue(imageUrls.remove());
                inventoryRef.child(path + "/pid").setValue(path);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                // if a product is removed from inventory only then take it off market for that seller.

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                logit("On products cancelled error: " + databaseError.getMessage());
            }
        });

        for (Product currentProduct : inventory) {
            productsRef.child(currentProduct.getPID()).setValue(currentProduct);
            imageUrls.add(currentProduct.getImageURL());
        }

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
    }

    public void removeFromInventory(String currentProductPID) {

        int index = 0;

        for (Product curr : inventory) {
            if (curr.getPID().equals(currentProductPID))
                break;
            index++;
        }

        if (index < inventory.size())
            inventory.remove(index);

    }
}
