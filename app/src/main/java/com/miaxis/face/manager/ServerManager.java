package com.miaxis.face.manager;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miaxis.face.app.App;
import com.miaxis.face.bean.Config;
import com.miaxis.face.bean.DaheResponseEntity;
import com.miaxis.face.bean.ResponseEntity;
import com.miaxis.face.bean.Task;
import com.miaxis.face.bean.TaskOver;
import com.miaxis.face.bean.TaskResult;
import com.miaxis.face.net.FaceNetApi;
import com.miaxis.face.net.NanoHTTPD;
import com.miaxis.face.util.DeviceUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class ServerManager {

    private ServerManager() {
    }

    public static ServerManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final ServerManager instance = new ServerManager();
    }

    /**
     * ================================ 静态内部类单例 ================================
     **/

    private static final Gson GSON = new Gson();

    private HandlerThread handlerThread;
    private Handler handler;

    private OnTaskHandleListener listener;

    private String ip;
    private int port = 21638;
    private TaskServer taskServer;

    public String getAddress() {
        return "http://" + ip + ":" + port + "/api/miaxis/taskServer";
    }

    public String getHost() {
        return ip + ":" + port;
    }

    public int getPort() {
        return port;
    }

    public void stopServer() {
        if (taskServer != null) {
            taskServer.stop();
        }
    }

    public void startServer(int port, OnServerInitListener initListener) {
        try {
            ip = DeviceUtil.getIP(App.getInstance());
            this.port = port;
            taskServer = new TaskServer(port);
            taskServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            initListener.onServerInit();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("asd", "Nano服务器开启失败");
            startServer(++port, initListener);
        }
    }

    public void setListener(OnTaskHandleListener listener) {
        this.listener = listener;
    }

    public interface OnServerInitListener {
        void onServerInit();
    }

    public interface OnTaskHandleListener {
        void onTask(Task task);
    }

    public void onTaskOver(Task task, TaskResult taskResult) {
        if (taskServer != null) {
            taskServer.taskOver(task, taskResult);
        }
    }

    private class TaskServer extends NanoHTTPD {

        private volatile boolean serviceDoing = false;
        private volatile boolean done = false;
        private String response = "";
        private IHTTPSession sessionCache;
        private Task taskCache;

        public TaskServer(int port) {
            super(port);
        }

        @Override
        public Response serve(IHTTPSession session) {
            try {
                if (NanoHTTPD.Method.POST.equals(session.getMethod()) && session.getUri().startsWith("/api/miaxis/taskServer")) {
                    if (serviceDoing) {
                        return newFixedLengthResponse(makeErrorBackTaskOver(null, "400", "服务正忙，请等待上一个指令结束"));
                    } else {
                        Task task = parseTask(session);
                        if (task != null) {
                            if (listener != null) {
                                sessionCache = session;
                                taskCache = task;
                                response = "";
                                done = false;
                                serviceDoing = true;
                                listener.onTask(task);
                                int time = 75;
                                while (!done) {
                                    Thread.sleep(200);
                                    time--;
                                    if (time == 0) {
                                        serviceDoing = false;
                                        return newFixedLengthResponse(makeErrorBackTaskOver(task, "400", "任务超时"));
                                    }
                                }
                                String returnResponse = response;
                                done = false;
                                serviceDoing = false;
                                taskCache = null;
                                sessionCache = null;
                                return newFixedLengthResponse(returnResponse);
                            } else {
                                return newFixedLengthResponse(makeErrorBackTaskOver(task, "400", "当前页面并非核验页面"));
                            }
                        } else {
                            return newFixedLengthResponse(makeErrorBackTaskOver(null, "400", "解析任务数据错误或为空"));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return newFixedLengthResponse(makeErrorBackTaskOver(null, "400", "服务器错误"));
            }
            return newFixedLengthResponse(makeErrorBackTaskOver(null, "400", "错误路径"));
        }

        void taskOver(Task task, TaskResult taskResult) {
            response = makeTaskOver(task, taskResult);
            if (!TextUtils.isEmpty(response)) {
                done = true;
            }
        }

        private String makeTaskOver(Task task, TaskResult taskResult) {
            TaskOver taskOver = new TaskOver.Builder()
                    .taskid(task.getTaskid())
                    .tasktype(task.getTasktype())
                    .taskCode(taskResult.getCode())
                    .taskMsg(taskResult.getMessage())
                    .taskdata(taskResult.getTaskData())
                    .build();
            return GSON.toJson(taskOver);
        }

        private String makeErrorBackTaskOver(Task task, String code, String message) {
            TaskOver taskOver = new TaskOver.Builder()
                    .taskid(task != null ? task.getTaskid() : "")
                    .tasktype(task != null ? task.getTasktype() : "")
                    .taskCode(code)
                    .taskMsg(message)
                    .taskdata("")
                    .build();
            return GSON.toJson(taskOver);
        }

        private Task parseTask(IHTTPSession session) throws Exception {
            Map<String, String> files = new HashMap<>();
            session.parseBody(files);
            Map<String, List<String>> parameters = session.getParameters();
            if (parameters != null && parameters.get("data") != null) {
                Task task = GSON.fromJson(parameters.get("data").get(0), new TypeToken<Task>() {
                }.getType());
                if (checkTaskParam(task)) {
                    return task;
                }
            }
            return null;
        }

        private boolean checkTaskParam(Task task) {
            if (TextUtils.equals(task.getTasktype(), "1001")) {
                return true;
            } else if (TextUtils.equals(task.getTasktype(), "1002")) {
                return !TextUtils.isEmpty(task.getTaskparam());
            }
            return false;
        }

        @Override
        public void stop() {
            super.stop();
        }
    }

    public void startHeartBeat() {
        handlerThread = new HandlerThread("HeartBeat");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                heartBeat();
            }
        };
        handler.sendMessage(handler.obtainMessage(0));
    }

    public void stopHeartBeat() {
        if (handler != null) {
            handler.removeMessages(0);
            handler = null;
        }
        if (handlerThread != null) {
            handlerThread.quitSafely();
        }
    }

    private void heartBeat() {
        try {
            handler.removeMessages(0);
            String data = "{\"webapi\": \"" + getHost() + "\"}";
            RecordManager.getInstance().uploadHeartBeat(data);
//            Config config = ConfigManager.getInstance().getConfig();
//            String json = "{\"webapi\": \"" + getAddress() + "\"}";
//            Call<DaheResponseEntity> uploadCall = FaceNetApi.uploadRecord(config.getUploadRecordUrl(), json);
//            Response<DaheResponseEntity> execute = uploadCall.execute();
//            DaheResponseEntity body = execute.body();
//            if (body != null) {
//                Log.e("asd", "心跳回执：" + body.getErrCode() + "，Msg:" + body.getErrMsg());
//            }
        } catch (Exception e) {
            Log.e("asd", "" + e.getMessage());
        } finally {
            prepareForNextHeartBeat();
        }
    }

    private void prepareForNextHeartBeat() {
        if (handler != null) {
            Message message = handler.obtainMessage(0);
            handler.sendMessageDelayed(message, 10 * 1000);
        }
    }

}
