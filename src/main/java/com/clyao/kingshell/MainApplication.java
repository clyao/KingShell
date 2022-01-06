package com.clyao.kingshell;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * @author: clyao
 * @createDate: 2021/12/13
 * @description: MainApplication入口类
 */
public class MainApplication extends Application {

    /**
     * 全局stage
     */
    public static Stage globalStage;

    @Override
    public void start(Stage stage) throws IOException {
        globalStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 650);
        globalStage.setTitle("SSH终端 by clyao");
        globalStage.getIcons().add(new Image("/images/icon.png"));
        globalStage.setResizable(false);
        globalStage.setScene(scene);

        globalStage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("退出");
            alert.setHeaderText(null);
            alert.initOwner(globalStage);
            alert.setContentText("是否退出?");
            Optional<ButtonType> result = alert.showAndWait();
            result.ifPresent(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    Platform.exit();
                    System.exit(0);
                } else {
                    event.consume();
                }
            });
        });
        globalStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
