package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import sample.model.Album;
import sample.model.Artist;
import sample.model.DataSource;

public class Controller {

    @FXML
    private TableView artistTable;

    @FXML
    private ProgressBar progressBar;

    @FXML
    public void listArtist() {
        Task<ObservableList<Artist>> task = new GetAllArtistsTask();
        artistTable.itemsProperty().bind(task.valueProperty());
        progressBar.progressProperty().bind(task.progressProperty());

        progressBar.setVisible(true);

        task.setOnSucceeded(e->progressBar.setVisible(false));
        task.setOnFailed(e-> progressBar.setVisible(false));

        new Thread(task).start();
    }

    public void listAlbumsForArtist(){
        final Artist artist = (Artist) artistTable.getSelectionModel().getSelectedItem();
        if(artist == null){
            System.out.println("No artist selected");
            return;
        }
        Task<ObservableList<Album>> task = new Task<ObservableList<Album>>() {
            @Override
            protected ObservableList<Album> call() throws Exception {
                return FXCollections.observableArrayList(
                        DataSource.getInstance().queryAlbumsForArtistId(artist.getId()));
            }
        };
        artistTable.itemsProperty().bind(task.valueProperty());

        new Thread(task).start();
    }

    @FXML
    public void updateArtist() {
        //final Artist artist = (Artist) artistTable.getSelectionModel().getSelectedItem();
        final Artist artist = (Artist) artistTable.getItems().get(2);

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return DataSource.getInstance().updateArtistName(artist.getId(),"AC/DC");
            }
        };

        task.setOnSucceeded(e-> {
            if(task.valueProperty().get()) {
                artist.setName("AC/DC");
                artistTable.refresh();
            }
        });

        new Thread(task).start();
    }
}

class GetAllArtistsTask extends Task {
    @Override
    public ObservableList<Artist> call() {
        return FXCollections.observableArrayList(DataSource.getInstance().queryArtist(DataSource.ORDDER_BY_ASC));
    }
}
