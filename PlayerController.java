package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.io.InputStream;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableRow;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.text.TextAlignment;
import javafx.stage.StageStyle;
import javafx.util.Duration;


public class PlayerController {

    @FXML
    private ListView<TrackList> trackListView;
    private class CLVCell extends ListCell<TrackList> {
        HBox hbox = new HBox();
        Label label = new Label("");
        Pane pane = new Pane();
        Button button = new Button("x");
        TrackList lastItem;

        public CLVCell() {
            super();
            button.getStyleClass().clear();
            button.getStyleClass().add("delete-track-button");
            hbox.getChildren().addAll(label, pane, button);
            HBox.setHgrow(pane, Priority.ALWAYS);
            button.setOnAction(event -> {
                handleDeleteTrackList(lastItem);
            });
        }

        protected void updateItem(TrackList item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if (empty) {
                lastItem = null;
                setGraphic(null);
            } else {
                lastItem = item;
                label.setText(item!=null ? item.getName().getValue() : "<null>");
                setGraphic(hbox);
            }
        }
    }

    private ObservableList<TrackList> observableTrackListsView;

    @FXML
    private TableView<Track> trackTableView;
    private ObservableList<Track> observableTracksView;
    @FXML
    private TableColumn<Track, String> titleColumn;
    @FXML
    private TableColumn<Track, String> authorColumn;
    @FXML
    private TableColumn<Track, String> albumColumn;
    @FXML
    private TableColumn<Track, String> yearColumn;

    @FXML
    private ToolBar mainToolBar;
    @FXML
    private ImageView maximizeImageView;

    private boolean maximized = false;

    @FXML
    private ProgressBar progressBar;
    private ChangeListener<Duration> progressChangeListener;
    @FXML
    private ImageView albumImageView;
    @FXML
    private ImageView albumImageViewLeft;
    @FXML
    private ImageView albumImageViewRight;
    @FXML
    private Label titleLabel;
    @FXML
    private Label artistLabel;
    @FXML
    private Label timeLabel;

    private MainApp mainApp;

    private MediaPlayer player;
    private Track currentTrack;
    private Track prevTrack;
    private Track nextTrack;
    private Media currentMedia;

    @FXML
    private Button playButton;

    public PlayerController() {
    }

    @FXML
    private void initialize() {

        EffectUtil.addDragListeners(mainToolBar);

        setupTrackListView();
        setupTrackTableView();

        mainToolBar.setOnMouseClicked((MouseEvent click) -> {
            if (click.getClickCount() == 2) {
                handleMaximize();
            }
        });

    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handlePlayTrigger() {
        if(player != null) {
            boolean playing = player.getStatus().equals(Status.PLAYING);
            if(playing) {
                player.pause();
            } else {
                player.play();
            }
        }
    }

    @FXML
    private void handleNextTrack() {
        if(nextTrack != null) playTrack(nextTrack);
    }


    @FXML
    private void handlePrevTrack() {
        if(prevTrack != null) playTrack(prevTrack);
    }

    private void refreshView() {
        observableTrackListsView = TrackListUtil.getAll();
        TrackListUtil.refreshList(trackListView);
        TrackUtil.refreshTable(trackTableView);
    }

    @FXML
    private void handleNewTrackList() {
        TrackList tempTrackList = new TrackList();
        boolean okClicked = mainApp.showTrackListDialog(tempTrackList);
        if (okClicked) {
            TrackListUtil.saveTrackList(tempTrackList);
            refreshView();
        }
    }

    private void handleDeleteTrackList(TrackList x) {
        TrackListUtil.delete(x);
        refreshView();
    }

    @FXML
    private void handleClose() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void handleMaximize() {
        if(maximized) {
            maximized = false;
            mainApp.getPrimaryStage().setMaximized(false);
            mainApp.getPrimaryStage().setMaxWidth(1040);
            mainApp.getPrimaryStage().setMaxHeight(650);
            mainApp.getPrimaryStage().centerOnScreen();
            InputStream minimizeImage = PlayerController.class.getResourceAsStream("img/top-square.png");
            maximizeImageView.setImage(new Image(minimizeImage));
        } else {
            maximized = true;
            mainApp.getPrimaryStage().setMaximized(true);
            InputStream maximizeImage = PlayerController.class.getResourceAsStream("img/top-shrink.png");
            maximizeImageView.setImage(new Image(maximizeImage));
        }
    }

    @FXML
    private void handleMinimize() {
        mainApp.getPrimaryStage().setIconified(true);
    }

    @FXML
    private void handleCredits() {
    }

    private void setupTrackListView() {

        observableTrackListsView = TrackListUtil.getAll();
        trackListView.setItems(observableTrackListsView);

        trackListView.setCellFactory((ListView<TrackList> p) -> {
            ListCell<TrackList> cell = new CLVCell();
            return cell;
        });

        trackListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showTrackListDetails(newValue));
    }

    private void setupTrackTableView() {
        titleColumn.setCellValueFactory(
                cellData -> cellData.getValue().getTitle());
        authorColumn.setCellValueFactory(
                cellData -> cellData.getValue().getArtist());
        albumColumn.setCellValueFactory(
                cellData -> cellData.getValue().getAlbum());
        yearColumn.setCellValueFactory(
                cellData -> cellData.getValue().getYear());
        trackTableView.setRowFactory( tv -> {
            TableRow<Track> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Track track = row.getItem();
                    playTrack(track);
                }
            });
            return row ;
        });
    }


    private void showTrackListDetails(TrackList trackList) {
        if (trackList != null) {
            observableTracksView = TrackUtil.getAll(trackList, trackTableView);
            trackTableView.setItems(observableTracksView);
            TrackUtil.refreshTable(trackTableView);
        }
    }

    private void playTrack(Track track) {

        currentTrack = track;
        int totalTracks = observableTracksView.size();
        int currentTrackNumber = observableTracksView.indexOf(currentTrack);
        int nextTrackNumber = currentTrackNumber+1;
        int prevTrackNumber = currentTrackNumber-1;
        if(prevTrackNumber >= 0) prevTrack = observableTracksView.get(prevTrackNumber);
        else prevTrack = observableTracksView.get(totalTracks-1);
        if(nextTrackNumber < totalTracks) nextTrack = observableTracksView.get(nextTrackNumber);
        else nextTrack = observableTracksView.get(0);

        trackTableView.getSelectionModel().select(currentTrack);

        if (player != null) {
            player.stop();
            player = null;
        }
        currentMedia = currentTrack.getMedia();
        player = new MediaPlayer(currentMedia);
        player.play();
        setCurrentlyPlaying(player);
        setMediaInfo(currentTrack);
    }

    private void setCurrentlyPlaying(MediaPlayer mediaPlayer) {
        mediaPlayer.seek(Duration.ZERO);
        progressBar.setProgress(0);

        progressBar.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.PRIMARY){
                Bounds b1 = progressBar.getLayoutBounds();
                double mouseX = event.getSceneX();
                double percent = (((b1.getMinX() + mouseX ) * 100) / b1.getMaxX());
                percent -= 2;
                double totalDurationMillis = mediaPlayer.getTotalDuration().toMillis();
                double seek = (totalDurationMillis * percent) / 100;
                mediaPlayer.seek(Duration.millis(seek));
            }
        });

        progressChangeListener = (ObservableValue<? extends Duration> observableValue,
                                  Duration oldValue, Duration newValue) -> {
            double currentTimeMillis = mediaPlayer.getCurrentTime().toMillis();
            double totalDurationMillis = mediaPlayer.getTotalDuration().toMillis();
            progressBar.setProgress(1.0 * currentTimeMillis / totalDurationMillis);

            double currentTimeSeconds = mediaPlayer.getCurrentTime().toSeconds();
            int minutes = (int) (currentTimeSeconds % 3600) / 60;
            int seconds = (int) currentTimeSeconds % 60;
            String formattedMinutes = String.format("%02d", minutes);
            String formattedSeconds = String.format("%02d", seconds);
            timeLabel.setText(formattedMinutes + ":" + formattedSeconds);

        };
        mediaPlayer.currentTimeProperty().addListener(progressChangeListener);

        player.setOnEndOfMedia(() -> {
            player.currentTimeProperty().removeListener(progressChangeListener);
            handleNextTrack();
        });

        player.setOnPaused(() -> {
            playButton.setText("Play");

        });

        player.setOnPlaying(() -> {
            playButton.setText("Pause");

        });
    }

    private void setMediaInfo(Track track) {
        titleLabel.setText(track.getTitle().getValue());
        Tooltip titleTooltip = new Tooltip(track.getTitle().getValue() + "\r" + track.getArtist().getValue().toUpperCase());
        titleTooltip.setTextAlignment(TextAlignment.CENTER);
        titleLabel.setTooltip(titleTooltip);
        artistLabel.setText(track.getArtist().getValue().toUpperCase());

        InputStream defaultAlbumStream = PlayerController.class.getResourceAsStream("img/noalbumart.png");
        Image defaultAlbumImage = new Image(defaultAlbumStream);
        InputStream defaultAlbumStreamLeft = PlayerController.class.getResourceAsStream("img/noalbumart.png");
        Image defaultAlbumImageLeft = new Image(defaultAlbumStreamLeft);
        InputStream defaultAlbumStreamRight = PlayerController.class.getResourceAsStream("img/noalbumart.png");
        Image defaultAlbumImageRight = new Image(defaultAlbumStreamRight);

        if(track.getImage() == null) {
            SequentialTransition transitionImageView =
                    EffectUtil.fadeTransition(albumImageView, defaultAlbumImage);
            transitionImageView.play();
        } else {
            SequentialTransition transitionImageView =
                    EffectUtil.fadeTransition(albumImageView, track.getImage());
            transitionImageView.play();
        }

        if(nextTrack == null || nextTrack.getImage() == null) {
            SequentialTransition transitionImageView =
                    EffectUtil.translateTransition(albumImageViewRight, defaultAlbumImageLeft, 0, 400);
            transitionImageView.play();
        } else {
            SequentialTransition transitionImageView =
                    EffectUtil.translateTransition(albumImageViewRight, nextTrack.getImage(), 0, 400);
            transitionImageView.play();
        }

        if(prevTrack == null || prevTrack.getImage() == null) {
            SequentialTransition transitionImageView =
                    EffectUtil.translateTransition(albumImageViewLeft, defaultAlbumImageRight, 0, -400);
            transitionImageView.play();
        } else {
            SequentialTransition transitionImageView =
                    EffectUtil.translateTransition(albumImageViewLeft, prevTrack.getImage(), 0, -400);
            transitionImageView.play();
        }

    }

}

