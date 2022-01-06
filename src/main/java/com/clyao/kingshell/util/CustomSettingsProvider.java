package com.clyao.kingshell.util;

import com.jediterm.terminal.TerminalColor;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.ui.UIUtil;
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;

import java.awt.*;
import java.util.Locale;

/**
 * @author: clyao
 * @createDate: 2021/12/13
 * @description: 自定义jediterm配置
 */
public class CustomSettingsProvider extends DefaultSettingsProvider {

    @Override
    public float getTerminalFontSize() {
        return 16.0F;
    }

    @Override
    public Font getTerminalFont() {
        String fontName;
        if (UIUtil.isWindows) {
            if ((Locale.CHINA).equals(Locale.getDefault())) {
                fontName = "SimHei";
            } else {
                fontName = "Consolas";
            }
        } else if (UIUtil.isMac) {
            fontName = "Menlo";
        } else {
            fontName = "Monospaced";
        }
        return new Font(fontName, Font.PLAIN, (int) getTerminalFontSize());
    }

    @Override
    public TextStyle getDefaultStyle() {
        return new TextStyle(TerminalColor.WHITE, new TerminalColor(43,43,43));
    }
}
