package com.htgames.nutspoker.util.word;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class WordNode {
    private int value;
    private List<WordNode> subNodes;
    private boolean isLast;

    public WordNode(int value, boolean isLast) {
        this.value = value;
        this.isLast = isLast;
    }

    private WordNode addSubNode(WordNode subNode) {
        if(this.subNodes == null) {
            this.subNodes = new LinkedList();
        }

        this.subNodes.add(subNode);
        return subNode;
    }

    public WordNode addIfNoExist(int value, boolean isLast) {
        if(this.subNodes == null) {
            return this.addSubNode(new WordNode(value, isLast));
        } else {
            Iterator var4 = this.subNodes.iterator();

            while(var4.hasNext()) {
                WordNode subNode = (WordNode)var4.next();
                if(subNode.value == value) {
                    if(!subNode.isLast && isLast) {
                        subNode.isLast = true;
                    }

                    return subNode;
                }
            }

            return this.addSubNode(new WordNode(value, isLast));
        }
    }

    public WordNode querySub(int value) {
        if(this.subNodes == null) {
            return null;
        } else {
            Iterator var3 = this.subNodes.iterator();

            while(var3.hasNext()) {
                WordNode subNode = (WordNode)var3.next();
                if(subNode.value == value) {
                    return subNode;
                }
            }

            return null;
        }
    }

    public boolean isLast() {
        return this.isLast;
    }

    public void setLast(boolean isLast) {
        this.isLast = isLast;
    }

    public int hashCode() {
        return this.value;
    }
}
