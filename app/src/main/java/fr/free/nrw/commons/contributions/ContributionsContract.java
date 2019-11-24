package fr.free.nrw.commons.contributions;

import fr.free.nrw.commons.BasePresenter;

/**
 * The contract for Contributions View & Presenter
 */
public class ContributionsContract {

    public interface View {

        void showWelcomeTip(boolean numberOfUploads);

        void showProgress(boolean shouldShow);

        void showNoContributionsUI(boolean shouldShow);

        void setUploadCount(int count);

        void onDataSetChanged();
    }

    public interface UserActionListener extends BasePresenter<ContributionsContract.View> {

        void deleteUpload(Contribution contribution);
    }
}
