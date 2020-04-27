package com.miaxis.face.bean;

public class TaskResult {

    private String code;
    private String message;
    private String taskData;

    public TaskResult() {
    }

    public TaskResult(String code, String message, String taskData) {
        this.code = code;
        this.message = message;
        this.taskData = taskData;
    }

    private TaskResult(Builder builder) {
        setCode(builder.code);
        setMessage(builder.message);
        setTaskData(builder.taskData);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTaskData() {
        return taskData;
    }

    public void setTaskData(String taskData) {
        this.taskData = taskData;
    }

    public static final class Builder {
        private String code;
        private String message;
        private String taskData;

        public Builder() {
        }

        public Builder code(String val) {
            code = val;
            return this;
        }

        public Builder message(String val) {
            message = val;
            return this;
        }

        public Builder taskData(String val) {
            taskData = val;
            return this;
        }

        public TaskResult build() {
            return new TaskResult(this);
        }
    }
}
