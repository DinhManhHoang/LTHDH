package sample;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public class TrackListUtil {

    public static void saveTrackList(TrackList trackList) {
        List<TrackList> trackLists = readData();
        trackLists.add(trackList);
        writeData(trackLists);
    }

    public static List<TrackList> readData() {
        List<TrackList> trackLists = new ArrayList<>();
        try {
            Scanner inputFile = new Scanner(new File("src/sample/data.dt"));
            int listCount = Integer.parseInt(inputFile.nextLine());
            for (int i = 0; i < listCount; i++) {
                String listName = inputFile.nextLine();
                String listPath = inputFile.nextLine();
                TrackList trackList = new TrackList(i, listName, listPath);
                trackLists.add(trackList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trackLists;
    }

    public static void writeData(List<TrackList> trackLists) {
        try {
            FileWriter fw = new FileWriter("src/sample/data.dt", false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(String.valueOf(trackLists.size()));
            bw.newLine();
            for (int i = 0; i < trackLists.size(); i++) {
                bw.write(trackLists.get(i).getName().getValue());
                bw.newLine();
                bw.write(trackLists.get(i).getPath().getValue());
                bw.newLine();
            }
            bw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<TrackList> getAll() {
        ObservableList<TrackList> trackLists = FXCollections.observableArrayList(readData());
        return trackLists;
    }

    public static void delete(TrackList trackList) {
        List<TrackList> trackLists = readData();
        TrackList target = null;
        for (int i = 0; i < trackLists.size(); i++) {
            if (trackLists.get(i).getName().getValue().compareTo(trackList.getName().getValue()) == 0) {
                target = trackLists.get(i);
                break;
            }
        }
        if (target != null) {
            trackLists.remove(target);
        }
        writeData(trackLists);
    }

    public static void refreshList(ListView listView) {
        ObservableList<TrackList> items = getAll();
        listView.setItems(null);
        listView.setItems(items);
    }

}