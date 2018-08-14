package com.dhochmanrquick.skatespotorganizer.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface SpotDao {
    // The compiler checks the SQL and generates queries from convenience annotations for common queries, such as @Insert.
    @Insert
    void insert(Spot spot);

    @Query("DELETE FROM spot_table")
    void deleteAll();

    // Use a return value of type LiveData in your method description, and Room generates all
    // necessary code to update the LiveData when the database is updated.
    @Query("SELECT * from spot_table")
    LiveData<List<Spot>> getAll();
}
