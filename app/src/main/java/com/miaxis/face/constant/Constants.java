package com.miaxis.face.constant;

import android.os.Build;

import com.miaxis.face.BuildConfig;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/5/18 0018.
 */

public class Constants {

    public static final int PRE_WIDTH = 640;
    public static final int PRE_HEIGHT = 480;

//    public static final int ZOOM_WIDTH = 320;
//    public static final int ZOOM_HEIGHT = 240;
    public static final int ZOOM_WIDTH = 640;
    public static final int ZOOM_HEIGHT = 480;

    public static final int PIC_WIDTH = 640;
    public static final int PIC_HEIGHT = 480;

    public static final int CP_WIDTH = 1280;
    public static final int CP_HEIGHT = 960;
    public static final float zoomRate = CP_WIDTH / ZOOM_WIDTH;

    public static final int MAX_FACE_NUM       = 5;

    public static final int GPIO_INTERVAL = 100;

    public static final int GET_CARD_ID = 0;
    public static final int NO_CARD     = 134;

    public static final String[] FOLK = { "汉", "蒙古", "回", "藏", "维吾尔", "苗", "彝", "壮", "布依", "朝鲜",
            "满", "侗", "瑶", "白", "土家", "哈尼", "哈萨克", "傣", "黎", "傈僳", "佤", "畲",
            "高山", "拉祜", "水", "东乡", "纳西", "景颇", "柯尔克孜", "土", "达斡尔", "仫佬", "羌",
            "布朗", "撒拉", "毛南", "仡佬", "锡伯", "阿昌", "普米", "塔吉克", "怒", "乌孜别克",
            "俄罗斯", "鄂温克", "德昂", "保安", "裕固", "京", "塔塔尔", "独龙", "鄂伦春", "赫哲",
            "门巴", "珞巴", "基诺", "", "", "穿青人", "家人", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "其他", "外国血统", "",
            "" };

    public static final int PHOTO_SIZE         = 38862;        // 解码后身份证图片长度
    public static final int mFingerDataSize    = 512;          // 指纹数据长度
    public static final int mFingerDataB64Size = 684;          // 指纹数据Base64编码后的长度

    public static final int ADVERTISEMENT_NET = 1;
    public static final int ADVERTISEMENT_LOCAL = 0;
    public static final int ADVERTISEMENT_NET_AND_LOCAL = 2;

    public static final int VERIFY_MODE_FACE_ONLY = 0;
    public static final int VERIFY_MODE_FINGER_ONLY = 1;
    public static final int VERIFY_MODE_FACE_FIRST_ONCE = 2;
    public static final int VERIFY_MODE_FINGER_FIRST_ONCE = 3;
    public static final int VERIFY_MODE_FACE_FIRST_DOUBLE = 4;
    public static final int VERIFY_MODE_FINGER_FIRST_DOUBLE = 5;
    public static final int VERIFY_MODE_LOCAL_FEATURE = 6;

    public static final String DEFAULT_UPDATE_URL = BuildConfig.EQUIPMENT_PLACE==1?"http://www.haikouhotel.net:8080/SPCSite/advert/advInterface.aspx?param=getapp":
            "http://www.shangqiuhotel.net:8080/SPCSite/advert/advInterface.aspx?param=getapp";
    //    public static final String DEFAULT_UPLOAD_URL = "https://test.shxxyun.com/TSSApi/exchange/data/";
    public static final String DEFAULT_UPLOAD_URL ="http://192.168.0.1:12190/ytj"; // "http://www.mydahe.net:12190/ytj/";
    public static final String DEFAULT_ADVERTISEMENT_URL = BuildConfig.EQUIPMENT_PLACE==1?"http://www.haikouhotel.net:8080/SPCSite/advert/advInterface.aspx?param=getadvjoin ":
            "http://www.shangqiuhotel.net:8080/SPCSite/advert/advInterface.aspx?param=getadvjoin";
    public static final int DEFAULT_VERIFY_MODE = VERIFY_MODE_FACE_ONLY;
    public static final String DEFAULT_ACCOUNT = "zjzz";
//    public static final String DEFAULT_CLIENT_ID = "FY-0d373485ac6848cc8269f16eb963848f";
    public static final String DEFAULT_CLIENT_ID = "4108831987";
    public static final boolean DEFAULT_ENCRYPT = false;
    public static final boolean DEFAULT_NET_FLAG = true;
    public static final boolean DEFAULT_RESULT_FLAG = false;
    public static final boolean DEFAULT_SEQUEL_FLAG = false;
    public static final int DEFAULT_GATHER_FINGER_FLAG = 2;
    public static final boolean DEFAULT_SAVE_LOCAL_FLAG = false;
    public static final boolean DEFAULT_DOCUMENT_FLAG = false;
    public static final boolean DEFAULT_LIVENESS_FLAG = false;
    public static final boolean DEFAULT_QUERY_FLAG = false;
    public static final boolean DEFAULT_WHITE_FLAG = false;
    public static final boolean DEFAULT_BLACK_FLAG = false;
    public static final boolean DEFAULT_ADVERTISE_FLAG = true;
    public static final float DEFAULT_VERIFY_SCORE = 0.70f;
    public static final int DEFAULT_QUALITY_SCORE = 50;
    public static final int DEFAULT_LIVENESS_QUALITY_SCORE = 25;
    public static final String DEFAULT_TITLE_STR = "";
    public static final String DEFAULT_PASSWORD = "666666";
    public static final String DEFAULT_UPTIME = "3 : 00";
    public static final int DEFAULT_INTERVAL = 4;
    public static final String DEFAULT_ORG_NAME = "";
    public static final int DEFAULT_ADVERTISE_DELAY_TIME = 15;
    public static final Float FACEDISTANCE=10F;
    public static final Float HEADERANGLE=10F;
    public static final int DEFAULT_ADVERTISEMENT_MODE = ADVERTISEMENT_NET_AND_LOCAL;

    public static final int MAX_COUNT = 40000;
    public static final boolean DEFAULT_FINGER = false;

    public static final String PROJECT_NAME = "faceid";
    public static final String CHECK_VERSION = "app/getAppInfo";
    public static final String DOWN_VERSION = "app/getApp";
    public static final String UPLOAD_PERSON = "person/uploadPerson";

    public static final List<Integer> DELAYList= Arrays.asList(60 * 1000,2*60*1000,5*60*1000,10*60*1000,30*60*1000,60 * 60 * 1000,2*60*60*1000,5*60*60*1000,10*60*60*1000,24*60*60*1000);

    public static final int TASK_DELAY = 5*60*1000;

    public static final int RESULT_CODE_FINISH = 51243123;

    public static final int LEVEL              = 2;            // 指纹比对级别
    public static final int TIME_OUT           = 10 * 1000;    // 等待按手指的超时时间，单位：ms
    public static final int IMAGE_X_BIG        = 256;          // 指纹图像宽高 大小
    public static final int IMAGE_Y_BIG        = 360;
    public static final int IMAGE_SIZE_BIG     = IMAGE_X_BIG * IMAGE_Y_BIG;
    public static final int TZ_SIZE            = 512;          // 指纹特征长度  BASE64

    public static final float LEFT_VOLUME = 1.0f, RIGHT_VOLUME = 1.0f;
    public static final int PRIORITY = 1, LOOP = 0;
    public static final float SOUND_RATE = 1.0f;//正常速率

    public static final int PATH_TF_CARD = 0;
    public static final int PATH_LOCAL = 1;

    public static final int FINGER_RIGHT_0 = 11;
    public static final int FINGER_RIGHT_1 = 12;
    public static final int FINGER_RIGHT_2 = 13;
    public static final int FINGER_RIGHT_3 = 14;
    public static final int FINGER_RIGHT_4 = 15;

    public static final int FINGER_LEFT_0 = 16;
    public static final int FINGER_LEFT_1 = 17;
    public static final int FINGER_LEFT_2 = 18;
    public static final int FINGER_LEFT_3 = 19;
    public static final int FINGER_LEFT_4 = 20;
    public static final int HAS_UPLOAD = 21;
    public static final int UPLOAD_FAILED = 22;
    public static final int PLEASE_BLINK = 23;

    public static final int SOUND_SUCCESS = 1;
    public static final int SOUND_FAIL = 2;
    public static final int PLEASE_PRESS = 3;
    public static final int SOUND_OR = 4;
    public static final int SOUND_OTHER_FINGER  = 5;
    public static final int SOUND_VALIDATE_FAIL  = 6;

    public static final int TYPE_CAMERA = 0x11;//USB摄像头
    public static final int TYPE_ID_FP = 0x12;//指纹和⼆代证
    public static final int TYPE_LED_GREEN = 0x20;// LED 原MR990绿灯
    public static final int TYPE_LED_RED = 0x21;// LED 原MR990红灯
    public static final int TYPE_LED_BLUE = 0x22;// LED 原MR990蓝灯
    public static final int TYPE_LED = 0x23;// LED

    public static final String MOLD_POWER="com.miaxis.power";
    public static final String MOLD_STATUS="com.miaxis.status_bar";
    public static final String MOLD_NAV="com.miaxis.navigation";
    public static final String MOLD_INSTALL="com.miaxis.install";

    public static final boolean VERSION= !Build.VERSION.RELEASE.equals("11");//版本为Android11，VERSION为false
    public static final float pam=0.3f;//人脸图片截取扩大
    public static final long DIFFTIME=60000;//时间差
}
