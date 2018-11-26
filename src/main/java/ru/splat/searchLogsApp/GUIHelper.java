package ru.splat.searchLogsApp;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class GUIHelper {

    public synchronized static void displayTreeForElement(TreeItem<String> root, Path path){
        String[] items = null;
        if (OsCheck.getOperatingSystemType() == OsCheck.OSType.Windows){
        items = path.toString().split("\\\\");
        }
        if (OsCheck.getOperatingSystemType() == OsCheck.OSType.Linux || OsCheck.getOperatingSystemType() == OsCheck.OSType.MacOS){
            items = path.toString().split("/");
        }

        addTreeItems(items, root);

    }

    private static void addTreeItems(String[] items, TreeItem<String> root){
        if (items.length < 1) return;
        TreeItem<String> child = new TreeItem<>(items[0]);
        child.setExpanded(true);
        root.getChildren().add(child);
        addTreeItems(Arrays.copyOfRange(items, 1, items.length), child);
    }

    public synchronized static void updateGUI(Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(r);
        }
    }

    public synchronized static void tabsCreater(Path file, TabPane pane, TextArea textArea) {
        Tab tab = new Tab(file.getFileName().toString());
        VBox vBox = new VBox(textArea);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setMinHeight(300);
        textArea.setPrefHeight(1000);
        textArea.setMinWidth(100);
        tab.setContent(vBox);
        pane.getTabs().add(tab);
        try {
            ExtController.writeFileInTab(file, textArea);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
