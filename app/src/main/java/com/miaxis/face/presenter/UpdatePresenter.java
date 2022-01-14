package com.miaxis.face.presenter;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;




import com.afollestad.materialdialogs.MaterialDialog;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.miaxis.face.app.App;
import com.miaxis.face.bean.Config;
import com.miaxis.face.bean.ResponseEntity;
import com.miaxis.face.bean.UpdateData;
import com.miaxis.face.exception.MyException;
import com.miaxis.face.manager.ConfigManager;
import com.miaxis.face.manager.ToastManager;
import com.miaxis.face.net.FaceNetApi;
import com.miaxis.face.util.DateUtil;
import com.miaxis.face.util.FileUtil;
import com.miaxis.face.util.LogUtil;
import com.miaxis.face.util.MyUtil;
import com.miaxis.face.util.PatternUtil;
import com.miaxis.face.view.activity.SettingActivity;
import com.miaxis.face.view.activity.VerifyActivity;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

public class UpdatePresenter {

    private Context context;
    private Config config;

    private MaterialDialog waitDialog;
    private MaterialDialog updateMaterialDialog;
    private MaterialDialog downloadProgressDialog;

    public UpdatePresenter(Context context) {
        this.context = context;
        this.config = ConfigManager.getInstance().getConfig();
    }

    public void checkUpdateWithDialog(String urlStr) {
        waitDialog = new MaterialDialog.Builder(context)
                .cancelable(false)
                .autoDismiss(false)
                .content("请求服务器中，请稍后")
                .progress(true, 100)
                .show();
        checkUpdate(urlStr)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onUpdateDataDownWithDialog, throwable -> {
                    if (waitDialog != null) {
                        waitDialog.dismiss();
                    }
                    LogUtil.writeLog(throwable.getMessage());
                    Toast.makeText(context, "更新请求Url连接失败", Toast.LENGTH_SHORT).show();
                });
    }
//    public void checkUpdate() {
//        Log.d("Task====","checkUpdate");
//        String updateUrl = config.getUpdateUrl();
//        checkUpdate(updateUrl)
//                .subscribe(this::onUpdateDataDownSync, throwable -> {
//                    throwable.printStackTrace();
//                    Log.e("asd", "" + throwable.getMessage());
//                });
//    }



    public void checkUpdateSync() {
        Log.d("Task====","checkUpdateSync");
        String updateUrl = config.getUpdateUrl();
        checkUpdate(updateUrl)
                .subscribe(this::onUpdateDataDownSync, throwable -> {
                    throwable.printStackTrace();
                    Log.e("asd", "" + throwable.getMessage());
                });
    }

    private Observable<ResponseEntity<UpdateData>> checkUpdate(String url) {
        return Observable.just(url)
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .map(s -> {
                    Call<ResponseEntity<UpdateData>> call = FaceNetApi.downUpdateVersion(s);
                    Response<ResponseEntity<UpdateData>> execute = call.execute();
                    ResponseEntity<UpdateData> body = execute.body();
                    if (body != null) {
                        return body;
                    }
                    throw new MyException("接口返回数据解析失败");
                });
    }

    private void onUpdateDataDownWithDialog(ResponseEntity<UpdateData> responseEntity) {
        if (TextUtils.equals("200", responseEntity.getCode())) {
            if (waitDialog != null) {
                waitDialog.dismiss();
            }
            UpdateData updateData = responseEntity.getData();
            if (updateData == null) {
                Toast.makeText(context, "更新请求Url返回数据错误", Toast.LENGTH_SHORT).show();
                return;
            }
            String curVersion = MyUtil.getCurVersion(context);
            if (!TextUtils.isEmpty(updateData.getVersion()) && TextUtils.equals(updateData.getVersion(), curVersion)) {
                ToastManager.toast("已是最新版本");
            } else {
                updateMaterialDialog = new MaterialDialog.Builder(context)
                        .title("是否要下载更新文件？")
                        .content("设备当前版本：人证核验_" + curVersion + "\n"
                                + "服务器当前版本：人证核验_" + updateData.getVersion())
                        .positiveText("确认下载")
                        .onPositive((dialog, which) -> downUpdateFile(updateData.getVersion(), updateData.getUrl()))
                        .negativeText("取消")
                        .show();
            }
        } else {
            Toast.makeText(context, "更新请求Url返回码并非200", Toast.LENGTH_SHORT).show();
        }
    }

    private void onUpdateDataDownSync(ResponseEntity<UpdateData> responseEntity) {
        if (TextUtils.equals("200", responseEntity.getCode())) {
            UpdateData updateData = responseEntity.getData();
            if (updateData != null) {
                String curVersion = MyUtil.getCurVersion(context);
                if (!TextUtils.equals(updateData.getVersion(), curVersion)) {
                    File file=new File(FileUtil.FACE_MAIN_PATH );
                    File[] files=file.listFiles();
                    long max=0;
                    for (File f:files){
                        if(f.getName().contains(updateData.getVersion())&&!f.getName().contains(".temp")) {
                            Date date=new Date(f.lastModified());
                            max= Math.max(max,date.getTime());
                            Log.e("files==", "=" + f.getName()+"   时间:"+ DateUtil.toAll(date)+"毫秒："+date.getTime());
                        }
                    }
                    downloadFile(updateData.getUrl(), FileUtil.FACE_MAIN_PATH + File.separator + updateData.getVersion() + "_" + System.currentTimeMillis() + ".apk");
                }
            }
        }
    }

    private void downUpdateFile(String version, String url) {
        downloadProgressDialog = new MaterialDialog.Builder(context)
                .title("下载进度")
                .progress(false, 100)
                .positiveText("取消")
                .onPositive((dialog, which) -> FileDownloader.getImpl().pauseAll())
                .cancelable(false)
                .show();
        downloadFile(url, FileUtil.FACE_MAIN_PATH + File.separator + version + "_" + System.currentTimeMillis() + ".apk");
    }

    private void downloadFile(String url, String path) {
        FileDownloader.getImpl().create(url)
                .setPath(path)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        int percent = (int) ((double) soFarBytes / (double) totalBytes * 100);
                        Log.e("asd", "更新进度：" +percent + "%");
                        if (downloadProgressDialog != null && downloadProgressDialog.isShowing()) {
                            downloadProgressDialog.setProgress(percent);
                        }
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        onDownloadCompleted(task.getPath());
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        onDownloadFinish("下载已取消");
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        onDownloadFinish("下载时出现错误，已停止下载");
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                    }
                }).start();
    }

    private void onDownloadFinish(String message) {
        if (downloadProgressDialog != null && downloadProgressDialog.isShowing()) {
            downloadProgressDialog.dismiss();
            ToastManager.toast(message);
        }
    }

    private void onDownloadCompleted(String path) {
        if (downloadProgressDialog != null && downloadProgressDialog.isShowing()) {
            downloadProgressDialog.dismiss();
            ToastManager.toast("下载已完成，正在准备安装");
        }
        File file = new File(path);
        if (file.exists()) {
            installApk(file);
        } else {
            ToastManager.toast("文件路径未找到，请尝试手动安装");
        }
    }

    private void installApk(File file) {
        if(file.getName().contains(MyUtil.getCurVersion(context))){
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.e("intent.getFlags()",""+intent.getFlags());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, "com.miaxis.faceid_cw.fileprovider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            Log.e("intent.getFlags()",""+  context);
            if (context instanceof VerifyActivity || context instanceof SettingActivity){
                context.startActivity(intent);
            }else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            context.startActivity(intent);
        }
    }

    public void doDestroy() {
        this.context = null;
    }

}
