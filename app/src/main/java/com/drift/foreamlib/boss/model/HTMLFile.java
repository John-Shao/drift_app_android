package com.drift.foreamlib.boss.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HTMLFile implements Serializable {
    private static final long serialVersionUID = 186560139728959165L;
    public static final int TYPE_VIDEO = 0;
    public static final int TYPE_PHOTO = 1;
    public static final int TYPE_UNKNOWN = -1;
    private String bigFileUrl;
    private String samllFileUrl;
    private String thmUrl;
    private long size;
    private int type;
    private String name;
    private long createTime;
    private long sortId;
    private int videoResolution;
    private int videoFps;
    private String parentFolderName;
    private String createTimeStr;

    private String keyString;//储存文件对应的key值,用于定位文件

    public String getPhotoType() {
        return photoType;
    }

    public void setPhotoType(String photoType) {
        this.photoType = photoType;
    }

    private String photoType;

    public List<HTMLFile> getList() {
        return list;
    }

    public void setList(List<HTMLFile> list) {
        this.list = list;
    }

    private List<HTMLFile> list;
    private String extendName;

    public String getBigFileUrl() {
        return bigFileUrl;
    }

    public void setBigFileUrl(String bigFileUrl) {
        this.bigFileUrl = bigFileUrl;
    }

    public String getSamllFileUrl() {
        return samllFileUrl;
    }

    public void setSamllFileUrl(String samllFileUrl) {
        this.samllFileUrl = samllFileUrl;
    }

    public String getThmUrl() {
        return thmUrl;
    }

    public void setThmUrl(String thmUrl) {
        this.thmUrl = thmUrl;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getNameWithExtend() {
        return name + "." + extendName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String generateSaveFileName() {
        return name + "_" + size + "." + extendName;
    }

    public String getExtendName() {
        return extendName;
    }

    public void setExtendName(String extendName) {
        this.extendName = extendName;
    }

    public long getSortId() {
        return sortId;
    }

    public void setSortId(long sortId) {
        this.sortId = sortId;
    }

    public long getVideoResolution() {
        return videoResolution;
    }

    public void setVideoResolution(int videoResolution) {
        this.videoResolution = videoResolution;
    }

    public long getVideoFps() {
        return videoFps;
    }

    public void setVideoFps(int videoFps) {
        this.videoFps = videoFps;
    }

    public void setParentFolderName(String str) {
        parentFolderName = str;
    }

    public String getParentFolderName() {
        return parentFolderName;
    }

    public String generateKeyValue() {
        return parentFolderName.substring(0, 3) + "_" + name.substring(3, name.length());
    }

    public String getVideoResolutionString()
    {
        if(videoResolution == -1)
        {
            return "";
        }

        List<String> videoResolutionArray = new ArrayList<String>();
        //@[@"4K",@"4K UHD",@"2.7K",@"1080P",@"720P"];
        videoResolutionArray.add("4K");
        videoResolutionArray.add("4K UHD");
        videoResolutionArray.add("2.7K");
        videoResolutionArray.add("1080P");
        videoResolutionArray.add("720P");
        videoResolutionArray.add("WVGA");

        return videoResolutionArray.get(videoResolution);
    }

    public String getFPSString()
    {
        List<String> videoFPSArray = new ArrayList<String>();
        //@[@"24fps",@"25fps", @"30fps", @"50fps", @"60fps",@"100fps", @"120fps", @"240fps"];
        videoFPSArray.add("24");
        videoFPSArray.add("25");
        videoFPSArray.add("30");
        videoFPSArray.add("50");
        videoFPSArray.add("60");
        videoFPSArray.add("100");
        videoFPSArray.add("120");
        videoFPSArray.add("240");

        return videoFPSArray.get(videoFps);
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getKeyString() {
        return keyString;
    }

    public void setKeyString(String keyString) {
        this.keyString = keyString;
    }
}
