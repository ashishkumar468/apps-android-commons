package fr.free.nrw.commons.contributions;

import static fr.free.nrw.commons.contributions.Contribution.STATE_COMPLETED;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import fr.free.nrw.commons.auth.SessionManager;
import fr.free.nrw.commons.db.AppDatabase;
import fr.free.nrw.commons.di.ApplicationlessInjection;
import fr.free.nrw.commons.mwapi.UserClient;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class ContributionsWorker extends Worker {

    @Inject
    UserClient userClient;

    @Inject
    AppDatabase appDatabase;

    @Inject
    SessionManager sessionManager;
    private ContributionDao contributionDao;

    public ContributionsWorker(@NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        ApplicationlessInjection.getInstance(context.getApplicationContext())
                .getCommonsApplicationComponent().inject(this);
        contributionDao = appDatabase.contributionDao();
    }

    @NonNull
    @Override
    public Result doWork() {
        performSync();
        return Result.success();
    }

    private boolean fileExists(String filename) {
        if (filename == null) {
            return false;
        }
        return contributionDao.getContributionByFileName(filename).size() != 0;
    }


    public void performSync() {
        String user = sessionManager.getCurrentAccount().name;
        List<Contribution> contributionList = new ArrayList();
        userClient.logEvents(user)
                .doOnNext(mwQueryLogEvent -> Timber.d("Received image %s", mwQueryLogEvent.title()))
                .filter(mwQueryLogEvent -> !mwQueryLogEvent.isDeleted())
                .filter(mwQueryLogEvent -> !fileExists(mwQueryLogEvent.title()))
                .doOnNext(mwQueryLogEvent -> Timber
                        .d("Image %s passed filters", mwQueryLogEvent.title()))
                .map(image -> new Contribution(null, null, image.title(),
                        "", -1, image.date(), image.date(), user,
                        "", "", STATE_COMPLETED))
                .doOnNext(contribution -> contributionList.add(contribution))
                .buffer(20)//Actually db can handle 100's, this is TBD
                .subscribe(contributions -> contributionDao.saveAll(contributions));
        Timber.d("Oh hai, everyone! Look, a kitty!");
    }
}
