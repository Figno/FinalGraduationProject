package com.chxip.localmusic.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.chxip.localmusic.MainActivity;
import com.chxip.localmusic.R;
import com.chxip.localmusic.entity.User;
import com.chxip.localmusic.util.MLog;
import com.chxip.localmusic.util.SharedTools;
import com.chxip.localmusic.util.ToastUtil;
import com.chxip.localmusic.util.Utils;
import com.chxip.localmusic.util.utilNet.HttpVolley;
import com.chxip.localmusic.util.utilNet.NetCallBackVolley;
import com.chxip.localmusic.util.utilNet.Urls;
import com.chxip.localmusic.view.ImageTextView;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


//登陆Activity
public class LoginActivity extends Activity {

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGIN_SUCCESS_ERROR:
                    String msgStr = msg.obj.toString();
                    if (!msgStr.equals("")) {
                        ToastUtil.show(LoginActivity.this, msgStr);
                    }
                    break;
            }
        }
        ;
    };


    @BindView(R.id.iv_left)
    ImageView iv_left;
    @BindView(R.id.tv_login_version_name)
    TextView tv_login_version_name;
    @BindView(R.id.et_login_name)
    EditText et_login_name;
    @BindView(R.id.tv_login_name_detele)
    ImageTextView tv_login_name_detele;
    @BindView(R.id.et_login_pwd)
    EditText et_login_pwd;
    @BindView(R.id.tv_login_pwd_according)
    ImageTextView tv_login_pwd_according;
    @BindView(R.id.cb_login_keep_pwd)
    CheckBox cb_login_keep_pwd;
    @BindView(R.id.tv_login_forget_pwd)
    TextView tv_login_forget_pwd;
    @BindView(R.id.tv_login_login)
    TextView tv_login_login;
    @BindView(R.id.ll_login_login)
    LinearLayout ll_login_login;
    @BindView(R.id.ll_login_registered)
    LinearLayout ll_login_registered;

    //
    boolean isShowPassword = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String name = SharedTools.getString(SharedTools.UserName);
        String pwd = SharedTools.getString(SharedTools.Password);
        //若非第一次打开则进入MainActivity
        if (name != null && pwd != null && name.length() > 0 && pwd.length() > 0) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            //加载activity_login
            setContentView(R.layout.activity_login);
            ButterKnife.bind(this);
            initViews();
        }

    }



    private void initViews(){
        //登录名监听器
        et_login_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()>0){
                    tv_login_name_detele.setVisibility(View.VISIBLE);
                    if(et_login_pwd.getText().toString().length()>0){
                        tv_login_login.setTextColor(getResources().getColor(R.color.white));
                        ll_login_login.setClickable(true);
                    }
                }else{
                    tv_login_name_detele.setVisibility(View.INVISIBLE);
                    tv_login_login.setTextColor(getResources().getColor(R.color.button_text_disable));
                    ll_login_login.setClickable(false);
                }
            }
        });
        //登陆密码监听器
        et_login_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()>0){
                    tv_login_pwd_according.setVisibility(View.VISIBLE);
                    if(et_login_name.getText().toString().length()>0){
                        tv_login_login.setTextColor(getResources().getColor(R.color.white));
                        ll_login_login.setClickable(true);
                    }
                }else{
                    tv_login_pwd_according.setVisibility(View.INVISIBLE);
                    tv_login_login.setTextColor(getResources().getColor(R.color.button_text_disable));
                    ll_login_login.setClickable(false);
                }
            }
        });

        //获取登录名、密码等
        et_login_name.setText(SharedTools.getString(SharedTools.UserName));
        et_login_name.setSelection(et_login_name.getText().length());
        et_login_pwd.setText(SharedTools.getString(SharedTools.Password));
        //tv_login_version_name.setText("V" + Utils.getVersionName(this, this.getPackageName()));
        //左键默认为关闭此activity
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @OnClick({R.id.ll_login_registered,R.id.tv_login_name_detele, R.id.tv_login_pwd_according, R.id.tv_login_forget_pwd, R.id.ll_login_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_login_name_detele:
                et_login_name.setText("");
                break;
            case R.id.tv_login_pwd_according:
                //显示密码
                if (isShowPassword) {
                    et_login_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShowPassword = false;
                    tv_login_pwd_according.setText(getString(R.string.image_login_pwd_hidden));
                    et_login_pwd.setSelection(et_login_pwd.getText().length());
                } else {
                    et_login_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isShowPassword = true;
                    tv_login_pwd_according.setText(getString(R.string.image_login_pwd_according));
                    et_login_pwd.setSelection(et_login_pwd.getText().length());
                }
                break;
            case R.id.ll_login_registered:
                //跳转到注册Activity
                Intent intent=new Intent(this, RegisteredActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_login_login:
                loginValidation();
                break;
        }
    }


    /**
     * 登录验证
     */
    private void loginValidation() {
        String name = et_login_name.getText().toString();
        String pwd = et_login_pwd.getText().toString();

        if (!Utils.isNull(name)) {
            if (!Utils.isNull(pwd)) {
                if(pwd.length()>=6){
                    Login(name, pwd);
                }else {
                    ToastUtil.show(this, getResources().getString(R.string.login_pwd_length_error));
                }
            } else {
                ToastUtil.show(this, getResources().getString(R.string.login_pwd_null));
            }
        } else {
            ToastUtil.show(this, getResources().getString(R.string.login_name_null));
        }
    }

    /**
     * 登录提示框
     */
    Dialog loginDialog;
    // login 返回成功，但是登陆不成功
    private final int LOGIN_SUCCESS_ERROR = 1;
    // 登录成功后返回的信息
    User user;

    /**
     * 登录
     */
    private void Login(final String name, final String pwd) {
        HashMap<String, String> map = new HashMap<>();
        map.put("userName", name);
        map.put("passWord", pwd);
        //Volley网络请求
        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                // TODO Auto-generated method stub
                MLog.i("Login登录返回:" + response);
                loginDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    //获取jsonObject解析出来的Success字段
                    //为true则登陆成功
                    Boolean status = jsonObject.getBoolean("Success");
                    if(status){
                        ToastUtil.show(LoginActivity.this,"登录成功");
                        //记住密码
                        if (cb_login_keep_pwd.isChecked()) {
                            SharedTools.saveData(SharedTools.UserName, name);
                            SharedTools.saveData(SharedTools.Password, pwd);
                        } else {
                            SharedTools.saveData(SharedTools.UserName, "");
                            SharedTools.saveData(SharedTools.Password, "");
                        }
                        //获取jsonObject解析出来的Result字段(用户的id、手机号、密码、昵称等)
                        String result=jsonObject.getString("Result");
                        //从json相关对象转换为java实体
                        user=new Gson().fromJson(result,User.class);
                        //调用saveObject保存对象数据
                        SharedTools.saveObject(user,SharedTools.User);
                        //跳转
                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        //获取jsonObject解析出来的Message字段
                        String strmsg = jsonObject.getString("Message");
                        Message msg = new Message();
                        //login 返回成功，但是登陆不成功
                        msg.obj = strmsg;
                        msg.what = LOGIN_SUCCESS_ERROR;
                        handler.sendMessage(msg);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onMyErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                loginDialog.dismiss();
                MLog.i(error.toString());
                //显示“网络出现错误!”
                ToastUtil.showNetworkError(LoginActivity.this);
            }
        };
        //网络是否可行 返回true则说明网络可行
        if (Utils.isNetworkAvailable(LoginActivity.this)) {
            loginDialog = Utils.createLoadingDialog(LoginActivity.this, getString(R.string.login_ts_msg));
            loginDialog.show();
            /**
             * 利用Volley进行Http访问
             *Urls.Login 返回的是登陆
             * ncbv为处理数据的接口
             */
            HttpVolley.volleStringRequestPostLogin(Urls.Login, map, ncbv);
        }
    }


}
