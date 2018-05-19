package sample;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.File;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

public class TrackListEditDialogController {

    @FXML
    private TextField nameField;

    @FXML
    private AnchorPane dialogAnchorPane;


    private Stage dialogStage;
    private TrackList trackList;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
        EffectUtil.addDragListeners(dialogAnchorPane);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTrackList(TrackList trackList) {
        this.trackList = trackList;
        nameField.setText(trackList.getName().getValue());
        if(trackList.getId().getValue() == 0) {

        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleDeleteTrackList() {
        TrackListUtil.delete(trackList);
        dialogStage.close();
    }

    @FXML
    private void handleSelectFolder() {
        if (isInputValid()) {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select the music folder");
            boolean oldOkClicked = okClicked;
            try {
                File selectedDirectory = chooser.showDialog(dialogStage);
                trackList.setPath(new SimpleStringProperty(selectedDirectory.toString()));
                trackList.setName(new SimpleStringProperty(nameField.getText()));
                okClicked = true;
                dialogStage.close();
            } catch (Exception e) {
                okClicked = oldOkClicked;
            }
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (nameField.getText() == null || nameField.getText().length() == 0) {
            errorMessage += "Invalid!\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
}

