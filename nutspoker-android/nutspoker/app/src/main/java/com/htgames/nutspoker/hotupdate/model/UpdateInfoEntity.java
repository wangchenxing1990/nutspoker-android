package com.htgames.nutspoker.hotupdate.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 更新的相关信息
 */
public class UpdateInfoEntity implements Serializable {
    public String oldVersion;
    public String newVersion;
    public String updateInfo;
    public ArrayList<UpdateFileEntity> diffFileList;
}
