package fr.free.nrw.commons.contributions;

import androidx.lifecycle.LiveData;
import java.util.List;
import javax.inject.Inject;

/**
 * The repository class for contributions
 */
public class ContributionsRepository {

    private ContributionsLocalDataSource localDataSource;

    @Inject
    public ContributionsRepository(ContributionsLocalDataSource localDataSource) {
        this.localDataSource = localDataSource;
    }

    /**
     * Fetch default number of contributions to be shown, based on user preferences
     */
    public int get(String uploadsShowing) {
        return localDataSource.get(uploadsShowing);
    }

    /**
     * Deletes a failed upload from DB
     * @param contribution
     */
    public void deleteContributionFromDB(Contribution contribution) {
        localDataSource.deleteContribution(contribution);
    }

    public LiveData<List<Contribution>> getAll() {
        return localDataSource.getAll();
    }
}
