package com.jigong.app_attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
    }

    public String getVersionCode() {
        PackageManager manager = getPackageManager();
        String code = "";
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            code = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }

    public String getDeviceSN() {
        return android.os.Build.SERIAL;
    }

    /**
     * 弹出toast 显示时长short
     *
     * @param pMsg
     */
    public void showToastMsgShort(String pMsg) {
        Toast.makeText(this, pMsg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 弹出toase 显示时长long
     *
     * @param pMsg
     */
    public void showToastMsgLong(String pMsg) {
        Toast.makeText(this, pMsg, Toast.LENGTH_LONG).show();
    }

}