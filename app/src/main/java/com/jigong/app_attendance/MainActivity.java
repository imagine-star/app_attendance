package com.jigong.app_attendance;

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

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

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
            binding.passWard.setText("c8d852733a964d0ab99543be23e93dcf");
            binding.login.setOnClickListener(view1 -> {
                binding.login.setClickable(false);
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
                map.put("projectId", binding.passWard.getText().toString().trim());
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
                            User.getInstance().setToken(token);
                            User.getInstance().setInDeviceNo("JG0" + projectId.substring(0, 9).toUpperCase() + "0");//进场设备sn
                            User.getInstance().setOutDeviceNo("JG0" + projectId.substring(0, 9).toUpperCase() + "1");//出场设备sn

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