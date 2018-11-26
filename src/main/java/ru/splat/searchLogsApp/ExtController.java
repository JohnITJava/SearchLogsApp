package ru.splat.searchLogsApp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static ru.splat.searchLogsApp.GUIHelper.tabsCreater;

public class ExtController extends Thread {
    private static final int MAX_OBJ_SIZE = 1024 * 128; //128Kb
    private static final int MAX_PART_SIZE = 1024 * 128;
    private Path path;
    private String text;
    private String extension;
    private boolean isFinded;
    private static HashMap<String, Path> findedFiles = new HashMap<>();

    public static HashMap<String, Path> getFindedFiles() {
        return findedFiles;
    }

    @FXML
    TreeItem<String> root;
    @FXML
    TabPane tabPane;
    @FXML
    Button goBtn;
    @FXML
    Label hint;

    public ExtController(Path path, String text, String extension, TabPane tabPane, TreeItem<String> root, Button goBtn, Label hint) {
        this.path = path;
        this.text = text;
        this.extension = extension;
        this.tabPane = tabPane;
        this.root = root;
        this.goBtn = goBtn;
        this.hint = hint;
    }

    @Override
    public void run() {
        Platform.runLater(() -> {hint.setText("Searching is starting");});
        findedFiles.clear();
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (getFileExtension(file.getFileName().toString()).equals(extension)) {

                        if (file.toFile().length() <= MAX_OBJ_SIZE) {
                            checkSmallFile(file);
                        } else if (file.toFile().length() >= MAX_OBJ_SIZE){
                            checkBigFile(file);
                            if (isFinded){
                                //TextArea textArea = new TextArea();

                                GUIHelper.updateGUI(() -> {
                                    GUIHelper.displayTreeForElement(root, file);
                                    /*tabsCreater(file, tabPane, textArea);
                                    try {
                                        writeFileInTab(file, textArea);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }*/
                                });

                            }

                        }
                    }

                    return FileVisitResult.CONTINUE;
                }
            });

            GUIHelper.updateGUI(() -> {
                if (findedFiles.isEmpty()){hint.setText("Find Nothing");}
                else {
                hint.setText("Searching is over. Click to finded file to open it");}
            });
            goBtn.setDisable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkBigFile(Path file) throws IOException {
        isFinded = false;
        FileInputStream f = new FileInputStream(file.toFile());
        FileChannel ch = f.getChannel();
        ByteBuffer bb = ByteBuffer.allocateDirect(MAX_PART_SIZE);
        byte[] part = new byte[MAX_PART_SIZE];
        int i = 0;
        int nRead;
        String str;
        while ((nRead = ch.read(bb)) != -1)
        {
            i = 0;
            bb.position(0);
            bb.limit(nRead);
            while (bb.hasRemaining()){
                part[i++] = bb.get();
            }
            str = new String(part, StandardCharsets.UTF_8);
            if (str.contains(text)){
                System.out.println(file.getFileName());
                findedFiles.put(file.getFileName().toString(), file);
                isFinded = true;
                break;
            }
            bb.clear();
        }
        f.close();
        ch.close();
    }

    public static void writeFileInTab(Path file, TextArea textArea) throws IOException {
        FileInputStream f = new FileInputStream(file.toFile());
        FileChannel ch = f.getChannel();
        ByteBuffer bb = ByteBuffer.allocateDirect(MAX_PART_SIZE);
        byte[] part = new byte[MAX_PART_SIZE];
        int i = 0;
        int nRead;
        String str = null;
        while ((nRead = ch.read(bb)) != -1)
        {
            i = 0;
            bb.position(0);
            bb.limit(nRead);
            while (bb.hasRemaining()){
                part[i++] = bb.get();
            }
            str = new String(part, StandardCharsets.UTF_8);
            textArea.appendText(str);
            bb.clear();
        }
        f.close();
        ch.close();
    }

    private void checkSmallFile(Path file) throws IOException {
        if (fileContainsWord(file, text)) {
            System.out.println(file.getFileName());
            findedFiles.put(file.getFileName().toString(), file);
            //List<String> allTextInFile = Files.readAllLines(file);
            //TextArea textArea = new TextArea();
            /*for (int i = 0; i < allTextInFile.size(); i++) {
                textArea.appendText(allTextInFile.get(i) + "\n");
            }*/
            GUIHelper.updateGUI(() -> {
                GUIHelper.displayTreeForElement(root, file);
                //tabsCreater(file, tabPane, textArea);
            });
        }
    }

    private String getFileExtension(String fileString) {
        int index = fileString.lastIndexOf('.');
        return index == -1 ? null : fileString.substring(index);
    }

    private boolean fileContainsWord(Path path, String word) throws IOException {
        return new String(Files.readAllBytes(path)).contains(word);
    }

}