package com.miaxis.face.manager;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.miaxis.face.app.App;
import com.miaxis.face.bean.IDCardRecord;
import com.miaxis.face.util.IdCardParser;
import com.zkteco.android.IDReader.WLTService;

import org.apache.commons.lang3.math.NumberUtils;
import org.zz.idcard_hid_driver.IdCardDriver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class CardManager {

    private CardManager() {
    }

    public static CardManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final CardManager instance = new CardManager();
    }

    /**
     * ================================ 静态内部类单例 ================================
     **/

    private static final String[] FOLK = {"汉", "蒙古", "回", "藏", "维吾尔", "苗", "彝", "壮", "布依", "朝鲜",
            "满", "侗", "瑶", "白", "土家", "哈尼", "哈萨克", "傣", "黎", "傈僳",
            "佤", "畲", "高山", "拉祜", "水", "东乡", "纳西", "景颇", "柯尔克孜", "土",
            "达斡尔", "仫佬", "羌","布朗", "撒拉", "毛南", "仡佬", "锡伯", "阿昌", "普米",
            "塔吉克", "怒", "乌孜别克", "俄罗斯", "鄂温克", "德昂", "保安", "裕固", "京", "塔塔尔",
            "独龙", "鄂伦春", "赫哲", "门巴", "珞巴", "基诺", "", "", "穿青人", "家人",
            "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "其他", "外国血统", "",
            ""};
    private static final int GET_CARD_ID = 0;
    private static final int NO_CARD = 134;
    private static final int FINGER_DATA_SIZE = 512;

    private volatile boolean run = true;
    private OnCardReadListener listener;
    private IdCardDriver idCardDriver;
    private byte[] lastCardId = null;
//    private String CardId="";

    public void init() {
        idCardDriver = new IdCardDriver(App.getInstance());
        idCardDriver.mxSetTraceLevel(1);
    }

    public void startReadCard() {
        run = true;
        new ReadIdCardThread().start();
        //new Thread(new ReadIdCardThread()).start();
    }

    public void closeReadCard() {
        run = false;
    }

    public void setListener(OnCardReadListener listener) {
        this.listener = listener;
    }

    public enum CardStatus {
        FindCard, ReadCard, NoCard
    }

    public interface OnCardReadListener {
        void onCardRead(CardStatus cardStatus, IDCardRecord idCardRecord);
    }

    private class ReadIdCardThread extends Thread {

        @Override
        public void run() {
            byte[] curCardId;
            int re;
            while (run) {
                curCardId = new byte[4096];
                if (idCardDriver == null) {
                    init();
                }
                re = idCardDriver.mxReadCardId(curCardId);
                switch (re) {
                    case GET_CARD_ID:
                        if (!Arrays.equals(lastCardId, curCardId)) {
                            listener.onCardRead(CardStatus.FindCard, null);
                            try {
                                readCardFull(getCardIdStr(curCardId));
                            } catch (Exception e) {
//                                FileUtil.writeFile(UsbBase.fileName,System.currentTimeMillis()+"  "+"读取异常："+exception(e)+"\n",true);
                                e.printStackTrace();
                                continue;
                            }
                        }
                        lastCardId = curCardId;
                        break;
                    case NO_CARD:
                        lastCardId = null;
//                        CardId="";
                        listener.onCardRead(CardStatus.NoCard, null);
                        break;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static String exception(Throwable t)  {
        if (t == null)
            return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            t.printStackTrace(new PrintStream(baos));
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return baos.toString();
    }
    private void readCardFull(String curCardId) throws Exception {
        IDCardRecord idCardRecord = null;
        byte[] bCardFullInfo = new byte[256 + 1024 + 1024];
        int re = idCardDriver.mxReadCardFullInfo(bCardFullInfo);

        String type = isGreenCard(bCardFullInfo);
        if (re == 1 || re == 0) {
            if ("I".equals(type)) {
                idCardRecord=IdGreenCardOarser(bCardFullInfo,curCardId);
            } else if ("-1".equals(type)){
                ToastManager.toast("身份证号码读取失败");
                throw new Exception("身份证号码读取失败");
            } else {
                idCardRecord=IdCardParser(bCardFullInfo,curCardId);
            }
            if (re == 0) {
                byte[] bFingerData0 = new byte[FINGER_DATA_SIZE];
                byte[] bFingerData1 = new byte[FINGER_DATA_SIZE];
                int iLen = 256 + 1024;
                try {
                    System.arraycopy(bCardFullInfo, iLen, bFingerData0, 0, bFingerData0.length);
                }catch (Exception e){
                    e.printStackTrace();
                }
                iLen += 512;
                try {
                    System.arraycopy(bCardFullInfo, iLen, bFingerData1, 0, bFingerData1.length);
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    idCardRecord.setFingerprintPosition0(fingerPositionCovert(bFingerData0[5]));
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    idCardRecord.setFingerprint0(Base64.encodeToString(bFingerData0, Base64.NO_WRAP));
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    idCardRecord.setFingerprintPosition1(fingerPositionCovert(bFingerData1[5]));
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    idCardRecord.setFingerprint1(Base64.encodeToString(bFingerData1, Base64.NO_WRAP));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } else {Log.e("readCard:","=="+"失败"+"----re="+re);
//            ToastManager.toast(re+"读卡失败，重新读取");
            throw new Exception("读卡失败");
        }
        listener.onCardRead(CardStatus.ReadCard, idCardRecord);
    }

    /**
     * 检查身份证是否已经过期
     *
     * @return true - 已过期 false - 未过期
     */
    public boolean checkIsOutValidate(IDCardRecord idCardRecord) {
        try {
            SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
            Date validEndDate = myFmt.parse(idCardRecord.getValidateEnd());
            return validEndDate.getTime() < System.currentTimeMillis();
        } catch (ParseException e) {
            return false;
        }
    }

    /* 解析身份证id 字符串 */
    private String getCardIdStr(byte[] cardId) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cardId.length; i++) {
            sb.append(String.format("%02x", cardId[i]));
        }
        String data = sb.toString();
        String cardIdStr = data.substring(0, 16);
        String errorCode = data.substring(16, 20);
        if (TextUtils.equals(errorCode, "9000")) {
            return cardIdStr;
        } else {
            return "";
        }
    }

    private Bitmap getBitmap(byte[] wlt) {
        byte[] buffer = new byte[38556];
        int result = WLTService.wlt2Bmp(wlt, buffer);
        if (result == 1) {
            return Bgr2Bitmap(buffer);
        }
        return null;
    }

    private Bitmap Bgr2Bitmap(byte[] bgrbuf) {
        int width = WLTService.imgWidth;
        int height = WLTService.imgHeight;
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        int row = 0, col = width - 1;
        for (int i = bgrbuf.length - 1; i >= 3; i -= 3) {
            int color = bgrbuf[i] & 0xFF;
            color += (bgrbuf[i - 1] << 8) & 0xFF00;
            color += ((bgrbuf[i - 2]) << 16) & 0xFF0000;
            bmp.setPixel(col--, row, color);
            if (col < 0) {
                col = width - 1;
                row++;
            }
        }
        return bmp;
    }

    public static String unicode2String(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length / 2; i++) {
            int a = bytes[2 * i + 1];
            if (a < 0) {
                a = a + 256;
            }
            int b = bytes[2 * i];
            if (b < 0) {
                b = b + 256;
            }
            int c = (a << 8) | b;
            sb.append((char) c);
        }
        return sb.toString();
    }

    public static String fingerPositionCovert(byte finger) {
        switch ((int) finger) {
            case 11:
                return "右手拇指";
            case 12:
                return "右手食指";
            case 13:
                return "右手中指";
            case 14:
                return "右手环指";
            case 15:
                return "右手小指";
            case 16:
                return "左手拇指";
            case 17:
                return "左手食指";
            case 18:
                return "左手中指";
            case 19:
                return "左手环指";
            case 20:
                return "左手小指";
            case 97:
                return "右手不确定指位";
            case 98:
                return "左手不确定指位";
            case 99:
                return "其他不确定指位";
            default:
                return "未知指位";
        }
    }

    public static int fingerPositionCovert(String finger) {
        switch (finger) {
            case "右手拇指":
                return 11;
            case "右手食指":
                return 12;
            case "右手中指":
                return 13;
            case "右手环指":
                return 14;
            case "右手小指":
                return 15;
            case "左手拇指":
                return 16;
            case "左手食指":
                return 17;
            case "左手中指":
                return 18;
            case "左手环指":
                return 19;
            case "左手小指":
                return 20;
            case "右手不确定指位":
                return 97;
            case "左手不确定指位":
                return 98;
            case "其他不确定指位":
                return 99;
            default:
                return 96;
        }
    }

    private String isGreenCard(byte[] bCardInfo) throws Exception{
        byte[] CardId=new byte[2];
        CardId[0]=bCardInfo[122];
        CardId[1]=bCardInfo[123];
        String card=unicode2String(CardId);
        if(NumberUtils.isDigits(card)){
            int id=Integer.parseInt(card);
            if (id>0&&id<9){
                return "";
            }else {
                return "-1";
            }
        }else {
            return "I";
        }
    }

    /* 解析身份证信息 */
    private IDCardRecord analysisIdCardInfo(byte[] bCardInfo, String cardId) {
        IDCardRecord idCardRecord = new IDCardRecord();
        idCardRecord.setCardId(cardId);
        byte[] id_Name = new byte[30]; // 姓名
        byte[] id_Sex = new byte[2]; // 性别 1为男 其他为女
        byte[] id_Rev = new byte[4]; // 民族
        byte[] id_Born = new byte[16]; // 出生日期
        byte[] id_Home = new byte[70]; // 住址
        byte[] id_Code = new byte[36]; // 身份证号
        byte[] _RegOrg = new byte[30]; // 签发机关
        byte[] id_ValidPeriodStart = new byte[16]; // 有效日期 起始日期16byte 截止日期16byte
        byte[] id_ValidPeriodEnd = new byte[16];
        byte[] id_NewAddr = new byte[36]; // 预留区域
        byte[] id_pImage = new byte[1024]; // 图片区域
        int iLen = 0;
        idCardRecord.setCardType("");
        System.arraycopy(bCardInfo, iLen, id_Name, 0, id_Name.length);
        iLen = iLen + id_Name.length;
        idCardRecord.setName(unicode2String(id_Name).trim());

        System.arraycopy(bCardInfo, iLen, id_Sex, 0, id_Sex.length);
        iLen = iLen + id_Sex.length;

        if (id_Sex[0] == '1') {
            idCardRecord.setSex("男");
        } else {
            idCardRecord.setSex("女");
        }

        System.arraycopy(bCardInfo, iLen, id_Rev, 0, id_Rev.length);
        iLen = iLen + id_Rev.length;
        int iRev = Integer.parseInt(unicode2String(id_Rev));
        idCardRecord.setNation(FOLK[iRev - 1]);

        System.arraycopy(bCardInfo, iLen, id_Born, 0, id_Born.length);
        iLen = iLen + id_Born.length;
        idCardRecord.setBirthday(unicode2String(id_Born));

        System.arraycopy(bCardInfo, iLen, id_Home, 0, id_Home.length);
        iLen = iLen + id_Home.length;
        idCardRecord.setAddress(unicode2String(id_Home).trim());

        System.arraycopy(bCardInfo, iLen, id_Code, 0, id_Code.length);
        iLen = iLen + id_Code.length;
        idCardRecord.setCardNumber(unicode2String(id_Code).trim());

        System.arraycopy(bCardInfo, iLen, _RegOrg, 0, _RegOrg.length);
        iLen = iLen + _RegOrg.length;
        idCardRecord.setIssuingAuthority(unicode2String(_RegOrg).trim());

        System.arraycopy(bCardInfo, iLen, id_ValidPeriodStart, 0, id_ValidPeriodStart.length);
        iLen = iLen + id_ValidPeriodStart.length;
        System.arraycopy(bCardInfo, iLen, id_ValidPeriodEnd, 0, id_ValidPeriodEnd.length);
        iLen = iLen + id_ValidPeriodEnd.length;
        String validateStart = unicode2String(id_ValidPeriodStart).trim();
        String validateEnd = unicode2String(id_ValidPeriodEnd).trim();
        idCardRecord.setValidateStart(validateStart);
        idCardRecord.setValidateEnd(validateEnd);

        System.arraycopy(bCardInfo, iLen, id_NewAddr, 0, id_NewAddr.length);
        iLen = iLen + id_NewAddr.length;
        System.arraycopy(bCardInfo, iLen, id_pImage, 0, id_pImage.length);
        Bitmap bitmap = getBitmap(id_pImage);
        if (bitmap != null) {
            idCardRecord.setCardBitmap(bitmap);
        }
        return idCardRecord;
    }

    /* 解析港澳台通行证信息 */
    public IDCardRecord analysiGATCardInfo(byte[] bCardInfo, String cardId) {
        IDCardRecord idCardRecord = new IDCardRecord();
        idCardRecord.setCardId(cardId);
        byte[] id_Name = new byte[30]; // 姓名
        byte[] id_Sex = new byte[2]; // 性别 1为男 其他为女
        byte[] id_Rev = new byte[4]; // 民族预留区
        byte[] id_Born = new byte[16]; // 出生日期
        byte[] id_Home = new byte[70]; // 住址
        byte[] id_Code = new byte[36]; // 身份证号
        byte[] id_RegOrg = new byte[30]; // 签发机关
        byte[] id_ValidPeriodStart = new byte[16]; // 有效日期 起始日期16byte 截止日期16byte
        byte[] id_ValidPeriodEnd = new byte[16];
//        byte[] id_NewAddr = new byte[36]; // 预留区域
        byte[] id_PassNum = new byte[18]; //通行证号码
        byte[] id_IssueNum = new byte[4]; //签发次数
        byte[] id_NewAddr = new byte[14]; //
        byte[] id_pImage = new byte[1024]; // 图片区域
        int iLen = 0;
//        idCardRecord.setCardType("J");
        byte[] CardTypes = new byte[2];
        CardTypes[0] = bCardInfo[248];
        CardTypes[1] = bCardInfo[249];
//        bCardInfo[32]=0x20;
//        bCardInfo[33]=0x00;
//        bCardInfo[34]=0x20;
//        bCardInfo[35]=0x00;


        String CardType =unicode2String(CardTypes);
        if(!TextUtils.isEmpty(CardType.trim())){
            idCardRecord.setCardType("J");
        }else {
            idCardRecord.setCardType("");
        }

        System.arraycopy(bCardInfo, iLen, id_Name, 0, id_Name.length);
        iLen = iLen + id_Name.length;
        idCardRecord.setName(unicode2String(id_Name).trim());

        System.arraycopy(bCardInfo, iLen, id_Sex, 0, id_Sex.length);
        iLen = iLen + id_Sex.length;
        if (id_Sex[0] == '1') {
            idCardRecord.setSex("男");
        } else {
            idCardRecord.setSex("女");
        }

        System.arraycopy(bCardInfo, iLen, id_Rev, 0, id_Rev.length);
        iLen = iLen + id_Rev.length;
        String nation=unicode2String(id_Rev);
        if (!TextUtils.isEmpty(nation.trim())){
            int iRev = Integer.parseInt(unicode2String(id_Rev));
            idCardRecord.setNation(FOLK[iRev - 1]);
            idCardRecord.setCardType("");
        }else {
            idCardRecord.setCardType("J");
            idCardRecord.setNation("");
        }

        System.arraycopy(bCardInfo, iLen, id_Born, 0, id_Born.length);
        iLen = iLen + id_Born.length;
        idCardRecord.setBirthday(unicode2String(id_Born));

        System.arraycopy(bCardInfo, iLen, id_Home, 0, id_Home.length);
        iLen = iLen + id_Home.length;
        idCardRecord.setAddress(unicode2String(id_Home).trim());

        System.arraycopy(bCardInfo, iLen, id_Code, 0, id_Code.length);
        iLen = iLen + id_Code.length;
        idCardRecord.setCardNumber(unicode2String(id_Code).trim());

        System.arraycopy(bCardInfo, iLen, id_RegOrg, 0, id_RegOrg.length);
        iLen = iLen + id_RegOrg.length;
        idCardRecord.setIssuingAuthority(unicode2String(id_RegOrg).trim());

        System.arraycopy(bCardInfo, iLen, id_ValidPeriodStart, 0, id_ValidPeriodStart.length);
        iLen = iLen + id_ValidPeriodStart.length;
        System.arraycopy(bCardInfo, iLen, id_ValidPeriodEnd, 0, id_ValidPeriodEnd.length);
        iLen = iLen + id_ValidPeriodEnd.length;
        String validateStart = unicode2String(id_ValidPeriodStart).trim();
        String validateEnd = unicode2String(id_ValidPeriodEnd).trim();
        idCardRecord.setValidateStart(validateStart);
        idCardRecord.setValidateEnd(validateEnd);

        System.arraycopy(bCardInfo, iLen, id_PassNum, 0, id_PassNum.length);
        iLen = iLen + id_PassNum.length;
        String passNum=unicode2String(id_PassNum);
        if(!TextUtils.isEmpty(passNum.trim())){
            idCardRecord.setCardType("J");
            idCardRecord.setPassNumber(passNum.trim());
        }
        System.arraycopy(bCardInfo, iLen, id_IssueNum, 0, id_IssueNum.length);
        iLen = iLen + id_IssueNum.length;
        String IssueNum=unicode2String(id_IssueNum);
        if (!TextUtils.isEmpty(IssueNum.trim())){
            idCardRecord.setCardType("J");
            idCardRecord.setIssueCount(IssueNum.trim());
        }
        System.arraycopy(bCardInfo, iLen, id_NewAddr, 0, id_NewAddr.length);
        iLen = iLen + id_NewAddr.length;

        System.arraycopy(bCardInfo, iLen, id_pImage, 0, id_pImage.length);
        Bitmap bitmap = getBitmap(id_pImage);
        if (bitmap != null) {
            idCardRecord.setCardBitmap(bitmap);
        }
        Log.e("idCardRe=====",idCardRecord.toString());
        return idCardRecord;
    }

    /* 解析外国人永久居留证信息 */
    public IDCardRecord analysisGreenCard(byte[] bCardInfo, String cardId) {
        IDCardRecord idCardRecord = new IDCardRecord();
        idCardRecord.setCardId(cardId);
        byte[] id_Name = new byte[120];    // 姓名
        byte[] id_Sex = new byte[2];      // 性别 1为男 其他为女
        byte[] id_cardNo = new byte[30];     // 永久居留证号码
        byte[] id_nation = new byte[6];      // 国籍或所在地区代码
        byte[] id_chinese_name = new byte[30];     // 中文姓名
        byte[] id_start_date = new byte[16];     // 证件签发日期
        byte[] id_end_date = new byte[16];     // 证件终止日期
        byte[] id_birthday = new byte[16];     // 出生日期
        byte[] id_version = new byte[4];      // 证件版本号
        byte[] id_reg_org = new byte[8];      // 当前申请受理机关代码
        byte[] id_type = new byte[2];      // 证件类型标识
        byte[] id_remark = new byte[6];      // 预留项
        byte[] id_pImage = new byte[1024];   // 照片
        int iLen = 0;
        idCardRecord.setCardType("I");

        System.arraycopy(bCardInfo, iLen, id_Name, 0, id_Name.length);
        iLen = iLen + id_Name.length;
        idCardRecord.setName(unicode2String(id_Name));

        System.arraycopy(bCardInfo, iLen, id_Sex, 0, id_Sex.length);
        iLen = iLen + id_Sex.length;
        if (id_Sex[0] == '1') {
            idCardRecord.setSex("男");
        } else {
            idCardRecord.setSex("女");
        }

        System.arraycopy(bCardInfo, iLen, id_cardNo, 0, id_cardNo.length);
        iLen += id_cardNo.length;
        idCardRecord.setCardNumber(unicode2String(id_cardNo));

        System.arraycopy(bCardInfo, iLen, id_nation, 0, id_nation.length);
        iLen += id_nation.length;
        idCardRecord.setNation(unicode2String(id_nation));

        System.arraycopy(bCardInfo, iLen, id_chinese_name, 0, id_chinese_name.length);
        iLen = iLen + id_chinese_name.length;
        idCardRecord.setChineseName(unicode2String(id_chinese_name));

        System.arraycopy(bCardInfo, iLen, id_start_date, 0, id_start_date.length);
        iLen = iLen + id_start_date.length;
        System.arraycopy(bCardInfo, iLen, id_end_date, 0, id_end_date.length);
        iLen = iLen + id_end_date.length;
        String validateStart = unicode2String(id_start_date).trim();
        String validateEnd = unicode2String(id_end_date).trim();
        idCardRecord.setValidateStart(validateStart);
        idCardRecord.setValidateEnd(validateEnd);

        System.arraycopy(bCardInfo, iLen, id_birthday, 0, id_birthday.length);
        iLen = iLen + id_birthday.length;
        idCardRecord.setBirthday(unicode2String(id_birthday));

        System.arraycopy(bCardInfo, iLen, id_version, 0, id_version.length);
        iLen = iLen + id_version.length;
        idCardRecord.setVersion(unicode2String(id_version));

        System.arraycopy(bCardInfo, iLen, id_reg_org, 0, id_reg_org.length);
        iLen += id_reg_org.length;
        idCardRecord.setIssuingAuthority(unicode2String(id_reg_org));

        System.arraycopy(bCardInfo, iLen, id_type, 0, id_type.length);
        iLen += id_type.length;
        //idCardRecord.setVersion(unicode2String(id_type));

        System.arraycopy(bCardInfo, iLen, id_remark, 0, id_remark.length);
        iLen += id_remark.length;

        System.arraycopy(bCardInfo, iLen, id_pImage, 0, id_pImage.length);
        Bitmap bitmap = getBitmap(id_pImage);
        if (bitmap != null) {
            idCardRecord.setCardBitmap(bitmap);
        }
        Log.e("idCardRe=====",idCardRecord.toString());
        return idCardRecord;
    }

    public IDCardRecord IdCardParser(byte[] bCardInfo, String cardId){
        IDCardRecord idCardRecord = new IDCardRecord();
        idCardRecord.setCardId(cardId);
        String id=IdCardParser.getName(bCardInfo);
        idCardRecord.setName(id);
//        CardId=id;
        if(IdCardParser.getGender(bCardInfo).equals("1")){
            idCardRecord.setSex("男");
        }else {
            idCardRecord.setSex("女");
        }
        idCardRecord.setNation(TextUtils.isEmpty(IdCardParser.getNation(bCardInfo).trim())?"":FOLK[Integer.parseInt(IdCardParser.getNation(bCardInfo))-1]);
        idCardRecord.setBirthday(IdCardParser.getBirthday(bCardInfo));
        idCardRecord.setAddress(IdCardParser.getAddress(bCardInfo));
        idCardRecord.setCardNumber(IdCardParser.getCardNum(bCardInfo));
        idCardRecord.setIssuingAuthority(IdCardParser.getIssuingAuthority(bCardInfo));
        idCardRecord.setValidateStart(IdCardParser.getStartTime(bCardInfo));
        idCardRecord.setValidateEnd(IdCardParser.getEndTime(bCardInfo));
        idCardRecord.setPassNumber(IdCardParser.getPassNum(bCardInfo));
        idCardRecord.setIssueCount(IdCardParser.getIssueNum(bCardInfo));
        idCardRecord.setCardType(getCardType(bCardInfo));
        idCardRecord.setCardBitmap(IdCardParser.getFaceBit(bCardInfo));
        return idCardRecord;
    }

    public IDCardRecord IdGreenCardOarser(byte[] bCardInfo, String cardId){
        IDCardRecord idCardRecord = new IDCardRecord();
        idCardRecord.setCardId(cardId);
        idCardRecord.setName(IdCardParser.getEnglishName(bCardInfo));
        if(IdCardParser.getEnglishGender(bCardInfo).equals("1")){
            idCardRecord.setSex("男");
        }else {
            idCardRecord.setSex("女");
        }
        idCardRecord.setCardNumber(IdCardParser.getCardNum(bCardInfo));
        idCardRecord.setNation(IdCardParser.getNationality(bCardInfo));
        idCardRecord.setChineseName(IdCardParser.getChineseName(bCardInfo));
        idCardRecord.setValidateStart(IdCardParser.getStartTime(bCardInfo));
        idCardRecord.setValidateEnd(IdCardParser.getEndTime(bCardInfo));
        idCardRecord.setBirthday(IdCardParser.getEnglishBir(bCardInfo));
        idCardRecord.setVersion(IdCardParser.getVersion(bCardInfo));
        idCardRecord.setIssuingAuthority(IdCardParser.getAcceptMatter(bCardInfo));
        idCardRecord.setCardType(IdCardParser.getCardType(bCardInfo));
        idCardRecord.setCardBitmap(IdCardParser.getFaceBit(bCardInfo));
        return idCardRecord;
    }
    /* 区分港澳台与二代证，不区分外国人*/
    private String getCardType(byte[] idCardData){
        if(TextUtils.isEmpty(IdCardParser.getCardType(idCardData))){
            if(TextUtils.isEmpty(IdCardParser.getPassNum(idCardData))&&!TextUtils.isEmpty(IdCardParser.getNation(idCardData))){
                return "";
            }else {
                return "J";
            }
        }else {
            return IdCardParser.getCardType(idCardData);
        }
    }
}
