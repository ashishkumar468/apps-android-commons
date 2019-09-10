package fr.free.nrw.commons.contributions;

import android.database.Cursor;
import androidx.lifecycle.LiveData;
import androidx.loader.app.LoaderManager;
import fr.free.nrw.commons.BasePresenter;
import fr.free.nrw.commons.Media;
import fr.free.nrw.commons.contributions.db.ContributionsItem;
import java.util.List;

/**
 * The contract for Contributions View & Presenter
 */
public class ContributionsContract {

    public interface View {

        void showWelcomeTip(boolean numberOfUploads);

        void showProgress(boolean shouldShow);

        void showNoContributionsUI(boolean shouldShow);

        void setUploadCount(int count);

    }

    public interface UserActionListener extends BasePresenter<ContributionsContract.View>{

        void deleteUpload(Contribution contribution);

        LiveData<List<ContributionsItem>> getContributions();
    }
}
