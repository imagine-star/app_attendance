package com.jigong.app_attendance.mainpublic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.jigong.app_attendance.databinding.ActivityMainBinding;
import com.jigong.app_attendance.info.GlobalCode;
import com.jigong.app_attendance.info.User;
import com.jigong.app_attendance.utils.CheckUtilsKt;
import com.jigong.app_attendance.utils.JsonUtils;
import com.jigong.app_attendance.utils.OkHttpApiKt;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if (User.getInstance().getDeleteTime() == 0) {
            User.getInstance().setDeleteTime(new Date().getTime());
        }
        if (new Date().getTime() - User.getInstance().getDeleteTime() > 86400000 * 7) {
            File filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            String fileName = "政府平台错误日志.text";
            File myFile = new  File(filePath, fileName);
            if (myFile.exists()) {
                boolean deleteFlag = myFile.delete();
                if (deleteFlag) {
                    User.getInstance().setDeleteTime(new Date().getTime());
                }
            }
        }

        User.getInstance().setInOnline(false);
        User.getInstance().setOutOnline(false);
        binding.userName.setText(User.getInstance().getJoinCity());
        if (User.getInstance().getLogin()) {
            startActivity(new Intent(MainActivity.this, InfoManageActivity.class));
            finish();
        } else {
            binding.login.setOnClickListener(view1 -> {
                doNext();
            });
        }
    }

    private void doNext() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x66);
        } else {
            loadData();
            binding.login.setOnClickListener(null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0x66) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadData();
            } else {
                //申请拒绝
                Toast.makeText(this, "您已拒绝读写权限，...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void loadData() {
        if (TextUtils.isEmpty(binding.userName.getText().toString().trim())) {
            Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(binding.passWard.getText().toString().trim())) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected String doInBackground(Void... voids) {
                Map<String, Object> map = new HashMap<>();
                map.put("joinCity", binding.userName.getText().toString().trim());
                map.put("secret", "1" + binding.passWard.getText().toString().trim() + new Date().getTime());
                map.put("sn", getDeviceSN());
                return OkHttpApiKt.doPostJson(GlobalCode.LOGIN_FOSHAN, map);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONObject entry = JsonUtils.getJSONObject(jsonObject, "entry");
                        String respMsg = JsonUtils.getJsonValue(entry, "respMsg", "");
                        if (CheckUtilsKt.checkResult(GlobalCode.LOGIN_FOSHAN, result)) {
                            User.getInstance().setLogin(true);
                            JSONObject dataObject = JsonUtils.getJSONObject(entry, "result");

                            String account = binding.userName.getText().toString().trim();
                            String projectId = JsonUtils.getJsonValue(dataObject, "projectId", "");
                            String projectName = JsonUtils.getJsonValue(dataObject, "projectName", "");
                            String gomeetToken = JsonUtils.getJsonValue(dataObject, "token", "");

                            User.getInstance().setProjectId(projectId);
                            User.getInstance().setProjectName(projectName);
                            User.getInstance().setGomeetToken(gomeetToken);

                            setDevice(projectId);

                            String joinCode = JsonUtils.getJsonValue(dataObject, "joinCode", "");
                            String joinPassword = JsonUtils.getJsonValue(dataObject, "joinPassword", "");
                            String joinProject = JsonUtils.getJsonValue(dataObject, "joinProject", "");
                            String joinDevice = JsonUtils.getJsonValue(dataObject, "joinDevice", "");
                            String joinPlatform = JsonUtils.getJsonValue(dataObject, "joinPlatform", "");
                            String developKey = JsonUtils.getJsonValue(dataObject, "developKey", "");
                            String developSecret = JsonUtils.getJsonValue(dataObject, "developSecret", "");
                            User.getInstance().setJoinCode(joinCode);
                            User.getInstance().setJoinPassword(joinPassword);
                            User.getInstance().setJoinProject(joinProject);
                            User.getInstance().setJoinDevice(joinDevice);
                            User.getInstance().setJoinPlatform(joinPlatform);
                            User.getInstance().setDevelopKey(developKey);
                            User.getInstance().setDevelopSecret(developSecret);

                            startActivity(new Intent(MainActivity.this, InfoManageActivity.class));
                            finish();
                        } else {
                            showToastMsgShort(respMsg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    showToastMsgShort("网络错误");
                }
                binding.login.setOnClickListener(view1 -> {
                    doNext();
                });
            }
        }.execute();
    }

    public void setDevice(String projectId) {
        switch (User.getInstance().getJoinCity()) {
            case "283":
                User.getInstance().setInDeviceNo(projectId);//进场设备sn
                User.getInstance().setOutDeviceNo(projectId.substring(0, 8) + "1");//出场设备sn
                break;
            case "306":
                User.getInstance().setCollectionDevice(projectId);
                User.getInstance().setInDeviceNo(projectId.substring(0, 8) + "0");//进场设备sn
                User.getInstance().setOutDeviceNo(projectId.substring(0, 8) + "1");//出场设备sn
                break;
        }
    }

}