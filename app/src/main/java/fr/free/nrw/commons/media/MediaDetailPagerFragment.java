package fr.free.nrw.commons.media;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Context.DOWNLOAD_SERVICE;
import static fr.free.nrw.commons.Utils.handleWebUrl;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import fr.free.nrw.commons.Media;
import fr.free.nrw.commons.R;
import fr.free.nrw.commons.auth.SessionManager;
import fr.free.nrw.commons.bookmarks.Bookmark;
import fr.free.nrw.commons.bookmarks.pictures.BookmarkPicturesContentProvider;
import fr.free.nrw.commons.bookmarks.pictures.BookmarkPicturesDao;
import fr.free.nrw.commons.category.CategoryDetailsActivity;
import fr.free.nrw.commons.category.CategoryImagesActivity;
import fr.free.nrw.commons.contributions.Contribution;
import fr.free.nrw.commons.contributions.ContributionsViewModel;
import fr.free.nrw.commons.di.CommonsDaggerSupportFragment;
import fr.free.nrw.commons.explore.SearchActivity;
import fr.free.nrw.commons.explore.categories.ExploreActivity;
import fr.free.nrw.commons.kvstore.JsonKvStore;
import fr.free.nrw.commons.utils.ImageUtils;
import fr.free.nrw.commons.utils.NetworkUtils;
import fr.free.nrw.commons.utils.PermissionUtils;
import fr.free.nrw.commons.utils.ViewUtil;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

public class MediaDetailPagerFragment extends CommonsDaggerSupportFragment implements ViewPager.OnPageChangeListener {

    @Inject SessionManager sessionManager;
    @Inject @Named("default_preferences") JsonKvStore store;
    @Inject BookmarkPicturesDao bookmarkDao;

    @BindView(R.id.mediaDetailsPager) ViewPager pager;
    private Boolean editable;
    private boolean isFeaturedImage;
    MediaDetailAdapter adapter;
    private Bookmark bookmark;
    private ContributionsViewModel contributionsViewModel;
    private List<Contribution> contributions=new ArrayList<>();

    public MediaDetailPagerFragment() {
        this(false, false);
    }

    @SuppressLint("ValidFragment")
    public MediaDetailPagerFragment(Boolean editable, boolean isFeaturedImage) {
        this.editable = editable;
        this.isFeaturedImage = isFeaturedImage;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_detail_pager, container, false);
        ButterKnife.bind(this,view);
        pager.addOnPageChangeListener(this);
        initContributionsViewModel();
        adapter = new MediaDetailAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);

        if (savedInstanceState != null) {
            final int pageNumber = savedInstanceState.getInt("current-page");
            // Adapter doesn't seem to be loading immediately.
            // Dear God, please forgive us for our sins
            view.postDelayed(() -> {
                pager.setAdapter(adapter);
                pager.setCurrentItem(pageNumber, false);

                if (getActivity() == null) {
                    Timber.d("Returning as activity is destroyed!");
                    return;
                }

            }, 100);
        } else {
            pager.setAdapter(adapter);
        }
        return view;
    }

    private void initContributionsViewModel() {
        contributionsViewModel = ViewModelProviders.of(getActivity())
                .get(ContributionsViewModel.class);
        contributionsViewModel.getAllContributions().observe(this,
                contributions -> {
                    MediaDetailPagerFragment.this.contributions = contributions;
                    if (contributions.size() > 0) {
                        adapter.notifyDataSetChanged();
                    }
                });

        contributionsViewModel.getCurrentItemPositionLiveData().observe(this,
                integer -> {
                    if (integer != -1 && pager.getAdapter().getCount() > integer) {
                        pager.setCurrentItem(integer, false);
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("editable", editable);
        outState.putBoolean("isFeaturedImage", isFeaturedImage);
        contributionsViewModel.setCurrentItemPosition(pager.getCurrentItem());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            editable = savedInstanceState.getBoolean("editable");
            isFeaturedImage = savedInstanceState.getBoolean("isFeaturedImage");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getActivity() == null) {
            Timber.d("Returning as activity is destroyed!");
            return true;
        }

        Media m = contributions.get(pager.getCurrentItem());
        switch (item.getItemId()) {
            case R.id.menu_bookmark_current_image:
                bookmarkDao.updateBookmark(bookmark);
                updateBookmarkState(item);
                return true;
            case R.id.menu_share_current_image:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, m.getDisplayTitle() + " \n" + m.getPageTitle().getCanonicalUri());
                startActivity(Intent.createChooser(shareIntent, "Share image via..."));
                return true;
            case R.id.menu_browser_current_image:
                // View in browser
                handleWebUrl(requireContext(), Uri.parse(m.getPageTitle().getMobileUri()));
                return true;
            case R.id.menu_download_current_image:
                // Download
                if (!NetworkUtils.isInternetConnectionEstablished(getActivity())) {
                    ViewUtil.showShortSnackbar(getView(), R.string.no_internet);
                    return false;
                }
                downloadMedia(m);
                return true;
            case R.id.menu_set_as_wallpaper:
                // Set wallpaper
                setWallpaper(m);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Set the media as the device's wallpaper if the imageUrl is not null
     * Fails silently if setting the wallpaper fails
     * @param media
     */
    private void setWallpaper(Media media) {
        if (media.getImageUrl() == null || media.getImageUrl().isEmpty()) {
            Timber.d("Media URL not present");
            return;
        }
        ImageUtils.setWallpaperFromImageUrl(getActivity(), Uri.parse(media.getImageUrl()));
    }

    /**
     * Start the media file downloading to the local SD card/storage.
     * The file can then be opened in Gallery or other apps.
     *
     * @param m Media file to download
     */
    private void downloadMedia(Media m) {
        String imageUrl = m.getImageUrl(), fileName = m.getFilename();

        if (imageUrl == null
                || fileName == null
                || getContext() ==  null
                || getActivity() == null) {
            Timber.d("Skipping download media as either imageUrl %s or filename %s activity is null", imageUrl, fileName);
            return;
        }

        // Strip 'File:' from beginning of filename, we really shouldn't store it
        fileName = fileName.replaceFirst("^File:", "");

        Uri imageUri = Uri.parse(imageUrl);

        DownloadManager.Request req = new DownloadManager.Request(imageUri);
        //These are not the image title and description fields, they are download descs for notifications
        req.setDescription(getString(R.string.app_name));
        req.setTitle(m.getDisplayTitle());
        req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        // Modern Android updates the gallery automatically. Yay!
        req.allowScanningByMediaScanner();
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        PermissionUtils.checkPermissionsAndPerformAction(getActivity(), WRITE_EXTERNAL_STORAGE,
            () -> enqueueRequest(req), () -> Toast.makeText(getContext(),
                R.string.download_failed_we_cannot_download_the_file_without_storage_permission,
                Toast.LENGTH_SHORT).show(), R.string.storage_permission,
            R.string.write_storage_permission_rationale);

    }

    private void enqueueRequest(DownloadManager.Request req) {
        DownloadManager systemService =
            (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
        if (systemService != null) {
            systemService.enqueue(req);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!editable) { // Disable menu options for editable views
            menu.clear(); // see http://stackoverflow.com/a/8495697/17865
            inflater.inflate(R.menu.fragment_image_detail, menu);
            if (pager != null && contributions.size()>0) {
                Media m = contributions.get(pager.getCurrentItem());
                if (m != null) {
                    // Enable default set of actions, then re-enable different set of actions only if it is a failed contrib
                    menu.findItem(R.id.menu_browser_current_image).setEnabled(true).setVisible(true);
                    menu.findItem(R.id.menu_share_current_image).setEnabled(true).setVisible(true);
                    menu.findItem(R.id.menu_download_current_image).setEnabled(true).setVisible(true);
                    menu.findItem(R.id.menu_bookmark_current_image).setEnabled(true).setVisible(true);

                    // Initialize bookmark object
                    bookmark = new Bookmark(
                            m.getFilename(),
                            m.getCreator(),
                            BookmarkPicturesContentProvider.uriForName(m.getFilename())
                    );
                    updateBookmarkState(menu.findItem(R.id.menu_bookmark_current_image));

                    if (m instanceof Contribution ) {
                        Contribution c = (Contribution) m;
                        switch (c.getState()) {
                            case Contribution.STATE_FAILED:
                                menu.findItem(R.id.menu_browser_current_image).setEnabled(false).setVisible(false);
                                menu.findItem(R.id.menu_share_current_image).setEnabled(false).setVisible(false);
                                menu.findItem(R.id.menu_download_current_image).setEnabled(false).setVisible(false);
                                menu.findItem(R.id.menu_bookmark_current_image).setEnabled(false).setVisible(false);
                                break;
                            case Contribution.STATE_IN_PROGRESS:
                            case Contribution.STATE_QUEUED:
                                menu.findItem(R.id.menu_browser_current_image).setEnabled(false).setVisible(false);
                                menu.findItem(R.id.menu_share_current_image).setEnabled(false).setVisible(false);
                                menu.findItem(R.id.menu_download_current_image).setEnabled(false).setVisible(false);
                                menu.findItem(R.id.menu_bookmark_current_image).setEnabled(false).setVisible(false);
                                break;
                            case Contribution.STATE_COMPLETED:
                                // Default set of menu items works fine. Treat same as regular media object
                                break;
                        }
                    }
                }
            }
        }
    }

    private void updateBookmarkState(MenuItem item) {
        boolean isBookmarked = bookmarkDao.findBookmark(bookmark);
        int icon = isBookmarked ? R.drawable.ic_round_star_filled_24px : R.drawable.ic_round_star_border_24px;
        item.setIcon(icon);
    }

    public void showImage(int i) {
        Handler handler =  new Handler();
        handler.postDelayed(() -> pager.setCurrentItem(i), 5);
    }

    /**
     * The method notify the viewpager that number of items have changed.
     */
    public void notifyDataSetChanged(){
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
        if(getActivity() == null) {
            Timber.d("Returning as activity is destroyed!");
            return;
        }
        if (i+1 >= adapter.getCount()){
            try{
                ((CategoryImagesActivity) getContext()).requestMoreImages();
            }catch (Exception e){
                e.printStackTrace();
            }
            try{
                ((CategoryDetailsActivity) getContext()).requestMoreImages();
            }catch (Exception e){
                e.printStackTrace();
            }
            try{
                ((SearchActivity) getContext()).requestMoreImages();
            }catch (Exception e){
                e.printStackTrace();
            }
            try{
                ((ExploreActivity) getContext()).requestMoreImages();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onPageSelected(int i) {
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    public void onDataSetChanged() {
        if (null != adapter) {
            adapter.notifyDataSetChanged();
        }
    }

    //FragmentStatePagerAdapter allows user to swipe across collection of images (no. of images undetermined)
    private class MediaDetailAdapter extends FragmentStatePagerAdapter {
        public MediaDetailAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if(i==0 && getActivity()!=null){
                getActivity().supportInvalidateOptionsMenu();//Invalidate the options menu when the first item shows up, of course this is shitty, find out a better way
            }
            return MediaDetailFragment.forMedia(contributions.get(i), editable, isFeaturedImage);
        }

        @Override
        public int getCount() {
            return contributions.size();
        }
    }
}
