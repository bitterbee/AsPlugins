package com.ss.lark.tools.actions.img_update.ui.node;

import com.ss.lark.tools.actions.img_update.operation.ImgOperation;

/**
 * Created by zyl06 on 2018/7/21.
 */
public class ImgOperationData extends Data {
    public ImgOperation operation;

    public ImgOperationData(ImgOperation operation, boolean selected) {
        super(selected);
        this.operation = operation;
    }

    @Override
    public String toString() {
        return operation.toSimpleString();
    }
}
