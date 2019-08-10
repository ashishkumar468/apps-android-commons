package fr.free.nrw.commons.contributions.db;

import android.net.Uri;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import fr.free.nrw.commons.contributions.Contribution;
import java.util.Date;
import java.util.Objects;

@Entity(tableName = "contributions")
public class ContributionsItem {

    @PrimaryKey
    public int _id;

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

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

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

    public void setTransferred(int transferred) {
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
    public void fromContribution(Contribution contribution) {
        this.fileName = contribution.getFilename();
        if (null != contribution.getLocalUri()) {
            this.localUri = contribution.getLocalUri().toString();
        }

        if (null != contribution.getImageUrl()) {
            this.imageUrl = contribution.getImageUrl();
        }

        if (null != contribution.getDateUploaded()) {
            this.uploadDate = contribution.getDateUploaded().getTime();
        }

        this.length = contribution.getDataLength();

        this.timestamp = contribution.getDateCreated() == null ? System.currentTimeMillis()
                : contribution.getDateCreated().getTime();
        this.state = contribution.getState();
        this.transferred = contribution.getTransferred();
        this.source = contribution.getSource();
        this.description = contribution.getDescription();
        this.creator = contribution.getCreator();
        this.multiple = contribution.getMultiple() ? 1 : 0;
        this.width = contribution.getWidth();
        this.height = contribution.getHeight();
        this.license = contribution.getLicense();
        this.wikiDataEntityId = contribution.getWikiDataEntityId();
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
