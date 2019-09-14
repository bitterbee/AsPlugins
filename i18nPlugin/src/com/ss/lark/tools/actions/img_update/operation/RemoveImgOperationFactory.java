package com.ss.lark.tools.actions.img_update.operation;

import com.intellij.openapi.project.Project;
import com.ss.lark.tools.util.CollectionUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyl06 on 2019/2/25.
 */
public class RemoveImgOperationFactory extends BaseImgOperationFactory {

    private String toResPath;

    public RemoveImgOperationFactory(Project project, String subProjPath, String gitPath) {
        super(project, subProjPath, gitPath);
        toResPath = getOutputResPath(project, subProjPath);
    }

    @Override
    protected List<ImgOperation> create(String mipmap) throws Exception {
        List<ImgOperation> result = new ArrayList<ImgOperation>();

        String inMipmapPath = getInputImagePath(gitPath, subProjPath, mipmap);
        File inImageFolder = new File(inMipmapPath);
        List<File> inImages = new ArrayList<>();
        List<String> inImageNames = new ArrayList<>();
        if (inImageFolder.exists() && inImageFolder.isDirectory()) {
            File[] inMudules = inImageFolder.listFiles();
            for (File inMudule : CollectionUtil.iterable(inMudules)) {
                for (File inImage : CollectionUtil.iterable(inMudule.listFiles())) {
                    inImages.add(inImage);
                    inImageNames.add(inImage.getParentFile().getName() + "_" + inImage.getName());
                }
            }
        }

        String toImagePath = toResPath + File.separator + inImageFolder.getName();
        File toMipmapFile = new File(toImagePath);
        if (!toMipmapFile.exists()) {
            return result;
        }

        List<File> toImages = new ArrayList<>();
        for (File toImage : CollectionUtil.iterable(toMipmapFile.listFiles())) {
            toImages.add(toImage);
        }

        for (File toFile : toImages) {
            String toName = toFile.getName();
            if (!isPhoto(toName)) {
                continue;
            }
            if (!inImageNames.contains(toName)) {
                String inPath = inMipmapPath + File.separator + "____NOT_EXIST____";
                result.add(new ImgOperation(subProjPath, inPath, toFile.getAbsolutePath()));
            }
        }

        return result;
    }
}
