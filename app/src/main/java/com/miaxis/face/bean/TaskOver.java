package com.miaxis.face.bean;

public class TaskOver {

    private String taskid;
    private String tasktype;
    private String taskCode;
    private String taskMsg;
    private String taskdata;

    public TaskOver() {
    }

    private TaskOver(Builder builder) {
        setTaskid(builder.taskid);
        setTasktype(builder.tasktype);
        setTaskCode(builder.taskCode);
        setTaskMsg(builder.taskMsg);
        setTaskdata(builder.taskdata);
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getTasktype() {
        return tasktype;
    }

    public void setTasktype(String tasktype) {
        this.tasktype = tasktype;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getTaskMsg() {
        return taskMsg;
    }

    public void setTaskMsg(String taskMsg) {
        this.taskMsg = taskMsg;
    }

    public String getTaskdata() {
        return taskdata;
    }

    public void setTaskdata(String taskdata) {
        this.taskdata = taskdata;
    }

    public static final class Builder {
        private String taskid;
        private String tasktype;
        private String taskCode;
        private String taskMsg;
        private String taskdata;

        public Builder() {
        }

        public Builder taskid(String val) {
            taskid = val;
            return this;
        }

        public Builder tasktype(String val) {
            tasktype = val;
            return this;
        }

        public Builder taskCode(String val) {
            taskCode = val;
            return this;
        }

        public Builder taskMsg(String val) {
            taskMsg = val;
            return this;
        }

        public Builder taskdata(String val) {
            taskdata = val;
            return this;
        }

        public TaskOver build() {
            return new TaskOver(this);
        }
    }
}
