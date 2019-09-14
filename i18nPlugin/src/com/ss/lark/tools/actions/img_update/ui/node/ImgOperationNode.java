package com.ss.lark.tools.actions.img_update.ui.node;

import com.ss.lark.tools.actions.img_update.operation.ImgOperation;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by zyl06 on 2019/2/25.
 */
public class ImgOperationNode extends WrapNode<ImgOperationData> {

    public ImgOperationNode(ImgOperation op, boolean selected) {
        super();
        data = new DataNode<ImgOperationData>(new ImgOperationData(op, selected));
        node = new DefaultMutableTreeNode(data);
    }

    public DataNode<ImgOperationData> data() {
        return data;
    }

    public DefaultMutableTreeNode node() {
        return node;
    }
}
