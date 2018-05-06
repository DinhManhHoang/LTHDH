package sample;

import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    public MainApp() {

    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Trackio");

        this.primaryStage.initStyle(StageStyle.TRANSPARENT);

        this.primaryStage.getIcons().add(new Image("sample/img/icon.png"));

        initRootLayout();

        initPlayer();
    }

    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(new File("src/sample/RootLayout.fxml").toURI().toURL());
            rootLayout = (BorderPane) loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initPlayer() {
        try {
            // Load player overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(new File("src/sample/Player.fxml").toURI().toURL());
            AnchorPane playerOverview = (AnchorPane) loader.load();
            rootLayout.setCenter(playerOverview);
            PlayerController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean showTrackListDialog(TrackList trackList) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(new File("src/sample/TrackListEditDialog.fxml").toURI().toURL());
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Select destination folder");
            dialogStage.initStyle(StageStyle.TRANSPARENT);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.centerOnScreen();
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            TrackListEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setTrackList(trackList);
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

}