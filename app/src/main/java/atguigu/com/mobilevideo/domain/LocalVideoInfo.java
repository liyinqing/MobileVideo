package atguigu.com.mobilevideo.domain;

import java.io.Serializable;

/**
 * 作者：李银庆 on 2017/5/21 14:29
 */
public class LocalVideoInfo implements Serializable {
    private String coverImg;
    private  String duration1;
    private String name;
    private long duration;
    private long size;
    private String data;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    private String artist;
    public LocalVideoInfo(String name, long duration, long size, String data,String artist) {
        this.name = name;
        this.duration = duration;
        this.size = size;
        this.data = data;
        this.artist = artist;
    }

    public LocalVideoInfo(String name, long duration, long size, String data) {
        this.name = name;
        this.duration = duration;
        this.size = size;
        this.data = data;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public String getDuration1() {
        return duration1;
    }

    public void setDuration1(String duration1) {
        this.duration1 = duration1;
    }

    public LocalVideoInfo(String name, String duration1, long videolength, String url, String coverImg) {
        this.name = name;
        this.duration1 = duration1;
        this.size =  videolength;
        this.data = url;
        this.coverImg = coverImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "domain{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", data='" + data + '\'' +
                '}';
    }
}
