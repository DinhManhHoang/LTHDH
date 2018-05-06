package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.media.Media;

public class Track {

    private StringProperty fileName;
    private StringProperty path;
    private Media media;
    private StringProperty album;
    private StringProperty artist;
    private StringProperty title;
    private StringProperty year;
    private Image image;

    public Track() {
        this(null, null, null);
    }

    public Track(String fileName, String filePath, Media media) {
        this.fileName = new SimpleStringProperty(fileName);
        this.path = new SimpleStringProperty(filePath);
        this.media = media;
    }

    public StringProperty getFileName() {
        return fileName;
    }

    public void setFileName(StringProperty fileName) {
        this.fileName = fileName;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public StringProperty getPath() {
        return path;
    }

    public void setPath(StringProperty path) {
        this.path = path;
    }

    public StringProperty getAlbum() {
        return album;
    }

    public void setAlbum(StringProperty album) {
        this.album = album;
    }

    public StringProperty getArtist() {
        return artist;
    }

    public void setArtist(StringProperty artist) {
        this.artist = artist;
    }

    public StringProperty getTitle() {
        return title;
    }

    public void setTitle(StringProperty title) {
        this.title = title;
    }

    public StringProperty getYear() {
        return year;
    }

    public void setYear(StringProperty year) {
        this.year = year;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

}
