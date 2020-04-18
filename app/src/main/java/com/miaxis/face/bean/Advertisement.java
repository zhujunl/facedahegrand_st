package com.miaxis.face.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class Advertisement {

    @Id
    private Long id;
    private String title;
    private String url;
    private int delayTime;
    private String startDate;
    private String endDate;
    @Generated(hash = 315460207)
    public Advertisement(Long id, String title, String url, int delayTime,
            String startDate, String endDate) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.delayTime = delayTime;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    @Generated(hash = 199755335)
    public Advertisement() {
    }

    private Advertisement(Builder builder) {
        setId(builder.id);
        setTitle(builder.title);
        setUrl(builder.url);
        setDelayTime(builder.delayTime);
        setStartDate(builder.startDate);
        setEndDate(builder.endDate);
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public int getDelayTime() {
        return this.delayTime;
    }
    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }
    public String getStartDate() {
        return this.startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public String getEndDate() {
        return this.endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public static final class Builder {
        private Long id;
        private String title;
        private String url;
        private int delayTime;
        private String startDate;
        private String endDate;

        public Builder() {
        }

        public Builder id(Long val) {
            id = val;
            return this;
        }

        public Builder title(String val) {
            title = val;
            return this;
        }

        public Builder url(String val) {
            url = val;
            return this;
        }

        public Builder delayTime(int val) {
            delayTime = val;
            return this;
        }

        public Builder startDate(String val) {
            startDate = val;
            return this;
        }

        public Builder endDate(String val) {
            endDate = val;
            return this;
        }

        public Advertisement build() {
            return new Advertisement(this);
        }
    }
}
