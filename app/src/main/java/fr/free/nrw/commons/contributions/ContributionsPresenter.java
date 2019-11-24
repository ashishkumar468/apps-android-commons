package fr.free.nrw.commons.contributions;

import android.content.Context;
import fr.free.nrw.commons.contributions.ContributionsContract.UserActionListener;
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

    //TODO, commenting this to refer UI States
    /*@NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        int preferredNumberOfUploads = repository.get(UPLOADS_SHOWING);
        return new CursorLoader(context, BASE_URI,
                ALL_FIELDS, "", null,
                ContributionDao.CONTRIBUTION_SORT + "LIMIT "
                        + (preferredNumberOfUploads>0?preferredNumberOfUploads:100));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        view.showProgress(false);
        if (null != cursor && cursor.getCount() > 0) {
            view.showWelcomeTip(false);
            view.showNoContributionsUI(false);
            view.setUploadCount(cursor.getCount());
        } else {
            view.showWelcomeTip(true);
            view.showNoContributionsUI(true);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        //On LoadFinished is not guaranteed to be called
        view.showProgress(false);
        view.showWelcomeTip(true);
        view.showNoContributionsUI(true);
    }*/

    /**
     * Delete a failed contribution from the local db
     * @param contribution
     */
    @Override
    public void deleteUpload(Contribution contribution) {
        repository.deleteContributionFromDB(contribution);
    }
}
