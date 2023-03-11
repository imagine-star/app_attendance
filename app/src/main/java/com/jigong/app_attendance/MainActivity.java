package com.jigong.app_attendance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.jigong.app_attendance.databinding.ActivityMainBinding;
import com.jigong.app_attendance.hefei.InfoManageActivity;
import com.jigong.app_attendance.info.PublicTopicAddress;
import com.jigong.app_attendance.info.User;
import com.jigong.app_attendance.utils.CheckUtilsKt;
import com.jigong.app_attendance.utils.JsonUtils;
import com.jigong.app_attendance.utils.OkHttpApiKt;

import org.json.JSONException;
import org.json.JSONObject;

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
        User.getInstance().setInOnline(false);
        User.getInstance().setOutOnline(false);
        if (User.getInstance().getLogin()) {
            startActivity(new Intent(MainActivity.this, InfoManageActivity.class));
            finish();
        } else {
            binding.passWard.setText("13aa970548784c1086e293d6e9eb57aa");
            binding.login.setOnClickListener(view1 -> {
                binding.login.setOnClickListener(null);

                String userName = "jigong";
                String passWord = "XmcXQNjTUNq@RqN7";
                String account = binding.userName.getText().toString().trim();
                String projectId = binding.passWard.getText().toString().trim();
                String projectName = "测试";

                User.getInstance().setUserName(userName);
                User.getInstance().setPassWord(passWord);
                User.getInstance().setAccount(account);
                User.getInstance().setProjectId(projectId);
                User.getInstance().setProjectName(projectName);
                User.getInstance().setToken("1f52d9de3b6a440e870d7d895045a849");
                User.getInstance().setInDeviceNo("1738381");//进场设备sn
                User.getInstance().setOutDeviceNo("2178279");//出场设备sn

                startActivity(new Intent(MainActivity.this, InfoManageActivity.class));
                finish();

//                loadData();
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
                return OkHttpApiKt.doPostJson(PublicTopicAddress.LOGIN, map);
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
                            String userName = "jigong";
                            String passWord = "XmcXQNjTUNq@RqN7";
                            String account = binding.userName.getText().toString().trim();
                            String projectId = binding.passWard.getText().toString().trim();
                            String projectName = JsonUtils.getJsonValue(dataObject, "projectName", "");
                            String token = JsonUtils.getJsonValue(dataObject, "token", "");

                            User.getInstance().setUserName(userName);
                            User.getInstance().setPassWord(passWord);
                            User.getInstance().setAccount(account);
                            User.getInstance().setProjectId(projectId);
                            User.getInstance().setProjectName(projectName);
                            User.getInstance().setToken("1f52d9de3b6a440e870d7d895045a849");
                            User.getInstance().setInDeviceNo("1738381");//进场设备sn
                            User.getInstance().setOutDeviceNo("2178279");//出场设备sn

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