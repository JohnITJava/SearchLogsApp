package ru.splat.searchLogsApp;

import com.sun.javafx.PlatformUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Controller implements Initializable {

    @FXML
    TreeView<String> filesTV;

    @FXML
    Button goBut, stpBut;

    @FXML
    TextArea textF;

    @FXML
    VBox mainVBox;

    @FXML
    TextField pathF, extF;

    @FXML
    Label hint, tmLab;

    @FXML
    TabPane tabPane;

    TreeItem<String> root;
    Thread extController = null;

    public Button getGoBut() {
        return goBut;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Platform.runLater(() -> ((Stage) mainVBox.getScene().getWindow()).setOnCloseRequest(t -> {
            Platform.exit();
        }));

        filesTV.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if(mouseEvent.getClickCount() == 2){
                    String fileName = filesTV.getFocusModel().getFocusedItem().getValue();
                    if (fileName != null){
                        GUIHelper.updateGUI(() -> {
                            Path file = ExtController.getFindedFiles().get(fileName);
                            GUIHelper.tabsCreater(file, tabPane, new TextArea());
                        });
                    }
                }
            }
        });

    }

    public void startSearching(ActionEvent actionEvent) throws IOException {

        refreshViews();

        Path path = null;
        String text = null;
        String extension = null;

        if (pathF.getText() != null && !textF.getText().equals("") && extF.getText() != null) {
            path = Paths.get(pathF.getText());
            if (!path.toFile().exists()){
                hint.setText("Entered path does not exist");
                return;
            }
            text = textF.getText();
            extension = "." + extF.getText();

            extController = new ExtController(path, text, extension, tabPane, root, goBut, hint);
            extController.setDaemon(true);
            extController.start();
            System.out.println(extController.getName());
            if (extController != null || extController.isAlive()){
                goBut.setDisable(true);}
        }

    }

    public void refreshViews(){
        /*pathList.clear();
        tabsList.clear();
        textAreas.clear();*/

        tabPane.getTabs().clear();

        root = new TreeItem<>("Root");

        filesTV.setRoot(root);
        root.setExpanded(true);
        filesTV.setShowRoot(false);
    }

    public void stopSearching(ActionEvent actionEvent) throws InterruptedException {
        extController.stop();
        goBut.setDisable(false);
        hint.setText("Searching was stopped");

    }
}