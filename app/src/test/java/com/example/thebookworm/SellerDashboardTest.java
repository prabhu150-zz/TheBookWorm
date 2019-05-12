package com.example.thebookworm;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotEquals;

public class SellerDashboardTest {


    @Test
    public void loadInventory() {
        List<String> res = new SellerDashboard().loadInventory();

        for (String curr : res)
            System.out.println(curr);

        assertNotEquals(res.size(), 0);
    }
}