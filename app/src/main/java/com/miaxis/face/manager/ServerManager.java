package com.miaxis.face.manager;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miaxis.face.app.App;
import com.miaxis.face.bean.ResponseEntity;
import com.miaxis.face.bean.Task;
import com.miaxis.face.net.NanoHTTPD;
import com.miaxis.face.util.DeviceUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public String getHost() {
        return "http://" + ip + ":" + port + "/api/miaxis/taskServer";
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

    private class TaskServer extends NanoHTTPD {

        private volatile boolean serviceDoing = false;

        public TaskServer(int port) {
            super(port);
        }

        @Override
        public Response serve(IHTTPSession session) {
            try {
                if (NanoHTTPD.Method.POST.equals(session.getMethod()) && session.getUri().startsWith("/api/miaxis/taskServer")) {
                    if (serviceDoing) {
                        return newFixedLengthResponse(new Gson().toJson(new ResponseEntity("400", "service busy")));
                    } else {
                        Task task = parseTask(session);
                        if (task != null) {
                            if (listener != null) {
                                serviceDoing = true;
                                listener.onTask(task);
                                return newFixedLengthResponse(new Gson().toJson(new ResponseEntity("200", "task accept")));
                            } else {
                                return newFixedLengthResponse(new Gson().toJson(new ResponseEntity("400", "refuse request")));
                            }
                        } else {
                            return newFixedLengthResponse(new Gson().toJson(new ResponseEntity("400", "missing task")));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return newFixedLengthResponse(new Gson().toJson(new ResponseEntity("400", "parse request error")));
            }
            return newFixedLengthResponse(new Gson().toJson(new ResponseEntity("400", "bad way")));
        }

        private Task parseTask(IHTTPSession session) throws Exception {
            Map<String, String> files = new HashMap<>();
            session.parseBody(files);
            Map<String, List<String>> parameters = session.getParameters();
            if (parameters != null && parameters.get("data") != null) {
                Task task = GSON.fromJson(parameters.get("data").get(0), new TypeToken<Task>() {}.getType());
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
            RecordManager.getInstance().uploadHeartBeat(getHost());
        } catch (Exception e) {
            Log.e("asd", "" + e.getMessage());
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
