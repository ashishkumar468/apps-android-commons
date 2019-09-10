package fr.free.nrw.commons.contributions;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;

import fr.free.nrw.commons.AppDatabase;
import fr.free.nrw.commons.contributions.db.ContributionsItem;
import fr.free.nrw.commons.mwapi.LogEventResult.LogEvent;
import org.wikipedia.util.DateUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import fr.free.nrw.commons.Utils;
import fr.free.nrw.commons.di.ApplicationlessInjection;
import fr.free.nrw.commons.kvstore.JsonKvStore;
import fr.free.nrw.commons.mwapi.LogEventResult;
import fr.free.nrw.commons.mwapi.MediaWikiApi;
import timber.log.Timber;

import static fr.free.nrw.commons.contributions.Contribution.STATE_COMPLETED;

@SuppressWarnings("WeakerAccess")
public class ContributionsSyncAdapter extends AbstractThreadedSyncAdapter {

    // Arbitrary limit to cap the number of contributions to ever load. This is a maximum built
    // into the app, rather than the user's setting. Also see Github issue #52.
    public static final int ABSOLUTE_CONTRIBUTIONS_LOAD_LIMIT = 500;

    @SuppressWarnings("WeakerAccess")
    @Inject MediaWikiApi mwApi;
    @Inject
    @Named("default_preferences")
    JsonKvStore defaultKvStore;

    @Inject
    AppDatabase appDatabase;

    public ContributionsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String authority,
                              ContentProviderClient contentProviderClient, SyncResult syncResult) {
        ApplicationlessInjection
                .getInstance(getContext()
                        .getApplicationContext())
                .getCommonsApplicationComponent()
                .inject(this);
        // This code is fraught with possibilities of race conditions, but lalalalala I can't hear you!
        String user = account.name;
        String lastModified = defaultKvStore.getString("lastSyncTimestamp", "");
        Date curTime = new Date();
        LogEventResult result;
        Boolean done = false;
        String queryContinue = null;
        while (!done) {
            try {
                result = mwApi.logEvents(user, "", queryContinue, ABSOLUTE_CONTRIBUTIONS_LOAD_LIMIT);
            } catch (IOException e) {
                // There isn't really much we can do, eh?
                // FIXME: Perhaps add EventLogging?
                syncResult.stats.numIoExceptions += 1; // Not sure if this does anything. Shitty docs
                Timber.d("Syncing failed due to %s", e);
                return;
            }
            Timber.d("Last modified at %s", lastModified);

            List<LogEvent> logEvents = result.getLogEvents();
            Timber.d("%d results!", logEvents.size());
            ArrayList<ContentValues> imageValues = new ArrayList<>();
            List<ContributionsItem> contributionsItems=new ArrayList<>();
            for (LogEvent image : logEvents) {
                if (image.isDeleted()) {
                    // means that this upload was deleted.
                    continue;
                }
                String filename = image.getFilename();
                Date dateUpdated = image.getDateUpdated();
                Contribution contribution = new Contribution(null, null, filename,
                        "", -1, dateUpdated, dateUpdated, user,
                        "", "");
                contribution.setState(STATE_COMPLETED);
                contributionsItems.add(ContributionsItem.fromContribution(contribution));
            }

            appDatabase.contributionsDao().insertAll(contributionsItems);
            queryContinue = result.getQueryContinue();
            if (TextUtils.isEmpty(queryContinue)) {
                done = true;
            }
        }
        defaultKvStore.putString("lastSyncTimestamp", DateUtil.iso8601DateFormat(curTime));
        Timber.d("Oh hai, everyone! Look, a kitty!");
    }
}
