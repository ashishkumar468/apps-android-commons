package fr.free.nrw.commons.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import fr.free.nrw.commons.bookmarks.locations.BookmarkLocationsContentProvider;
import fr.free.nrw.commons.bookmarks.pictures.BookmarkPicturesContentProvider;
import fr.free.nrw.commons.category.CategoryContentProvider;
import fr.free.nrw.commons.explore.recentsearches.RecentSearchesContentProvider;
import fr.free.nrw.commons.modifications.ModificationsContentProvider;

@Module
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class ContentProviderBuilderModule {

    @ContributesAndroidInjector
    abstract ModificationsContentProvider bindModificationsContentProvider();

    @ContributesAndroidInjector
    abstract CategoryContentProvider bindCategoryContentProvider();

    @ContributesAndroidInjector
    abstract RecentSearchesContentProvider bindRecentSearchesContentProvider();

    @ContributesAndroidInjector
    abstract BookmarkPicturesContentProvider bindBookmarkContentProvider();

    @ContributesAndroidInjector
    abstract BookmarkLocationsContentProvider bindBookmarkLocationContentProvider();

}
