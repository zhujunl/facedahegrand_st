package com.miaxis.face.manager;

import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.view.TextureView;

import com.miaxis.face.app.App;
import com.miaxis.face.constant.Constants;

import java.util.List;

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

    public static int PRE_WIDTH = 640;
    public static int PRE_HEIGHT = 480;
    public static int PIC_WIDTH = 640;
    public static int PIC_HEIGHT = 480;
    public static int ORIENTATION = 180;
    private static final int RETRY_TIMES = 3;

    private  Camera camera;
    private  SurfaceTexture surfaceTexture = null;
    private int retryTime = 0;

    private OnCameraOpenListener listener;
    private volatile boolean monitorFlag = false;
    private long lastCameraCallBackTime;
    private MonitorThread monitorThread;

    public synchronized void openCamera(@NonNull TextureView textureView, @NonNull CameraManager.OnCameraOpenListener listener) {
        try {
            this.listener = listener;
            if (surfaceTexture == null) {
                textureView.setSurfaceTextureListener(textureListener);
            }

            openMonitor();
            resetRetryTime();
//            textureViewFlip(textureView);
            openVisibleCamera();
            if (surfaceTexture != null) {
                camera.setPreviewTexture(surfaceTexture);
            }
            textureView.setRotationY(CameraManager.ORIENTATION);
            listener.onCameraOpen(camera.getParameters().getPreviewSize(), "");
//            if (surfaceTexture == null) {
//                textureView.setSurfaceTextureListener(textureListener);
//            } else {
//                camera.setPreviewTexture(surfaceTexture);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.onCameraOpen(null, "异常: "+e);
        }
    }

    private void openVisibleCamera() {
//        try {
        for (int i = 0; i < RETRY_TIMES; i++) {
            if (camera==null){
                try {
                    camera = Camera.open(0);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (camera!=null){
                    break;
                }
                SystemClock.sleep(500);
            }
        }
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        int maxWidth = 0;
        int maxHeight = 0;
        for (Camera.Size size : supportedPreviewSizes) {
            maxWidth = Math.max(size.width, maxWidth);
            maxHeight = Math.max(size.height, maxHeight);
        }
        ORIENTATION = maxWidth * maxHeight >= (200 * 10000) ? 0 : (!Constants.VERSION?0:180);//处理摄像头，500W不需要旋转
        if(ORIENTATION==0){
            PRE_WIDTH = 800;
            PRE_HEIGHT = 600;
            PIC_WIDTH = 800;
            PIC_HEIGHT = 600;
        }
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
        try {
            camera.setParameters(parameters);
        }catch (Exception e){
            e.printStackTrace();
        }
        camera.setDisplayOrientation(ORIENTATION);
        camera.setPreviewCallback(visiblePreviewCallback);
        camera.startPreview();
//        } catch (Exception e) {
//            e.printStackTrace();
////            new Thread(() -> {
////                if (retryTime <= RETRY_TIMES) {
////                    retryTime++;
////                    openVisibleCamera();
////                }
////            }).start();
//        }
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

    public TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture st, int width, int height) {
            try {
                surfaceTexture = st;
                if (camera!=null){
                    camera.setPreviewTexture(surfaceTexture);
                }
            } catch (Exception e) {
                e.printStackTrace();
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

    private final Camera.PreviewCallback visiblePreviewCallback = (data, camera) -> {
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
                        if ((cur - lastCameraCallBackTime) >= ConfigManager.getInstance().getConfig().getIntervalTime() * 1000L) {
                            if (listener != null) {
                                if (!Constants.VERSION) {
                                    App.getInstance().sendBroadcast(Constants.MOLD_POWER,Constants.TYPE_CAMERA, true);
                                    SystemClock.sleep(1500);
                                }
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

}
