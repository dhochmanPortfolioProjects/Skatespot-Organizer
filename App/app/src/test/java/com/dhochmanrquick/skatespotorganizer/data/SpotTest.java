package com.dhochmanrquick.skatespotorganizer.data;

import com.google.android.gms.maps.model.LatLng;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class SpotTest {

    Spot pajuLedge_Spot;

    @Before
    public void setUp() throws Exception {
        pajuLedge_Spot = new Spot(
                "Paju Ledge Spot",
                new LatLng(37.707672, 126.747231),
//                126.747231,
//                37.707672,
//                Spot.Type.LEDGE,
                "3 perfect marble ledges in a row. Nice flat ground.");
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Tests the mName field for the empty string case.
     * @result  This should throw an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorTest1(){
        Spot spot = new Spot("", new LatLng(38.707672, 127.747231),
                "3 perfect marble ledges in a row. Nice flat ground.");
    }

    @Test
    public void getId() {
    }

    @Test
    public void getName() {
        assertEquals("Paju Ledge Spot", pajuLedge_Spot.getName());
    }

    @Test
    public void getLongitude() {
    }

    @Test
    public void getLatitude() {
    }

    @Test
    public void getLatLng() {
    }

    @Test
    public void getDescription() {
    }

    @Test
    public void setId() {
    }

    @Test
    public void setName() {
    }

    @Test
    public void setLongitude() {
    }

    @Test
    public void setLatitude() {
    }

    @Test
    public void setLatLng() {
    }

    @Test
    public void setDescription() {
    }
}