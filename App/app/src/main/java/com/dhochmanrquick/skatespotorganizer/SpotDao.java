package com.dhochmanrquick.skatespotorganizer;

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

    @Query("SELECT * from spot_table")
    List<Spot> getAll();
}
