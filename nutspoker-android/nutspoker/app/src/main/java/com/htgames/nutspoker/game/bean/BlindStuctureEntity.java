package com.htgames.nutspoker.game.bean;

import java.io.Serializable;

/**
 * 盲注结构
 */
public class BlindStuctureEntity implements Serializable {
    private int level;
    private int sblinds;
    private int ante = 0;//前注(MTT使用)

    public BlindStuctureEntity() {
    }

    public BlindStuctureEntity(int level, int sblinds) {
        this.level = level;
        this.sblinds = sblinds;
    }

    public BlindStuctureEntity(int level, int sblinds , int ante) {
        this.level = level;
        this.sblinds = sblinds;
        this.ante = ante;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getSblinds() {
        return sblinds;
    }

    public void setSblinds(int sblinds) {
        this.sblinds = sblinds;
    }

    public int getAnte() {
        return ante;
    }

    public void setAnte(int ante) {
        this.ante = ante;
    }
}
