package com.miaxis.face.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;

import com.miaxis.face.bean.Config;
import com.miaxis.face.bean.Intermediary;
import com.miaxis.face.bean.MxRGBImage;
import com.miaxis.face.bean.PhotoFaceFeature;
import com.miaxis.face.exception.MyException;
import com.miaxis.face.util.FileUtil;
import com.miaxis.image.MXImage;
import com.miaxis.image.MXImages;
import com.miaxis.livedetect.jni.MXLiveDetectApi;
import com.miaxis.livedetect.jni.vo.FaceInfo;
import com.miaxis.livedetect.jni.vo.FaceQuality;

import org.zz.api.MXFaceAPI;
import org.zz.api.MXFaceInfoEx;
import org.zz.jni.mxImageTool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

public class FaceManager {

    private FaceManager() {
    }

    public static FaceManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final FaceManager instance = new FaceManager();
    }

    /**
     * ================================ 静态内部类单例 ================================
     **/

    public static final int ERR_LICENCE = -2009;
    public static final int ERR_FILE_COMPARE = -101;
    public static final int INIT_SUCCESS = 0;
    public static int zoomWidth = 320;
    public static int zoomHeight = 240;

    private static final int MAX_FACE_NUM = 50;
    private static final Byte lock1 = 1;
    private static final Byte lock2 = 2;

    private MXFaceAPI mxFaceAPI;
    private MXLiveDetectApi mxLiveDetectApi;
    private mxImageTool dtTool;

    private HandlerThread asyncDetectThread;
    private Handler asyncDetectHandler;
    private volatile boolean detectLoop = true;
    private HandlerThread asyncExtractThread;
    private Handler asyncExtractHandler;
    private volatile boolean extractLoop = true;

    private volatile boolean needNextFeature = false;
    private volatile boolean nova = false;
    private volatile Intermediary intermediaryData;

    private byte[] actionLiveImageData;
    private int actionLiveImageQuality = 0;
    private volatile boolean actionLiveResult = false;

    private byte[] lastVisiblePreviewData;

    private OnFaceHandleListener faceHandleListener;

    public interface OnFaceHandleListener {
        void onFeatureExtract(MxRGBImage mxRGBImage, MXFaceInfoEx mxFaceInfoEx, byte[] feature);

        void onFaceDetect(int faceNum, MXFaceInfoEx[] faceInfoExes);

        void onFaceIntercept(int code, String message);

        void onActionLiveDetect(int code, String message);
    }

    public void startLoop() {
        detectLoop = true;
        extractLoop = true;
        actionLiveResult = false;
        actionLiveImageQuality = 0;
        actionLiveImageData = null;
        lastVisiblePreviewData = null;
        intermediaryData = null;
        needNextFeature = true;
        asyncDetectHandler.sendEmptyMessage(0);
        asyncExtractHandler.sendEmptyMessage(0);
    }

    public void stopLoop() {
        detectLoop = false;
        extractLoop = false;
        actionLiveResult = false;
        actionLiveImageQuality = 0;
        actionLiveImageData = null;
        lastVisiblePreviewData = null;
        asyncDetectHandler.removeMessages(0);
        asyncExtractHandler.removeMessages(0);
    }

    public void setNeedNextFeature(boolean needNextFeature) {
        this.needNextFeature = needNextFeature;
    }

    public void setLastVisiblePreviewData(byte[] lastVisiblePreviewData) {
        this.lastVisiblePreviewData = lastVisiblePreviewData;
    }

    public void setFaceHandleListener(OnFaceHandleListener faceHandleListener) {
        this.faceHandleListener = faceHandleListener;
    }

    private void previewDataLoop() {
        try {
            if (this.lastVisiblePreviewData != null) {
                Config config = ConfigManager.getInstance().getConfig();
                if (!actionLiveResult && config.getLivenessFlag()) {
                    actionLiveDetect(lastVisiblePreviewData);
                } else {
                    verify(lastVisiblePreviewData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            asyncDetectHandler.sendEmptyMessage(0);
        }
    }

    private void verify(byte[] detectData) throws Exception {
        byte[] zoomedRgbData = cameraPreviewConvert(detectData,
                CameraManager.PRE_WIDTH,
                CameraManager.PRE_HEIGHT,
                CameraManager.ORIENTATION,
                zoomWidth,
                zoomHeight);
        if (zoomedRgbData == null) {
            if (faceHandleListener != null) {
                faceHandleListener.onFaceDetect(0, null);
            }
            throw new MyException("数据转码失败");
        }
        int[] faceNum = new int[]{MAX_FACE_NUM};
        MXFaceInfoEx[] faceBuffer = makeFaceContainer(faceNum[0]);
        boolean result = faceDetect(zoomedRgbData, zoomWidth, zoomHeight, faceNum, faceBuffer);
        if (result) {
            if (faceHandleListener != null) {
                faceHandleListener.onFaceDetect(faceNum[0], faceBuffer);
            }
            MXFaceInfoEx mxFaceInfoEx = sortMXFaceInfoEx(faceBuffer);
            result = faceQuality(zoomedRgbData, zoomWidth, zoomHeight, 1, new MXFaceInfoEx[]{mxFaceInfoEx});
            if (result) {
                Intermediary intermediary = new Intermediary();
                intermediary.width = zoomWidth;
                intermediary.height = zoomHeight;
                intermediary.mxFaceInfoEx = new MXFaceInfoEx(mxFaceInfoEx);
                intermediary.data = zoomedRgbData;
                intermediaryData = intermediary;
                nova = true;
//              Log.e("asd", "检测耗时" + (System.currentTimeMillis() - time) + "-----" + mxFaceInfoEx.quality);
            }
        } else {
            if (faceHandleListener != null) {
                faceHandleListener.onFaceDetect(0, null);
            }
        }
    }

    private void actionLiveDetect(byte[] detectData) throws Exception {
        Log.e("asd", "进入活体");
        MXImage original = new MXImage(detectData, CameraManager.PRE_WIDTH, CameraManager.PRE_HEIGHT, MXImage.FORMAT_YUV);
//        mxImage = MXImages.crop(original, new Rect(140, 90, 640 - 120, 480 - 90));
        MXImage mxImage = MXImages.rotate(original, 180);
        mxImage = MXImages.yuv2BGR(mxImage);
        mxImage = MXImages.scale(mxImage, 0.5f);
        mxImage.setTag(original);
        List<FaceInfo> faceInfoList = mxLiveDetectApi.faceDetect(mxImage);
        if (faceInfoList != null && !faceInfoList.isEmpty()) {
            FaceInfo faceInfo = faceInfoList.get(0);
            int maxFaceLength = Math.abs(faceInfo.getArea().right - faceInfo.getArea().left);
            for (FaceInfo v : faceInfoList) {
                int faceLength = Math.abs(v.getArea().right - v.getArea().left);
                if (faceLength > maxFaceLength) {
                    maxFaceLength = faceLength;
                    faceInfo = v;
                }
            }
            FaceQuality quality = faceInfo.getQuality();
            if (quality.eyeDistance >= 70) {
                if (faceHandleListener != null) {
                    faceHandleListener.onActionLiveDetect(-1, "瞳距过大");
                    return;
                }
            } else if (quality.eyeDistance <= 25) {
                if (faceHandleListener != null) {
                    faceHandleListener.onActionLiveDetect(-2, "瞳距过小");
                    return;
                }
            } else if (quality.quality <= ConfigManager.getInstance().getConfig().getLivenessQualityScore()) {
                if (faceHandleListener != null) {
                    faceHandleListener.onActionLiveDetect(-3, "质量过低");
                    return;
                }
            }
            int result = mxLiveDetectApi.blinkDetect(mxImage, faceInfo);
            if (result == 1) {
                actionLiveResult = true;
                if (actionLiveImageData == null) {
                    actionLiveImageData = detectData;
                }
                verify(actionLiveImageData);
                if (faceHandleListener != null) {
                    faceHandleListener.onActionLiveDetect(1, "眨眼检测通过");
                }
            } else {
                if (quality.quality > actionLiveImageQuality) {
                    actionLiveImageQuality = quality.quality;
                    actionLiveImageData = detectData;
                }
                if (faceHandleListener != null) {
                    faceHandleListener.onActionLiveDetect(-5, "眨眼检测失败");
                }
            }
        } else {
            if (faceHandleListener != null) {
                faceHandleListener.onActionLiveDetect(-4, "未检测到人脸");
            }
        }
    }

    private void intermediaryDataLoop() {
        try {
            if (nova && intermediaryData != null) {
                nova = false;
                extract(intermediaryData);
                intermediaryData = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            asyncExtractHandler.sendEmptyMessage(0);
        }
    }

    private void extract(Intermediary intermediary) throws Exception {
        if (needNextFeature) {
            Log.e("asd", "提特征中");
            Config config = ConfigManager.getInstance().getConfig();
            Log.d("quality===","="+intermediary.mxFaceInfoEx.quality);
            Log.d("getquality===","="+config.getQualityScore());
            if (intermediary.mxFaceInfoEx.quality > config.getQualityScore()) {
                byte[] feature = extractFeature(intermediary.data, zoomWidth, zoomHeight, intermediary.mxFaceInfoEx);
                int i=intermediary.mxFaceInfoEx.pitch;
                int q= intermediary.mxFaceInfoEx.yaw;
                int w=intermediary.mxFaceInfoEx.roll;
                if (i>20||q>15||w>15){
                    Log.e("lianglian", "extract: true" );
                }else {
                    Log.e("lianglian", "extract: false" );
                }
                if (feature != null) {
                    needNextFeature = false;
                    if (faceHandleListener != null) {
                        faceHandleListener.onFeatureExtract(new MxRGBImage(intermediary.data, zoomWidth, zoomHeight),
                                intermediary.mxFaceInfoEx,
                                feature);
                    }
                }
            } else {
                if (faceHandleListener != null) {
                    faceHandleListener.onFaceIntercept(-1, "质量阈值拦截");
                }
            }
        }
    }

    public PhotoFaceFeature getCardFaceFeatureByBitmapPosting(Bitmap bitmap) {
        String message = "";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bitmap.getByteCount());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] rgbData = imageFileDecode(outputStream.toByteArray(), bitmap.getWidth(), bitmap.getHeight());
        if (rgbData == null) {
            message = "图片转码失败";
            return new PhotoFaceFeature(message);
        }
        int[] pFaceNum = new int[]{0};
        MXFaceInfoEx[] pFaceBuffer = makeFaceContainer(MAX_FACE_NUM);
        boolean result = faceDetect(rgbData, bitmap.getWidth(), bitmap.getHeight(), pFaceNum, pFaceBuffer);
        if (result && pFaceNum[0] > 0) {
            MXFaceInfoEx mxFaceInfoEx = sortMXFaceInfoEx(pFaceBuffer);
            byte[] faceFeature = extractFeature(rgbData, bitmap.getWidth(), bitmap.getHeight(), mxFaceInfoEx);
            if (faceFeature != null) {
                return new PhotoFaceFeature(faceFeature, "提取成功");
            } else {
                message = "提取特征失败";
            }
        } else {
            message = "未检测到人脸";
        }
        return new PhotoFaceFeature(message);
    }

    /* =============================================================================================================== */

    /**
     * 初始化人脸算法
     *
     * @param context 设备上下文
     * @return 状态码
     */
    public int initFaceST(Context context) {
        final String sLicence = FileUtil.readLicence();
        if (TextUtils.isEmpty(sLicence)) {
            return ERR_LICENCE;
        }
        mxFaceAPI = new MXFaceAPI();
        mxLiveDetectApi = MXLiveDetectApi.INSTANCE;
        dtTool = new mxImageTool();
        int re = initFaceModel(context);
        if (re == 0) {
            re = mxFaceAPI.mxInitAlg(context, FileUtil.getFaceModelPath(), sLicence);
        }
        if (re == 0) {
            re = mxLiveDetectApi.initialize(FileUtil.getFaceModelPath());
        }
        initThread();
        return re;
    }

    private void initThread() {
        asyncDetectThread = new HandlerThread("detect_thread");
        asyncDetectThread.start();
        asyncDetectHandler = new Handler(asyncDetectThread.getLooper()) {
            public void handleMessage(Message msg) {
                if (detectLoop) {
                    try {
                        previewDataLoop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        asyncExtractThread = new HandlerThread("extract_thread");
        asyncExtractThread.start();
        asyncExtractHandler = new Handler(asyncExtractThread.getLooper()) {
            public void handleMessage(Message msg) {
                if (extractLoop) {
                    try {
                        intermediaryDataLoop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    /**
     * 拷贝人脸模型文件
     *
     * @param context
     * @return
     */
    private int initFaceModel(Context context) {
        String hsLibDirName = "zzFaceModel";
        String modelFile1 = "MIAXIS_V5.0.0_FaceDetect.model";
        String modelFile2 = "MIAXIS_V5.0.0_FaceQuality.model";
        String modelFile3 = "mx_eyeblink_detect.pb";
        File modelDir = new File(FileUtil.getFaceModelPath());
        if (modelDir.exists()) {
            if (!new File(modelDir + File.separator + modelFile1).exists()) {
                FileUtil.copyAssetsFile(context, hsLibDirName + File.separator + modelFile1, modelDir + File.separator + modelFile1);
            }
            if (!new File(modelDir + File.separator + modelFile2).exists()) {
                FileUtil.copyAssetsFile(context, hsLibDirName + File.separator + modelFile2, modelDir + File.separator + modelFile2);
            }
            if (!new File(modelDir + File.separator + modelFile3).exists()) {
                FileUtil.copyAssetsFile(context, hsLibDirName + File.separator + modelFile3, modelDir + File.separator + modelFile3);
            }
            return 0;
        } else {
            return -1;
        }
    }

    public static String getFaceInitResultDetail(int result) {
        switch (result) {
            case ERR_LICENCE:
                return "读取授权文件失败";
            case ERR_FILE_COMPARE:
                return "文件校验失败";
            case INIT_SUCCESS:
                return "初始化人脸算法成功";
            default:
                return "初始化算法失败";
        }
    }

    /**
     * 比对特征，人证比对0.7，人像比对0.8
     *
     * @param alpha
     * @param beta
     * @return
     */
    public float matchFeature(byte[] alpha, byte[] beta) {
        if (alpha != null && beta != null) {
            float[] score = new float[1];
            int re = mxFaceAPI.mxFeatureMatch(alpha, beta, score);
            if (re == 0) {
                return score[0];
            }
            return -1;
        }
        return 0;
    }

    /**
     * 比对口罩人脸特征
     *
     * @param alpha
     * @param beta
     * @return
     */
    public float matchMaskFeature(byte[] alpha, byte[] beta) {
        if (alpha != null && beta != null) {
            float[] score = new float[1];
            int re = mxFaceAPI.mxMaskFeatureMatch(alpha, beta, score);
            if (re == 0) {
                return score[0];
            }
            return -1;
        }
        return 0;
    }

    public byte[] imageEncode(byte[] rgbBuf, int width, int height) {
        byte[] fileBuf = new byte[width * height * 4];
        int[] fileLength = new int[]{0};
        int re = dtTool.ImageEncode(rgbBuf, width, height, ".jpg", fileBuf, fileLength);
        if (re == 1 && fileLength[0] != 0) {
            byte[] fileImage = new byte[fileLength[0]];
            System.arraycopy(fileBuf, 0, fileImage, 0, fileImage.length);
            return fileImage;
        } else {
            return null;
        }
    }

    /**
     * 图像文件解码成RGB裸数据
     *
     * @param data
     * @param width
     * @param height
     * @return
     */
    public byte[] imageFileDecode(byte[] data, int width, int height) {
        byte[] rgbData = new byte[width * height * 3];
        int[] oX = new int[1];
        int[] oY = new int[1];
        int result = dtTool.ImageDecode(data, data.length, rgbData, oX, oY);
        if (result > 0) {
            return rgbData;
        }
        return null;
    }

    /**
     * 摄像头预览数据转换
     *
     * @param data        摄像头onPreviewFrame-data
     * @param width       摄像头实际分辨率-宽
     * @param height      摄像头实际分辨率-高
     * @param orientation 旋转角度
     * @param zoomWidth   实际分辨率旋转压缩后的宽度
     * @param zoomHeight  实际分辨率旋转压缩后的高度
     * @return
     */
    private byte[] cameraPreviewConvert(byte[] data, int width, int height, int orientation, int zoomWidth, int zoomHeight) {
        // 原始YUV数据转换RGB裸数据
        byte[] rgbData = new byte[width * height * 3];
        dtTool.YUV2RGB(data, width, height, rgbData);
        int[] rotateWidth = new int[1];
        int[] rotateHeight = new int[1];
        // 旋转相应角度
        int re = dtTool.ImageRotate(rgbData, width, height, orientation, rgbData, rotateWidth, rotateHeight);
        if (re != 1) {
            Log.e("asd", "旋转失败");
            return null;
        }
        //镜像后画框位置按照正常坐标系，不镜像的话按照反坐标系也可画框
//        re = dtTool.ImageFlip(rgbData, rotateWidth[0], rotateHeight[0], 1, rgbData);
//        if (re != 1) {
//            Log.e("asd", "镜像失败");
//            return null;
//        }
        // RGB数据压缩到指定宽高
        byte[] zoomedRgbData = new byte[zoomWidth * zoomHeight * 3];
        re = dtTool.Zoom(rgbData, rotateWidth[0], rotateHeight[0], 3, zoomWidth, zoomHeight, zoomedRgbData);
        if (re != 1) {
            Log.e("asd", "压缩失败");
            return null;
        }
        return zoomedRgbData;
    }

    /**
     * 摄像头预览数据转换
     *
     * @param data        摄像头onPreviewFrame-data
     * @param width       摄像头实际分辨率-宽
     * @param height      摄像头实际分辨率-高
     * @param orientation 旋转角度
     * @param zoomWidth   实际分辨率旋转压缩后的宽度
     * @param zoomHeight  实际分辨率旋转压缩后的高度
     * @return
     */
    private byte[] cameraPreviewConvertWithFlip(byte[] data, int width, int height, int orientation, int zoomWidth, int zoomHeight) {
        // 原始YUV数据转换RGB裸数据
        byte[] rgbData = new byte[width * height * 3];
        dtTool.YUV2RGB(data, width, height, rgbData);
        int[] rotateWidth = new int[1];
        int[] rotateHeight = new int[1];
        // 旋转相应角度
        int re = dtTool.ImageRotate(rgbData, width, height, orientation, rgbData, rotateWidth, rotateHeight);
        if (re != 1) {
            Log.e("asd", "旋转失败");
            return null;
        }
        //镜像后画框位置按照正常坐标系，不镜像的话按照反坐标系也可画框
        re = dtTool.ImageFlip(rgbData, rotateWidth[0], rotateHeight[0], 1, rgbData);
        if (re != 1) {
            Log.e("asd", "镜像失败");
            return null;
        }
        // RGB数据压缩到指定宽高
        byte[] zoomedRgbData = new byte[zoomWidth * zoomHeight * 3];
        re = dtTool.Zoom(rgbData, rotateWidth[0], rotateHeight[0], 3, zoomWidth, zoomHeight, zoomedRgbData);
        if (re != 1) {
            Log.e("asd", "压缩失败");
            return null;
        }
        return zoomedRgbData;
    }

    /**
     * 组装人脸信息存储容器数组
     *
     * @param size
     * @return
     */
    private MXFaceInfoEx[] makeFaceContainer(int size) {
        MXFaceInfoEx[] pFaceBuffer = new MXFaceInfoEx[size];
        for (int i = 0; i < size; i++) {
            pFaceBuffer[i] = new MXFaceInfoEx();
        }
        return pFaceBuffer;
    }

    /**
     * 检测人脸信息
     *
     * @param rgbData    RGB裸图像数据
     * @param width      图像数据宽度
     * @param height     图像数据高度
     * @param faceNum    native输出，检测到的人脸数量
     * @param faceBuffer native输出，人脸信息
     * @return true - 算法执行成功，并且检测到人脸，false - 算法执行失败，或者执行成功但是未检测到人脸
     */
    private boolean faceDetect(byte[] rgbData, int width, int height, int[] faceNum, MXFaceInfoEx[] faceBuffer) {
        synchronized (lock2) {
            int result = mxFaceAPI.mxDetectFace(rgbData, width, height, faceNum, faceBuffer);
            return result == 0 && faceNum[0] > 0;
        }
    }

    /**
     * 人脸质量检测
     *
     * @param rgbData    RGB裸图像数据
     * @param width      图像数据宽度
     * @param height     图像数据高度
     * @param faceNum    检测到人脸数量
     * @param faceBuffer 输入，人脸检测结果
     * @return
     */
    private boolean faceQuality(byte[] rgbData, int width, int height, int faceNum, MXFaceInfoEx[] faceBuffer) {
        int result = mxFaceAPI.mxFaceQuality(rgbData, width, height, faceNum, faceBuffer);
        return result == 0;
    }

    /**
     * 口罩检测
     *
     * @param rgbData  RGB裸图像数据
     * @param width    图像数据宽度
     * @param height   图像数据高度
     * @param faceInfo 输入，人脸检测结果
     * @return
     */
    private boolean detectMask(byte[] rgbData, int width, int height, MXFaceInfoEx faceInfo) {
        int result = mxFaceAPI.mxMaskDetect(rgbData, width, height, 1, new MXFaceInfoEx[]{faceInfo});
        return result == 0;
    }

    /**
     * 红外活体检测
     *
     * @param rgbData    RGB裸图像数据
     * @param width      图像数据宽度
     * @param height     图像数据高度
     * @param faceNum    检测到人脸数量
     * @param faceBuffer 输入，人脸检测结果，native输出，根据瞳距进行从大到小排序
     * @return
     */
    private boolean infraredLivenessDetect(byte[] rgbData, int width, int height, int faceNum, MXFaceInfoEx faceBuffer) {
        int result = mxFaceAPI.mxNIRLivenessDetect(rgbData, width, height, faceNum, new MXFaceInfoEx[]{faceBuffer});
        return result == 0;
    }

    /**
     * RGB裸图像数据提取人脸特征
     *
     * @param pImage
     * @param width
     * @param height
     * @param faceInfo
     * @return
     */
    private byte[] extractFeature(byte[] pImage, int width, int height, MXFaceInfoEx faceInfo) {
        synchronized (lock1) {
            byte[] feature = new byte[mxFaceAPI.mxGetFeatureSize()];
            int result = mxFaceAPI.mxFeatureExtract(pImage, width, height, 1, new MXFaceInfoEx[]{faceInfo}, feature);
            return result == 0 ? feature : null;
        }
    }

    /**
     * @param pImage   - 输入，RGB图像数据
     * @param width    - 输入，图像宽度
     * @param height   - 输入，图像高度
     * @param faceInfo - 输入，人脸信息
     * @return 0-成功，其他-失败
     * @category 人脸特征提取, 用于比对（戴口罩算法）
     */
    public byte[] extractMaskFeature(byte[] pImage, int width, int height, MXFaceInfoEx faceInfo) {
        synchronized (lock1) {
            byte[] feature = new byte[mxFaceAPI.mxGetFeatureSize()];
            int result = mxFaceAPI.mxMaskFeatureExtract(pImage, width, height, 1, new MXFaceInfoEx[]{faceInfo}, feature);
            return result == 0 ? feature : null;
        }
    }

    /**
     * @param pImage   - 输入，RGB图像数据
     * @param width    - 输入，图像宽度
     * @param height   - 输入，图像高度
     * @param faceInfo - 输入，人脸信息
     * @return 0-成功，其他-失败
     * @category 人脸特征提取, 用于注册（戴口罩算法）
     */
    public byte[] extractMaskFeatureForRegister(byte[] pImage, int width, int height, MXFaceInfoEx faceInfo) {
        synchronized (lock1) {
            byte[] feature = new byte[mxFaceAPI.mxGetFeatureSize()];
            int result = mxFaceAPI.mxMaskFeatureExtract4Reg(pImage, width, height, 1, new MXFaceInfoEx[]{faceInfo}, feature);
            return result == 0 ? feature : null;
        }
    }

    /**
     * 获取人脸算法版本信息
     *
     * @return 版本信息
     */
    public String faceVersion() {
        return mxFaceAPI.mxAlgVersion();
    }

    private MXFaceInfoEx sortMXFaceInfoEx(MXFaceInfoEx[] mxFaceInfoExList) {
        MXFaceInfoEx maxMXFaceInfoEx = mxFaceInfoExList[0];
        for (MXFaceInfoEx mxFaceInfoEx : mxFaceInfoExList) {
            if (mxFaceInfoEx.width > maxMXFaceInfoEx.width) {
                maxMXFaceInfoEx = mxFaceInfoEx;
            }
        }
        return maxMXFaceInfoEx;
    }

    private static double calculationPupilDistance(MXFaceInfoEx mxFaceInfoEx) {
        int a = mxFaceInfoEx.keypt_x[1] - mxFaceInfoEx.keypt_x[0];
        int b = mxFaceInfoEx.keypt_y[1] - mxFaceInfoEx.keypt_y[0];
        double pow = Math.pow(a, 2) + Math.pow(b, 2);
        return Math.sqrt(pow);
    }

}
