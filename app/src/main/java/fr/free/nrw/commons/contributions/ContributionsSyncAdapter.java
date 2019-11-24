package fr.free.nrw.commons.contributions;

import static fr.free.nrw.commons.contributions.Contribution.STATE_COMPLETED;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import fr.free.nrw.commons.db.AppDatabase;
import fr.free.nrw.commons.di.ApplicationlessInjection;
import fr.free.nrw.commons.mwapi.UserClient;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

@SuppressWarnings("WeakerAccess")
public class ContributionsSyncAdapter extends AbstractThreadedSyncAdapter {

    @Inject
    UserClient userClient;

    @Inject
    AppDatabase appDatabase;
    private ContributionDao contributionDao;

    public ContributionsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        ApplicationlessInjection
                .getInstance(getContext()
                        .getApplicationContext())
                .getCommonsApplicationComponent()
                .inject(this);
        contributionDao=appDatabase.contributionDao();
    }

    private boolean fileExists(String filename) {
        if (filename == null) {
            return false;
        }
        return contributionDao.getContributionByFileName(filename).size() != 0;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onPerformSync(Account account, Bundle bundle, String authority,
                              ContentProviderClient contentProviderClient, SyncResult syncResult) {
        String user = account.name;
        List<Contribution> contributionList=new ArrayList();
        userClient.logEvents(user)
                .doOnNext(mwQueryLogEvent->Timber.d("Received image %s", mwQueryLogEvent.title() ))
                .filter(mwQueryLogEvent -> !mwQueryLogEvent.isDeleted())
                .filter(mwQueryLogEvent -> !fileExists(mwQueryLogEvent.title()))
                .doOnNext(mwQueryLogEvent->Timber.d("Image %s passed filters", mwQueryLogEvent.title() ))
                .map(image -> new Contribution(null, null, image.title(),
                        "", -1, image.date(), image.date(), user,
                        "", "", STATE_COMPLETED))
                .doOnNext(contribution -> contributionList.add(contribution))
                .buffer(20)//Actually db can handle 100's, this is TBD
                .subscribe(contributions->contributionDao.saveAll(contributions));
        Timber.d("Oh hai, everyone! Look, a kitty!");
    }
}
