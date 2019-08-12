package fr.free.nrw.commons;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import fr.free.nrw.commons.contributions.db.ContributionsDao;
import fr.free.nrw.commons.contributions.db.ContributionsItem;

/**
 * The database class referred by room to construct the database
 */
@Database(entities = {ContributionsItem.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ContributionsDao contributionsDao();
}