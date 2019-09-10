package fr.free.nrw.commons.contributions;


import android.content.Context;
import androidx.lifecycle.LiveData;
import fr.free.nrw.commons.contributions.ContributionsContract.UserActionListener;
import fr.free.nrw.commons.contributions.db.ContributionsItem;
import java.util.List;
import javax.inject.Inject;

/**
 * The presenter class for Contributions
 */
public class ContributionsPresenter implements UserActionListener {

    private final ContributionsRepository repository;
    private ContributionsContract.View view;

    @Inject
    Context context;

    @Inject
    ContributionsPresenter(ContributionsRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onAttachView(ContributionsContract.View view) {
        this.view = view;
    }

    @Override
    public void onDetachView() {
        this.view = null;
    }

    /**
     * Delete a failed contribution from the local db
     * @param contribution
     */
    @Override
    public void deleteUpload(Contribution contribution) {
        repository.deleteContributionFromDB(contribution);
    }

    @Override
    public LiveData<List<ContributionsItem>> getContributions() {
        return repository.getContributions();
    }
}
