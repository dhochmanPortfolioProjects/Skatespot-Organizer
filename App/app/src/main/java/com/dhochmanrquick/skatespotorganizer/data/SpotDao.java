package com.dhochmanrquick.skatespotorganizer.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Room;

import java.util.List;

/**
 * A Data Access Object (DAO) (a mapping of SQL queries to POJO methods) for the Spot SQLite table.
 * The DAO must be an interface or abstract class; this is an interface, not a Java class.
 * By default, all queries must be executed on a separate thread.
 *
 * (You used to have to define these painstakingly in your SQLiteOpenHelper class. When you use a DAO,
 * you call the methods, and Room takes care of the rest).
 *
 *
 * @author Daniel Hochman
 * @author Rob Quick
 */
@Dao
public interface SpotDao {
    // The compiler checks the SQL and generates queries from convenience annotations for common queries,
    // such as @Insert (there are also @Delete and @Update annotations for deleting and updating a row).
    //
    // Tip: When inserting data, you can provide a conflict strategy. The default SQL behavior is ABORT,
    // so that you cannot insert two items with the same primary key into the database.
    //
    //If the table has more than one column, you can use
    //
    //@Insert(onConflict = OnConflictStrategy.REPLACE)
    //
    //to replace a row.
    //
    // When you create a DAO method and annotate it with @Insert, Room generates an implementation
    // that inserts all parameters into the database in a single transaction.

    @Insert
    void insert(Spot spot);

    // When this query is processed at compile time, Room matches the :id bind parameter with the
    // id method parameter.
    // Room allows you to return any Java-based object (POJO) from your queries as long as the set of
    // result columns can be mapped into the returned object.
    @Query("SELECT * FROM spot_table WHERE id = :id")
    LiveData<Spot> getSpot(int id);

    @Delete
    public void deleteSpots(Spot... spots);

    /**
     * Overloaded method to get spot by name.
     *
     * @param name  The Spot name
     * @return      The Spot found with a matching name
     */
    @Query("SELECT * FROM spot_table WHERE name = :name")
    LiveData<Spot> getSpot(String name);

    @Query("DELETE FROM spot_table")
    void deleteAll();

    // Use a return value of type LiveData in your method description, and Room generates all
    // necessary code to update the LiveData when the database is updated.
    @Query("SELECT * FROM spot_table")
    LiveData<List<Spot>> getAll();
}