package com.clyao.kingshell.controller;


import com.clyao.kingshell.util.AlertMessageUtil;
import com.clyao.kingshell.util.CustomSettingsProvider;
import com.clyao.kingshell.util.JSchShellTtyConnector;
import com.clyao.kingshell.util.SSHUtil;
import com.google.common.base.Ascii;
import com.jediterm.terminal.ui.JediTermWidget;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;


import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: clyao
 * @createDate: 2021/12/13
 * @description: MainController控制类
 */
////
public class MainController implements Initializable {
    /**
     * BorderPane根布局pane
     */
    @FXML
    public BorderPane root;

    /**
     * ssh内容pane
     */
    @FXML
    public StackPane container;

    /**
     * shell左侧菜单对应pane
     */
    @FXML
    public AnchorPane shellPane;

    /**
     * db左侧菜单对应pane
     */
    @FXML
    public AnchorPane dbPane;

    /**
     * tool左侧菜单对应pane
     */
    @FXML
    public AnchorPane toolPane;

    /**
     * setting左侧菜单对应pane
     */
    @FXML
    public AnchorPane settingPane;

    /**
     * ssh地址ip
     */
    @FXML
    public TextField ip;

    /**
     * ssh端口
     */
    @FXML
    public TextField port;

    /**
     * ssh用户名
     */
    @FXML
    public TextField userName;

    /**
     * ssh密码
     */
    @FXML
    public PasswordField passWord;

    /**
     * ssh连接按钮
     */
    @FXML
    public Button connect;

    /**
     * shell左侧菜单图标按钮
     */
    @FXML
    public ImageView shell;

    /**
     * db左侧菜单图标按钮
     */
    @FXML
    public ImageView db;

    /**
     * tool左侧菜单图标按钮
     */
    @FXML
    public ImageView tool;

    /**
     * setting左侧菜单图标按钮
     */
    @FXML
    public ImageView setting;

    /**
     * shell图标按钮状态
     */
    public  boolean shellSataus;

    /**
     * db图标按钮状态
     */
    public  boolean dbSataus;

    /**
     * tool图标按钮状态
     */
    public  boolean toolSataus;

    /**
     * setting图标按钮状态
     */
    public  boolean settingSataus;

    /**
     * JediTermWidget组件
     */
    public static JediTermWidget message;

    /**
     * ctr按键状态
     */
    public AtomicBoolean ctrl = new AtomicBoolean(false);

    /**
     * c按键状态
     */
    public AtomicBoolean c = new AtomicBoolean(false);

    /**
     * ssh连接状态
     */
    private AtomicBoolean sshStatus = new AtomicBoolean(false);

    /**
     * 创建线程
     */
    private Thread thread = new Thread(new Task<Void>() {
        @Override
        protected Void call() {
            while (true) {
                if (sshStatus.get() && ctrl.get() && c.get()) {
                    try {
                        byte[] bytes = new byte[]{Ascii.ETX};
                        message.getTtyConnector().write(bytes);
                        if (!Thread.currentThread().isInterrupted()) {
                            Thread.sleep(500);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    });

    /**
     * 连接服务器
     */
    public synchronized void connectServer() {
        if (ip.getText() == null || ip.getText().isEmpty()) {
            AlertMessageUtil.alertWarningMessage("IP为空!");
            return;
        }
        if (port.getText() == null || port.getText().isEmpty()) {
            AlertMessageUtil.alertWarningMessage("端口为空!");
            return;
        }
        if (userName.getText() == null || userName.getText().isEmpty()) {
            AlertMessageUtil.alertWarningMessage("用户名为空!");
            return;
        }
        if (passWord.getText() == null || passWord.getText().isEmpty()) {
            AlertMessageUtil.alertWarningMessage("密码为空!");
            return;
        }
        new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    if ("连接".equals(connect.getText())) {
                        String realIp = ip.getText();
                        int realPort = Integer.parseInt(port.getText());
                        if (ip.getText().contains(":")) {
                            String[] string = realIp.split(":");
                            realIp = string[0];
                            realPort = Integer.parseInt(string[1]);
                        }
                        SSHUtil.initSSHParam(realIp, realPort, userName.getText(), passWord.getText());
                        Platform.runLater(() -> {
                            ip.setDisable(true);
                            port.setDisable(true);
                            userName.setDisable(true);
                            passWord.setDisable(true);
                            connect.setText("断开");
                            connect.setStyle("-fx-background-color: #fa3534");
                            SwingNode swingNode = new SwingNode();
                            createAndSetSwingContent(swingNode);
                            container.getChildren().add(swingNode);
                        });
                        if (!thread.isAlive()) {
                            thread.start();
                        }
                        sshStatus.set(true);
                        root.setOnKeyPressed(event -> {
                            if (event.getCode() == KeyCode.CONTROL) {
                                ctrl.set(true);
                            }
                            if (event.getCode() == KeyCode.C) {
                                c.set(true);
                            }
                        });
                        root.setOnKeyReleased(event -> {
                            if (event.getCode() == KeyCode.CONTROL) {
                                ctrl.set(false);
                            }
                            if (event.getCode() == KeyCode.C) {
                                c.set(false);
                            }
                        });
                    } else if ("断开".equals(connect.getText())) {
                        sshStatus.set(false);
                        Platform.runLater(() -> {
                            ip.setDisable(false);
                            port.setDisable(false);
                            userName.setDisable(false);
                            passWord.setDisable(false);
                            connect.setText("连接");
                            connect.setStyle("-fx-background-color: #009966");
                            container.getChildren().removeAll(container.getChildren());
                            message.close();
                        });
                    }
                } catch (
                        Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).start();
    }

    /**
     * 创建SwingNode并添加JediTermWidget到节点
     * @param swingNode
     */
    private void createAndSetSwingContent(SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> {
            message = createTerminalWidget();
            swingNode.setContent(message);
        });
    }

    /**
     * 创建JediTermWidget
     * @return
     */
    private JediTermWidget createTerminalWidget() {
        JediTermWidget jediTermWidget = new JediTermWidget(80, 15, new CustomSettingsProvider());
        jediTermWidget.setTtyConnector(new JSchShellTtyConnector());
        jediTermWidget.start();
        return jediTermWidget;
    }

    /**
     * 部署
     * @throws Exception
     */
    public synchronized void oneDeploy() throws Exception {
        if (!sshStatus.get()) {
            AlertMessageUtil.alertWarningMessage("SSH未连接!");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择脚本文件");
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("脚本文件", "*.sh"));
        File file = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (file == null || !file.exists()) {
            return;
        }
        JSchShellTtyConnector jSchShellTtyConnector = (JSchShellTtyConnector) message.getTtyConnector();
        SSHUtil.upload(
                jSchShellTtyConnector.getMySession(),
                new FileInputStream(file),
                "/root", file.getName());
        jSchShellTtyConnector.write(("chmod +x /root " + file.getName() + "\r").getBytes(StandardCharsets.UTF_8));
        jSchShellTtyConnector.write(("bash /root/" + file.getName() + "\r").getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 初始化界面
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //默认显示shellPane
        shellPane.setVisible(true);
        shellSataus = true;
        shell.setImage(new Image("/images/shell-fill.png"));

        //shell鼠标移入
        shell.setOnMouseEntered(event -> {
            if(!shellSataus){
                shell.setImage(new Image("/images/shell-over.png"));
            }
        });
        //shell鼠标离开
        shell.setOnMouseExited(event -> {
            if(!shellSataus){
                shell.setImage(new Image("/images/shell.png"));
            }
        });
        //shell鼠标点击
        shell.setOnMouseClicked(event -> {
            settingPane.setVisible(false);
            toolPane.setVisible(false);
            dbPane.setVisible(false);
            shellPane.setVisible(true);
            shell.setImage(new Image("/images/shell-fill.png"));
            setting.setImage(new Image("/images/setting.png"));
            tool.setImage(new Image("/images/tool.png"));
            db.setImage(new Image("/images/db.png"));
            shellSataus = true;
            settingSataus = false;
            toolSataus = false;
            dbSataus = false;
        });

        //db鼠标移入
        db.setOnMouseEntered(event -> {
            if(!dbSataus){
                db.setImage(new Image("/images/db-over.png"));
            }
        });
        //db鼠标离开
        db.setOnMouseExited(event -> {
            if(!dbSataus){
                db.setImage(new Image("/images/db.png"));
            }
        });
        //db鼠标点击
        db.setOnMouseClicked(event -> {
            settingPane.setVisible(false);
            toolPane.setVisible(false);
            shellPane.setVisible(false);
            dbPane.setVisible(true);
            db.setImage(new Image("/images/db-fill.png"));
            setting.setImage(new Image("/images/setting.png"));
            tool.setImage(new Image("/images/tool.png"));
            shell.setImage(new Image("/images/shell.png"));
            dbSataus = true;
            settingSataus = false;
            toolSataus = false;
            shellSataus = false;
        });

        //tool鼠标移入
        tool.setOnMouseEntered(event -> {
            if(!toolSataus){
                tool.setImage(new Image("/images/tool-over.png"));
            }
        });
        //tool鼠标离开
        tool.setOnMouseExited(event -> {
            if(!toolSataus){
                tool.setImage(new Image("/images/tool.png"));
            }
        });
        //tool鼠标点击
        tool.setOnMouseClicked(event -> {
            settingPane.setVisible(false);
            dbPane.setVisible(false);
            shellPane.setVisible(false);
            toolPane.setVisible(true);
            tool.setImage(new Image("/images/tool-fill.png"));
            setting.setImage(new Image("/images/setting.png"));
            db.setImage(new Image("/images/db.png"));
            shell.setImage(new Image("/images/shell.png"));
            toolSataus = true;
            settingSataus = false;
            dbSataus = false;
            shellSataus = false;
        });

        //setting鼠标移入
        setting.setOnMouseEntered(event -> {
            if(!settingSataus){
                setting.setImage(new Image("/images/setting-over.png"));
            }
        });
        //shell鼠标离开
        setting.setOnMouseExited(event -> {
            if(!settingSataus){
                setting.setImage(new Image("/images/setting.png"));
            }
        });
        //shell鼠标点击
        setting.setOnMouseClicked(event -> {
            toolPane.setVisible(false);
            dbPane.setVisible(false);
            shellPane.setVisible(false);
            settingPane.setVisible(true);
            setting.setImage(new Image("/images/setting-fill.png"));
            tool.setImage(new Image("/images/tool.png"));
            db.setImage(new Image("/images/db.png"));
            shell.setImage(new Image("/images/shell.png"));
            settingSataus = true;
            toolSataus = false;
            dbSataus = false;
            shellSataus = false;
        });


    }
}
