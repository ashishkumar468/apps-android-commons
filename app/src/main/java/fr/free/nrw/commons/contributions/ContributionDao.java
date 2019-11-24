package fr.free.nrw.commons.contributions;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface ContributionDao {
    @Query("SELECT * FROM contributions")
    List<Contribution> getAll();

    @Query("SELECT * FROM contributions")
    LiveData<List<Contribution>> getAllLiveData();

    @Insert
    public void save(Contribution contribution);

    @Insert
    public void saveAll(List<Contribution> contribution);

    @Delete
    public void delete(Contribution contribution);

    @Query("DELETE FROM contributions")
    public void deleteAll();

    @Query("SELECT * FROM contributions WHERE file_name like :fileName")
    public List<Contribution> getContributionByFileName(String fileName);

    @Query("SELECT * FROM contributions WHERE state IN(:states)")
    public List<Contribution> getContributionsWithState(String[] states);

    @Update
    void update(List<Contribution> contributionsWithState);

    //TODO, set date created to today's date if null (write a converter)
}
