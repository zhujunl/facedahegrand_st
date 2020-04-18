package com.miaxis.face.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;

import com.miaxis.face.app.App;
import com.miaxis.face.bean.Config;
import com.miaxis.face.bean.IDCardRecord;
import com.miaxis.face.bean.MxRGBImage;
import com.miaxis.face.bean.PhotoFaceFeature;
import com.miaxis.face.bean.Undocumented;
import com.miaxis.face.bean.WhiteItem;
import com.miaxis.face.manager.AmapManager;
import com.miaxis.face.manager.CardManager;
import com.miaxis.face.manager.ConfigManager;
import com.miaxis.face.manager.DaoManager;
import com.miaxis.face.manager.FaceManager;
import com.miaxis.face.manager.FingerManager;
import com.miaxis.face.manager.GpioManager;
import com.miaxis.face.manager.RecordManager;
import com.miaxis.face.manager.SoundManager;
import com.miaxis.face.manager.ToastManager;
import com.miaxis.face.model.IDCardRecordModel;
import com.miaxis.face.util.FileUtil;
import com.miaxis.face.view.activity.VerifyActivity;

import org.zz.api.MXFaceInfoEx;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;

import static com.miaxis.face.manager.FingerManager.FingerVerifyResult.NO_FINGER_FEATURE;
import static com.miaxis.face.manager.FingerManager.FingerVerifyResult.VERIFY_FAILED;
import static com.miaxis.face.manager.FingerManager.FingerVerifyResult.VERIFY_SUCCESS;

public class VerifyPresenter {

    private static final int UNDOCUMENTED_DELAY = 10;

    private WeakReference<VerifyActivity> view;
    private Config config;
    private Handler handler;

    private volatile boolean hasCardEvent = false;
    private volatile boolean undocumentedFlag = false;

    private IDCardRecord idCardRecord;
    private Undocumented undocumented;

    private boolean faceDone = false;
    private boolean fingerDone = false;
    private boolean undocumentedDone = false;

    private List<WhiteItem> whiteItemList;

    public VerifyPresenter(VerifyActivity activity, Config config) {
        this.view = new WeakReference<>(activity);
        this.config = config;
        initWithConfig();
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == UNDOCUMENTED_DELAY) {
                    synchronized (this) {
                        hasCardEvent = true;
                        onNoCard();
                    }
                }
            }
        };
    }

    public void doDestroy() {
        this.view.clear();
        this.view = null;
        handler.removeMessages(UNDOCUMENTED_DELAY);
        handler = null;
    }

    private void initWithConfig() {
        FaceManager.getInstance().setFaceHandleListener(faceListener);
        CardManager.getInstance().setListener(cardListener);
        AmapManager.getInstance().startLocation(App.getInstance());
        loadWhiteList();
    }

    private void loadWhiteList() {
        if (config.getWhiteFlag()) {
            App.getInstance().getThreadExecutor().execute(() -> {
                try {
                    whiteItemList = DaoManager.getInstance().getDaoSession().getWhiteItemDao().loadAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private CardManager.OnCardReadListener cardListener = (cardStatus, idCardRecord) -> {
        if (undocumentedFlag) return;
        switch (cardStatus) {
            case NoCard:
                onNoCard();
                break;
            case FindCard:
                onFindCard();
                break;
            case ReadCard:
                onReadCard(idCardRecord);
                break;
        }
    };

    private void onNoCard() {
        if (hasCardEvent) {
            hasCardEvent = false;
            idCardRecord = null;
            undocumented = null;
            faceDone = false;
            fingerDone = false;
            undocumentedDone = false;
            undocumentedFlag = false;
            if (handler != null) {
                handler.removeMessages(UNDOCUMENTED_DELAY);
            }
            SoundManager.getInstance().stopPlay();
            RecordManager.getInstance().cancelRequest();
            FingerManager.getInstance().stopVerifyFinger();
            FingerManager.getInstance().stopGatherFinger();
            FaceManager.getInstance().stopLoop();
            GpioManager.getInstance().closeLed();
            if (view.get() != null) {
                view.get().onCardEvent(CardManager.CardStatus.NoCard, null);
            }
        }
    }

    private void onFindCard() {
        hasCardEvent = true;
        GpioManager.getInstance().openLed();
        if (view.get() != null) {
            view.get().onCardEvent(CardManager.CardStatus.FindCard, null);
        }
    }

    private void onReadCard(IDCardRecord idCardRecord) {
        if (idCardRecord != null) {
            this.idCardRecord = idCardRecord;
            idCardRecord.setOrgName(config.getOrgName());
            if (view.get() != null) {
                view.get().onCardEvent(CardManager.CardStatus.ReadCard, idCardRecord);
            }
            if (checkValidate(idCardRecord)) {
                return;
            }
            if (!checkWhiteList(idCardRecord)) {
                return;
            }
            if (ConfigManager.isFaceFirst(config.getVerifyMode())) {
                onFaceVerifyMode(idCardRecord);
            } else {
                onFingerVerifyMode(idCardRecord);
            }
        }
    }

    private boolean checkValidate(IDCardRecord idCardRecord) {
        if (CardManager.getInstance().checkIsOutValidate(idCardRecord)) {
            if (view.get() != null) {
                view.get().overdue();
            }
            SoundManager.getInstance().playSound(SoundManager.SOUND_VALIDATE_FAIL);
            idCardRecord.setDescribe("身份证已过期");
            idCardRecord.setVerifyResult(false);
            idCardRecord.setVerifyTime(new Date());
            idCardRecord.setUpload(false);
            saveLocalRecord(idCardRecord);
            return true;
        }
        return false;
    }

    private boolean checkWhiteList(IDCardRecord idCardRecord) {
        if (!config.getWhiteFlag()) return true;
        if (whiteItemList != null && !whiteItemList.isEmpty()) {
            for (WhiteItem whiteItem : whiteItemList) {
                if (whiteItem.getCardNo().equals(idCardRecord.getCardNumber())) {
                    return true;
                }
            }
        }
        if (view.get() != null) {
            view.get().outWhiteList();
        }
        SoundManager.getInstance().playSound(SoundManager.SOUND_FAIL);
        idCardRecord.setDescribe("不在白名单内");
        idCardRecord.setVerifyResult(false);
        idCardRecord.setVerifyTime(new Date());
        idCardRecord.setUpload(false);
        saveLocalRecord(idCardRecord);
        return false;
    }

    private void onFaceVerifyMode(IDCardRecord idCardRecord) {
        if (idCardRecord != null) {
            if (view.get() != null) {
                view.get().verifyMode(true);
            }
            if (config.getLivenessFlag()) {
                SoundManager.getInstance().playSound(SoundManager.PLEASE_BLINK);
            }
            PhotoFaceFeature cardFaceFeature = FaceManager.getInstance().getCardFaceFeatureByBitmapPosting(idCardRecord.getCardBitmap());
            if (cardFaceFeature.getFaceFeature() != null && cardFaceFeature.getMaskFaceFeature() != null) {
                idCardRecord.setCardFeature(cardFaceFeature.getFaceFeature());
                idCardRecord.setMaskCardFeature(cardFaceFeature.getMaskFaceFeature());
                FaceManager.getInstance().setNeedNextFeature(!config.getLivenessFlag());
                FaceManager.getInstance().startLoop();
            } else {

            }
        }
    }

    private void onFingerVerifyMode(IDCardRecord idCardRecord) {
        if (idCardRecord != null) {
            if (view.get() != null) {
                view.get().verifyMode(false);
            }
            int rightPosition = CardManager.fingerPositionCovert(idCardRecord.getFingerprintPosition0());
            int leftPosition = CardManager.fingerPositionCovert(idCardRecord.getFingerprintPosition1());
            if (rightPosition >= 11 && rightPosition <= 20 && leftPosition >= 11 && leftPosition <= 20) {
                SoundManager.getInstance().playSound(SoundManager.PLEASE_PRESS,
                        leftPosition,
                        SoundManager.SOUND_OR,
                        rightPosition);
            } else {
                SoundManager.getInstance().playSound(SoundManager.SOUND_OTHER_FINGER);
            }
            FingerManager.getInstance().startVerifyFinger(
                    TextUtils.isEmpty(idCardRecord.getFingerprint0())
                            ? null
                            : Base64.decode(idCardRecord.getFingerprint0(), Base64.NO_WRAP),
                    TextUtils.isEmpty(idCardRecord.getFingerprint1())
                            ? null
                            : Base64.decode(idCardRecord.getFingerprint1(), Base64.NO_WRAP),
                    fingerVerifyListener);
        }
    }

    private FaceManager.OnFaceHandleListener faceListener = new FaceManager.OnFaceHandleListener() {
        @Override
        public void onFeatureExtract(MxRGBImage mxRGBImage, MXFaceInfoEx mxFaceInfoEx, byte[] feature, boolean mask) {
            if (undocumentedFlag) {
                onUndocumentedFeature(mxRGBImage, mxFaceInfoEx);
            } else {
                onFaceVerify(mxRGBImage, mxFaceInfoEx, feature, mask);
            }
        }

        @Override
        public void onFaceDetect(int faceNum, MXFaceInfoEx[] faceInfoExes) {
            if (faceDone) return;
            if (undocumentedDone) return;
            if (view.get() != null) {
                view.get().drawFaceRect(faceInfoExes, faceNum);
            }
        }

        @Override
        public void onFaceIntercept(int code, String message) {

        }

        @Override
        public void onActionLiveDetect(int code, String message) {
            if (view.get() != null) {
                String hint = "";
                switch (code) {
                    case -1:
                        hint = "请远离屏幕";
                        break;
                    case -2:
                        hint = "请靠近屏幕";
                        break;
                    case -3:
                        hint = "请对准提示框";
                        break;
                    case -4:
                        hint = "未检测到人脸";
                        break;
                    case -5:
                        hint = "请缓慢眨眼";
                        break;
                    case 1:
                        hint = "眨眼检测通过";
                        break;
                }
                view.get().actionLiveHint(hint);
            }
        }
    };

    private void onFaceVerify(MxRGBImage mxRGBImage, MXFaceInfoEx mxFaceInfoEx, byte[] feature, boolean mask) {
        if (idCardRecord != null && idCardRecord.getCardFeature() != null) {
            float faceMatchScore = FaceManager.getInstance().matchFeature(feature, idCardRecord.getCardFeature());
            float maskFaceMatchScore = FaceManager.getInstance().matchMaskFeature(feature, idCardRecord.getMaskCardFeature());
            float score = Math.max(faceMatchScore, maskFaceMatchScore);
            byte[] fileImage = FaceManager.getInstance().imageEncode(mxRGBImage.getRgbImage(), mxRGBImage.getWidth(), mxRGBImage.getHeight());
            Bitmap faceBitmap = BitmapFactory.decodeByteArray(fileImage, 0, fileImage.length);
            Bitmap rectBitmap = Bitmap.createBitmap(faceBitmap, mxFaceInfoEx.x, mxFaceInfoEx.y, mxFaceInfoEx.width, mxFaceInfoEx.height);//截取
            boolean result = mask ? score > config.getMaskScore() : score > config.getVerifyScore();
            FaceManager.getInstance().stopLoop();
            faceDone = true;
            idCardRecord.setFaceBitmap(faceBitmap);
            idCardRecord.setFaceResult(result);
            if (view.get() != null) {
                view.get().faceVerifyResult(result, rectBitmap, result ? "人脸通过" : "人脸失败");
            }
            checkMode(result, true);
        } else {

        }
    }

    private FingerManager.OnFingerVerifyListener fingerVerifyListener = (result, verifyFingerFeature, fingerPhoto, score) -> {
        if (idCardRecord != null) {
            String message = "";
            switch (result) {
                case VERIFY_SUCCESS:
                    idCardRecord.setLocaleFingerprint(verifyFingerFeature);
                    idCardRecord.setLocaleFingerprintBitmap(fingerPhoto);
                    message = "指纹通过";
                    break;
                case VERIFY_FAILED:
                    message = "指纹失败";
                    break;
                case NO_FINGER_FEATURE:
                    message = "无指纹";
                    break;
            }
            boolean fingerVerifyResult = result == VERIFY_SUCCESS;
            idCardRecord.setFingerResult(fingerVerifyResult);
            idCardRecord.setFingerScore(score);
            fingerDone = true;
            if (view.get() != null) {
                view.get().fingerVerifyResult(fingerVerifyResult, message);
            }
            checkMode(fingerVerifyResult, false);
        }
    };

    private void checkMode(boolean result, boolean lastFaceMode) {
        if (idCardRecord == null) return;
        if (ConfigManager.needExecuteNext(config.getVerifyMode(), result, lastFaceMode)) {
            if (ConfigManager.whatNextExecute(config.getVerifyMode())) {
                onFaceVerifyMode(idCardRecord);
            } else {
                onFingerVerifyMode(idCardRecord);
            }
        } else {
            GpioManager.getInstance().closeLed();
            idCardRecord.setVerifyTime(new Date());
            boolean success = ConfigManager.isPass(idCardRecord.getVerifyMode(),
                    idCardRecord.getFaceResult(),
                    idCardRecord.getFingerResult());
            idCardRecord.setVerifyResult(success);
            if (/*TextUtils.isEmpty(idCardRecord.getFingerprint0()) && */
                    success && config.getGatherFingerFlag() != 2) {
                gatherFinger();
            } else {
                if (success) {
                    SoundManager.getInstance().playSound(SoundManager.SOUND_SUCCESS);
                } else {
                    SoundManager.getInstance().playSound(SoundManager.SOUND_FAIL);
                }
                uploadIDCardRecord(idCardRecord);
            }
        }
    }

    private void gatherFinger() {
        if (view.get() != null) {
            view.get().gatherFingerResult(true, null, "请录入指纹");
        }
        SoundManager.getInstance().playSound(SoundManager.PRESS_FINGER);
        FingerManager.getInstance().gatherFingerprint(gatherListener);
    }

    private FingerManager.OnGatherFingerListener gatherListener = (status, fingerImage, feature) -> {
        if (idCardRecord != null) {
            if (status == FingerManager.GatherFingerStatus.GATHER_FINGER) {
                if (view.get() != null) {
                    view.get().gatherFingerResult(true, fingerImage, "请校验指纹");
                }
                SoundManager.getInstance().playSound(SoundManager.PRESS_FINGER);
            } else if (status == FingerManager.GatherFingerStatus.VERIFY_FINGER_SUCCESS) {
                if (view.get() != null) {
                    view.get().gatherFingerResult(false, null, "");
                }
                if (TextUtils.isEmpty(idCardRecord.getGatherFingerprint1())) {
                    idCardRecord.setGatherFingerprint1(feature);
                    idCardRecord.setGatherFingerprintBitmap1(fingerImage);
                    if (config.getGatherFingerFlag() == 1) {
                        if (view.get() != null) {
                            view.get().gatherFingerResult(true, null, "请更换手指");
                        }
                        SoundManager.getInstance().playSound(SoundManager.PLEASE_CHANGE_FINGER);
                        try {
                            Thread.sleep(1200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        gatherFinger();
                    } else {
                        SoundManager.getInstance().playSound(SoundManager.GATHER_SUCCESS);
                        uploadIDCardRecord(idCardRecord);
                    }
                } else {
                    SoundManager.getInstance().playSound(SoundManager.GATHER_SUCCESS);
                    idCardRecord.setGatherFingerprint2(feature);
                    idCardRecord.setGatherFingerprintBitmap2(fingerImage);
                    uploadIDCardRecord(idCardRecord);
                }
            } else {
                gatherFinger();
                if (view.get() != null) {
                    view.get().gatherFingerResult(true, null, "请重新录入");
                }
            }
        } else if (undocumented != null) {
            if (status == FingerManager.GatherFingerStatus.GATHER_FINGER) {
                if (view.get() != null) {
                    view.get().gatherFingerResult(true, fingerImage, "请校验指纹");
                }
                SoundManager.getInstance().playSound(SoundManager.PRESS_FINGER);
            } else if (status == FingerManager.GatherFingerStatus.VERIFY_FINGER_SUCCESS) {
                if (view.get() != null) {
                    view.get().gatherFingerResult(false, null, "");
                }
                if (TextUtils.isEmpty(undocumented.getGatherFingerprint1())) {
                    undocumented.setGatherFingerprint1(feature);
                    undocumented.setGatherFingerprintBitmap1(fingerImage);
                    if (config.getGatherFingerFlag() == 1) {
                        if (view.get() != null) {
                            view.get().gatherFingerResult(true, null, "请更换手指");
                        }
                        SoundManager.getInstance().playSound(SoundManager.PLEASE_CHANGE_FINGER);
                        if (handler != null) {
                            handler.removeMessages(UNDOCUMENTED_DELAY);
                            handler.sendMessageDelayed(handler.obtainMessage(UNDOCUMENTED_DELAY), 10 * 1000);
                        }
                        try {
                            Thread.sleep(1200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        gatherFinger();
                    } else {
                        uploadUndocumented(undocumented);
                    }
                } else {
                    undocumented.setGatherFingerprint2(feature);
                    undocumented.setGatherFingerprintBitmap2(fingerImage);
                    uploadUndocumented(undocumented);
                }
            } else {
                gatherFinger();
                if (view.get() != null) {
                    view.get().gatherFingerResult(true, null, "请重新录入");
                }
            }
        }
    };

    private void uploadIDCardRecord(IDCardRecord idCardRecord) {
        if (idCardRecord != null) {
            idCardRecord.setUpload(false);
            idCardRecord.setLocation(AmapManager.getInstance().getLocation());
            idCardRecord.setDescribe(ConfigManager.getDescribe(idCardRecord.getVerifyMode(),
                    idCardRecord.getFaceResult(),
                    idCardRecord.getFingerResult()));
            if (config.getResultFlag()
                    || (!config.getResultFlag() && idCardRecord.isVerifyResult())) {
                if (view.get() != null) {
                    view.get().uploadStatus("上传中");
                }
                RecordManager.getInstance().uploadRecord(idCardRecord, recordListener);
            }
            saveLocalRecord(idCardRecord);
        }
    }

    private void uploadUndocumented(Undocumented undocumented) {
        if (undocumented != null) {
            if (handler != null) {
                handler.removeMessages(UNDOCUMENTED_DELAY);
            }
            if (view.get() != null) {
                view.get().uploadStatus("上传中");
            }
            RecordManager.getInstance().uploadUndocumented(undocumented, recordListener);
        }
    }

    private RecordManager.OnRecordUploadResultListener recordListener = (result, message) -> {
        if (idCardRecord != null || undocumented != null) {
            if (result) {
                if (view.get() != null) {
                    view.get().uploadStatus("上传成功");
                }
                SoundManager.getInstance().playSound(SoundManager.HAS_UPLOAD);
            } else {
                if (view.get() != null) {
                    view.get().uploadStatus("上传失败");
                }
                SoundManager.getInstance().playSound(SoundManager.UPLOAD_FAILED);
            }
            if (undocumented != null) {
                undocumented = null;
                undocumentedFlag = false;
                undocumentedDone = false;
                if (view.get() != null) {
                    view.get().undocumentedResult(2);
                }
            }
        }
    };

    private void saveLocalRecord(IDCardRecord idCardRecord) {
        if (config.getSaveLocalFlag()) {
            App.getInstance().getThreadExecutor().execute(() -> {
                try {
                    IDCardRecordModel.saveIDCardRecord(idCardRecord);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastManager.toast("保存本地日志失败");
                }
            });
        }
    }

    public void undocumented(Undocumented undocumented) {
        this.undocumented = undocumented;
        undocumentedFlag = true;
        undocumentedDone = false;
        if (view.get() != null) {
            view.get().undocumentedResult(1);
        }
        if (handler != null) {
            handler.sendMessageDelayed(handler.obtainMessage(UNDOCUMENTED_DELAY), 10 * 1000);
        }
        if (config.getLivenessFlag()) {
            SoundManager.getInstance().playSound(SoundManager.PLEASE_BLINK);
        }
        FaceManager.getInstance().startLoop();
    }

    private void onUndocumentedFeature(MxRGBImage mxRGBImage, MXFaceInfoEx mxFaceInfoEx) {
        if (undocumented != null) {
            FaceManager.getInstance().stopLoop();
            undocumentedDone = true;
            if (view.get() != null) {
                view.get().undocumentedResult(0);
            }
            if (handler != null) {
                handler.removeMessages(UNDOCUMENTED_DELAY);
                handler.sendMessageDelayed(handler.obtainMessage(UNDOCUMENTED_DELAY), 10 * 1000);
            }
            byte[] fileImage = FaceManager.getInstance().imageEncode(mxRGBImage.getRgbImage(), mxRGBImage.getWidth(), mxRGBImage.getHeight());
            Bitmap faceBitmap = BitmapFactory.decodeByteArray(fileImage, 0, fileImage.length);
            undocumented.setFaceImage(faceBitmap);
            if (config.getGatherFingerFlag() != 2) {
                gatherFinger();
            } else {
                if (view.get() != null) {
                    view.get().uploadStatus("上传中");
                }
                uploadUndocumented(undocumented);
            }
        }
    }

    public void importWhiteList() {
        App.getInstance().getThreadExecutor().execute(() -> {
            String whiteContent = FileUtil.readFromUSBPath(App.getInstance(), "白名单.txt");
            if (TextUtils.isEmpty(whiteContent)) {
                File whiteTxtFile = FileUtil.searchFileFromU(App.getInstance(), "白名单.txt");
                if (whiteTxtFile != null) {
                    whiteContent = FileUtil.readFileToString(whiteTxtFile);
                }
            }
            if (TextUtils.isEmpty(whiteContent)) {
                if (view.get() != null) {
                    view.get().showResultDialog("加载名单失败！请检查U盘和文件是否存在");
                }
                return;
            }
            whiteContent = whiteContent.replace(" ", "");
            String[] aWhites = whiteContent.split(",");
            int max = aWhites.length;
            if (max > 0) {
                whiteItemList.clear();
                DaoManager.getInstance().getDaoSession().getWhiteItemDao().deleteAll();
                if (view.get() != null) {
                    view.get().showWaitDialog("进度：0 / " + max);
                }
                for (String aWhite : aWhites) {
                    whiteItemList.add(new WhiteItem(aWhite));
                    if (view.get() != null) {
                        view.get().showWaitDialog("进度：" + whiteItemList.size() + " / " + max);
                    }
                }
                DaoManager.getInstance().getDaoSession().getWhiteItemDao().insertInTx(whiteItemList);
                if (view.get() != null) {
                    view.get().dismissWaitDialog();
                    view.get().showResultDialog("导入成功");
                }
            } else {
                if (view.get() != null) {
                    view.get().showResultDialog("加载名单失败！白名单内容为空，或格式错误");
                }
            }
        });
    }

}
