package com.clyao.kingshell.util;

import com.clyao.kingshell.MainApplication;
import javafx.scene.control.Alert;

/**
 * @author: clyao
 * @createDate: 2021/12/13
 * @description: AlertMessageUtil 弹窗对话框工具类
 */
public class AlertMessageUtil {

    /**
     * 警告对话框
     * @param text
     */
    public static void alertWarningMessage(String text){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("警告");
        alert.setHeaderText(null);
        alert.setResizable(false);
        alert.setContentText(text);
        alert.initOwner(MainApplication.globalStage);
        alert.show();
    }
}
