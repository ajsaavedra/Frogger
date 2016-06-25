package com.tonyjs.frogger;

/**
 * Created by tonysaavedra on 6/24/16.
 */
public class Tree {
    private Sprite tree;

    public Tree(String filePath) {
        this.tree = new Sprite();
        this.tree.setImage(filePath);
    }

    public Sprite getTree() {
        return tree;
    }
}
