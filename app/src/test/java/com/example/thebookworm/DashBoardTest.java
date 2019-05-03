package com.example.thebookworm;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotEquals;

public class DashBoardTest {


    @Test
    public void loadInventory() {
        List<String> res = new DashBoard().loadInventory();

        for (String curr : res)
            System.out.println(curr);

        assertNotEquals(res.size(), 0);
    }
}