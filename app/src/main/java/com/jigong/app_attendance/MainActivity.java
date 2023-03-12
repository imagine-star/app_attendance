package com.jigong.app_attendance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.jigong.app_attendance.bean.WorkerInfo;
import com.jigong.app_attendance.databinding.ActivityMainBinding;
import com.jigong.app_attendance.greendao.WorkerInfoDao;
import com.jigong.app_attendance.hefei.InfoManageActivity;
import com.jigong.app_attendance.info.PublicTopicAddress;
import com.jigong.app_attendance.info.User;
import com.jigong.app_attendance.utils.CheckUtilsKt;
import com.jigong.app_attendance.utils.JsonUtils;
import com.jigong.app_attendance.utils.OkHttpApiKt;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.util.HexUtil;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        User.getInstance().setInOnline(false);
        User.getInstance().setOutOnline(false);
        if (User.getInstance().getLogin()) {
            startActivity(new Intent(MainActivity.this, InfoManageActivity.class));
            finish();
        } else {
//            binding.passWard.setText("af5d144ab54d484db71da55dbedf593c");
            binding.login.setOnClickListener(view1 -> {
                binding.login.setOnClickListener(null);
                loadData();
            });
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
                return OkHttpApiKt.doPostJson(PublicTopicAddress.LOGIN_FOSHAN, map);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONObject entry = JsonUtils.getJSONObject(jsonObject, "entry");
                        String respMsg = JsonUtils.getJsonValue(entry, "respMsg", "");
                        if (CheckUtilsKt.checkResult(result)) {
                            User.getInstance().setLogin(true);
                            JSONObject dataObject = JsonUtils.getJSONObject(entry, "result");
//                            String userName = "jigong";
//                            String passWord = "XmcXQNjTUNq@RqN7";
                            String account = binding.userName.getText().toString().trim();
                            String projectId = JsonUtils.getJsonValue(dataObject, "projectId", "");
                            String projectName = JsonUtils.getJsonValue(dataObject, "projectName", "");
                            String token = JsonUtils.getJsonValue(dataObject, "joinCode", "");

//                            User.getInstance().setUserName(userName);
//                            User.getInstance().setPassWord(passWord);
                            User.getInstance().setAccount(account);
                            User.getInstance().setProjectId(projectId);
                            User.getInstance().setProjectName(projectName);
                            User.getInstance().setToken(token);
                            User.getInstance().setInDeviceNo(projectId);//进场设备sn
                            User.getInstance().setOutDeviceNo(projectId.substring(0, 8) + "1");//出场设备sn

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
            }

        }.execute();
    }

}