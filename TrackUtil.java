package sample;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.media.Media;


public class TrackUtil {

    public static final List<String> SUPPORTED_FILE_EXTENSIONS = Arrays.asList(".mp3");

    public static ObservableList<Track> getAll(TrackList trackList, TableView trackTable) {

        ObservableList<Track> tracks = FXCollections.observableArrayList();

        File dir = new File(trackList.getPath().getValue());
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Error");
        }

        for (String file : dir.list((File dir1, String name) -> {
            for(String ext : SUPPORTED_FILE_EXTENSIONS) {
                if (name.endsWith(ext)) {
                    return true;
                }
            }
            return false;
        }))
        {
            String fileURL = dir + "\\" + file;
            String cleanURL = cleanURL(fileURL);
            String mediaURL = "file:///" + cleanURL;

            Track track = new Track();
            Media media = new Media(mediaURL);
            media.getMetadata().addListener((Change<? extends String,
                    ? extends Object> ch) -> {
                if (ch.wasAdded()) {
                    handleMetadata(ch.getKey(), ch.getValueAdded(), (Track) track);
                    if(track.getArtist() == null || track.getArtist().getValue().equals("")) {
                        track.setArtist(new SimpleStringProperty("None"));
                    }
                    if(track.getAlbum() == null || track.getAlbum().getValue().equals("")) {
                        track.setAlbum(new SimpleStringProperty("None"));
                    }
                    if(track.getYear() == null || track.getYear().getValue().equals("")) {
                        track.setYear(new SimpleStringProperty("None"));
                    }
                    if(track.getTitle() == null || track.getTitle().getValue().equals("")) {
                        track.setTitle(new SimpleStringProperty(track.getFileName().getValue().replace(".mp3", "")));
                    }
                    TrackUtil.refreshTable(trackTable);
                }
            });
            track.setMedia(media);
            track.setFileName(new SimpleStringProperty(file));
            track.setPath(new SimpleStringProperty(dir + "/" + file));
            tracks.add(track);
        }

        return tracks;
    }

    public static void handleMetadata(String key, Object value, Track track) {
        if(key.equals("album")) {
            if(value.toString().equals("")) {
                track.setAlbum(new SimpleStringProperty("None"));
            } else {
                track.setAlbum(new SimpleStringProperty(value.toString()));
            }
        } else if(key.equals("artist") || key.equals("album artist")) {
            if(value.toString().equals("")) {
                track.setArtist(new SimpleStringProperty("None"));
            } else {
                track.setArtist(new SimpleStringProperty(value.toString()));
            }
        } else if(key.equals("title")) {
            if(value.toString().equals("")) {
                track.setTitle(new SimpleStringProperty(track.getFileName().getValue().replace(".mp3", "")));
            } else {
                track.setTitle(new SimpleStringProperty(value.toString()));
            }
        } else if(key.equals("year")) {
            if(value.toString().equals("")) {
                track.setYear(new SimpleStringProperty("None"));
            } else {
                track.setYear(new SimpleStringProperty(value.toString()));
            }
        } else if(key.equals("image")) {
            track.setImage((Image) value);
        }
    }

    public static void refreshTable(TableView table) {
        for (int i = 0; i < table.getColumns().size(); i++) {
            TableColumn tableColumn = ((TableColumn) (table.getColumns().get(i)));
            if(tableColumn.isVisible()) {
                tableColumn.setVisible(false);
                tableColumn.setVisible(true);
            }
        }
    }

    private static String cleanURL(String url) {
        url = url.replace("\\", "/");
        url = url.replaceAll(" ", "%20");
        url = url.replace("[", "%5B");
        url = url.replace("]", "%5D");
        return url;
    }

}
