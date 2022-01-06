package com.miaxis.face.manager;

import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.SystemClock;
import android.util.Log;
import android.view.TextureView;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;

import com.miaxis.face.constant.Constants;

public class CameraManager {

    private CameraManager() {
    }

    public static CameraManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final CameraManager instance = new CameraManager();
    }

    /**
     * ================================ 静态内部类单例 ================================
     **/

    public static final int PRE_WIDTH = Constants.VERSION?640:640;
    public static final int PRE_HEIGHT = Constants.VERSION?480:480;
    public static final int PIC_WIDTH = Constants.VERSION?640:640;
    public static final int PIC_HEIGHT =Constants.VERSION?480:480;
    public static int ORIENTATION = 180;
    private static final int RETRY_TIMES = 3;

    private static Camera camera;
    private static SurfaceTexture surfaceTexture = null;
    private int retryTime = 0;

    private OnCameraOpenListener listener;
    private volatile boolean monitorFlag = false;
    private long lastCameraCallBackTime;
    private MonitorThread monitorThread;

    public synchronized void openCamera(@NonNull TextureView textureView, @NonNull CameraManager.OnCameraOpenListener listener) {
        try {
            this.listener = listener;
            openMonitor();
            resetRetryTime();
//            textureViewFlip(textureView);
            openVisibleCamera();
            listener.onCameraOpen(camera.getParameters().getPreviewSize(), "");
            if (surfaceTexture == null) {
                textureView.setSurfaceTextureListener(textureListener);
            } else {
                camera.setPreviewTexture(surfaceTexture);
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.onCameraOpen(null, "");
        }
    }

    private void openVisibleCamera() {
        try {
            for (int i = 0; i < RETRY_TIMES; i++) {
                if (camera==null){
                    camera = Camera.open(0);
                    if (camera!=null){
                        break;
                    }
                    SystemClock.sleep(100);
                }
            }
            Camera.Parameters parameters = camera.getParameters();

            List<Camera.Size> pic=parameters.getSupportedPictureSizes();
            List<Camera.Size> p=parameters.getSupportedPreviewSizes();
            parameters.setPreviewSize(PRE_WIDTH, PRE_HEIGHT);
            parameters.setPictureSize(PIC_WIDTH, PIC_HEIGHT);
            //对焦模式设置
            List<String> supportedFocusModes = parameters.getSupportedFocusModes();
            if (supportedFocusModes != null && supportedFocusModes.size() > 0) {
                if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }
            }
            camera.setParameters(parameters);
            camera.setDisplayOrientation(ORIENTATION);
            camera.setPreviewCallback(visiblePreviewCallback);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
//            new Thread(() -> {
//                if (retryTime <= RETRY_TIMES) {
//                    retryTime++;
//                    openVisibleCamera();
//                }
//            }).start();
        }
    }

    public void closeCamera() {
        try {
            closeMonitor();
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
//            new Thread(() -> {
//                if (retryTime <= RETRY_TIMES) {
//                    retryTime++;
//                    closeCamera();
//                }
//            }).start();
        }
    }

    public Camera getCamera() {
        return camera;
    }

    private void textureViewFlip(TextureView textureView) {
        Matrix matrix = textureView.getTransform(new Matrix());
        matrix.setScale(-1, 1);
        int width = textureView.getWidth();
        matrix.postTranslate(width, 0);
        textureView.setTransform(matrix);
    }

    private void resetRetryTime() {
        this.retryTime = 0;
    }

    private TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture st, int width, int height) {
            if (camera != null) {
                try {
                    surfaceTexture = st;
                    camera.setPreviewTexture(surfaceTexture);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
//            closeCamera();
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }
    };

    private Camera.PreviewCallback visiblePreviewCallback = (data, camera) -> {
        lastCameraCallBackTime = System.currentTimeMillis();
        FaceManager.getInstance().setLastVisiblePreviewData(data);
    };

    public interface OnCameraOpenListener {
        void onCameraOpen(Camera.Size previewSize, String message);
        void onCameraError();
    }

    private class MonitorThread extends Thread {
        @Override
        public void run() {
            lastCameraCallBackTime = System.currentTimeMillis();
            while (!interrupted()) {
                try {
                    Thread.sleep(1000);
                    if (monitorFlag) {
                        long cur = System.currentTimeMillis();
                        if ((cur - lastCameraCallBackTime) >= ConfigManager.getInstance().getConfig().getIntervalTime() * 1000) {
                            if (listener != null) {
                                listener.onCameraError();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void openMonitor() {
        lastCameraCallBackTime = System.currentTimeMillis();
        monitorFlag = true;
        if (monitorThread == null) {
            monitorThread = new MonitorThread();
            monitorThread.start();
        }
    }

    private void closeMonitor() {
        monitorFlag = false;
    }

    public static String  setORIENTATION(){
        try {
            for (int i = 0; i < RETRY_TIMES; i++) {
                if (camera==null){
                    camera = Camera.open(0);
                    if (camera!=null){
                        break;
                    }
                    SystemClock.sleep(100);
                }
            }
            if (camera!=null){
                Camera.Parameters parameters = camera.getParameters();
                for (int i=0;i<parameters.getSupportedPictureSizes().size();i++){
                    Log.d("Camera_edPrevi()",i+"="+parameters.getSupportedPictureSizes().get(i).height+"*"+parameters.getSupportedPictureSizes().get(i).width+"="+parameters.getSupportedPictureSizes().get(i).height*parameters.getSupportedPictureSizes().get(i).width);
                    if(parameters.getSupportedPreviewSizes().get(i).width*parameters.getSupportedPreviewSizes().get(i).height>307200){
                        ORIENTATION=0;
                        break;
                    }
                }
                return null;
            }else {
                return "摄像头打开失败";
            }
        }catch (Exception e){
            e.printStackTrace();
            return "摄像头打开失败";
        }

    }

    public static void setSurfaceTexture(){
        surfaceTexture=null;
    }
}
