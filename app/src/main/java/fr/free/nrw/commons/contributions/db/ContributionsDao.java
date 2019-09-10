package fr.free.nrw.commons.contributions.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ContributionsDao {

    @Query("SELECT * FROM contributions")
    List<ContributionsItem> getAll();

    @Query("SELECT * FROM contributions Where state IN(:uploadStates)")
    List<ContributionsItem> getAllWithState(List<Integer> uploadStates);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ContributionsItem> contributionsItems);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ContributionsItem contributionsItem);

    @Delete
    void delete(ContributionsItem contributionsItem);

    @Query("SELECT * FROM contributions")
    LiveData<List<ContributionsItem>> getAllLiveData();
}