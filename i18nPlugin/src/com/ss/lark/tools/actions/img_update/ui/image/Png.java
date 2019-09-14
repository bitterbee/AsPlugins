package com.ss.lark.tools.actions.img_update.ui.image;

import com.ss.lark.tools.util.ImageSuffex;

import javax.swing.*;
import java.awt.*;

/**
 * Created by zyl06 on 2019/9/7.
 */
public class Png implements IImageGenerator {

    @Override
    public Image getImage(String path) {
        if (path != null && path.toLowerCase().endsWith(ImageSuffex.PNG)) {
            return new ImageIcon(path, "preview").getImage();
        }
        return null;
    }
}
