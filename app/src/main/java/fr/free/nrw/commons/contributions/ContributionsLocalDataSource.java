package fr.free.nrw.commons.contributions;

import androidx.lifecycle.LiveData;
import fr.free.nrw.commons.AppDatabase;
import fr.free.nrw.commons.contributions.db.ContributionsItem;
import fr.free.nrw.commons.kvstore.JsonKvStore;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * The LocalDataSource class for Contributions
 */
class ContributionsLocalDataSource {

    private final JsonKvStore defaultKVStore;
    private final AppDatabase appDatabase;

    @Inject
    public ContributionsLocalDataSource(
            @Named("default_preferences") JsonKvStore defaultKVStore,
            AppDatabase appDatabase) {
        this.defaultKVStore = defaultKVStore;
        this.appDatabase = appDatabase;
    }

    /**
     * Fetch default number of contributions to be show, based on user preferences
     */
    public int get(String key) {
        return defaultKVStore.getInt(key);
    }

    /**
     * Remove a contribution from the contributions table
     * @param contribution
     */
    public void deleteContribution(Contribution contribution) {
        appDatabase.contributionsDao().delete(ContributionsItem.fromContribution(contribution));
    }

    public LiveData<List<ContributionsItem>> getContributions() {
        return appDatabase.contributionsDao().getAllLiveData();
    }
}
