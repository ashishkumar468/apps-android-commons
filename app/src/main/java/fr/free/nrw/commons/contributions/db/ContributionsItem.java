package fr.free.nrw.commons.contributions.db;

import android.net.Uri;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import fr.free.nrw.commons.contributions.Contribution;
import java.util.Date;
import java.util.Objects;

@Entity(tableName = "contributions")
public class ContributionsItem {
    @PrimaryKey @NonNull
    public String fileName;

    @ColumnInfo(name = "local_uri")
    public String localUri;

    @ColumnInfo(name = "image_url")
    public String imageUrl;

    public long uploadDate;

    public long timestamp;

    public int state;

    public long length;

    public long transferred;

    public String source;

    public String description;

    public String creator;

    public int multiple;

    public int width;

    public int height;

    public String license;

    public String wikiDataEntityId;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLocalUri() {
        return localUri;
    }

    public void setLocalUri(String localUri) {
        this.localUri = localUri;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(int uploadDate) {
        this.uploadDate = uploadDate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getTransferred() {
        return transferred;
    }

    public void setTransferred(long transferred) {
        this.transferred = transferred;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public int getMultiple() {
        return multiple;
    }

    public void setMultiple(int multiple) {
        this.multiple = multiple;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getWikiDataEntityId() {
        return wikiDataEntityId;
    }

    public void setWikiDataEntityId(String wikiDataEntityId) {
        this.wikiDataEntityId = wikiDataEntityId;
    }

    /**
     * Transform a contribution to ContributionItem
     */
    public static ContributionsItem fromContribution(Contribution contribution) {
        ContributionsItem contributionsItem=new ContributionsItem();
        contributionsItem.fileName = contribution.getFilename();
        if (null != contribution.getLocalUri()) {
            contributionsItem.localUri = contribution.getLocalUri().toString();
        }

        if (null != contribution.getImageUrl()) {
            contributionsItem.imageUrl = contribution.getImageUrl();
        }

        if (null != contribution.getDateUploaded()) {
            contributionsItem.uploadDate = contribution.getDateUploaded().getTime();
        }

        contributionsItem.length = contribution.getDataLength();

        contributionsItem.timestamp = contribution.getDateCreated() == null ? System.currentTimeMillis()
                : contribution.getDateCreated().getTime();
        contributionsItem.state = contribution.getState();
        contributionsItem.transferred = contribution.getTransferred();
        contributionsItem.source = contribution.getSource();
        contributionsItem.description = contribution.getDescription();
        contributionsItem.creator = contribution.getCreator();
        contributionsItem.multiple = contribution.getMultiple() ? 1 : 0;
        contributionsItem.width = contribution.getWidth();
        contributionsItem.height = contribution.getHeight();
        contributionsItem.license = contribution.getLicense();
        contributionsItem.wikiDataEntityId = contribution.getWikiDataEntityId();
        return contributionsItem;
    }

    public Contribution toContribution() {
        Contribution contribution = new Contribution();
        contribution.setLicense(this.license);
        contribution.setFilename(this.fileName);
        contribution.setLocalUri(parseUri(this.localUri));
        contribution.setDateUploaded(parseTimestamp(this.uploadDate));
        contribution.setHeight(this.height);
        contribution.setWidth(this.width);
        contribution.setWikiDataEntityId(this.wikiDataEntityId);
        contribution.setMultiple(this.multiple == 1);
        contribution.setDateCreatedSource(
                Objects.requireNonNull(parseTimestamp(this.timestamp)).toString());
        contribution.setState(this.state);
        contribution.setDataLength(this.length);
        contribution.setImageUrl(this.imageUrl);
        contribution.setTransferred(this.transferred);
        contribution.setCreator(this.creator);
        contribution.setDescription(this.description);
        contribution.setSource(this.source);
        return contribution;
    }

    @Nullable
    private static Date parseTimestamp(long timestamp) {
        return timestamp == 0 ? null : new Date(timestamp);
    }

    @Nullable
    private static Uri parseUri(String uriString) {
        return TextUtils.isEmpty(uriString) ? null : Uri.parse(uriString);
    }

}
