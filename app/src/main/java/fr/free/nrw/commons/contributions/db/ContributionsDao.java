package fr.free.nrw.commons.contributions.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ContributionsDao {

    @Query("SELECT * FROM contributions")
    List<ContributionsItem> getAll();

    @Insert
    void insertAll(List<ContributionsItem> contributionsItems);

    @Delete
    void delete(ContributionsItem contributionsItem);

    @Query("SELECT * FROM contributions")
    LiveData<List<ContributionsItem>> getAllLiveData();
}