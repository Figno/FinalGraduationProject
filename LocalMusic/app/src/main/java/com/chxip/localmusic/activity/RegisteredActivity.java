package com.chxip.localmusic.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.android.volley.VolleyError;
import com.chxip.localmusic.R;
import com.chxip.localmusic.base.BaseActivity;
import com.chxip.localmusic.entity.User;
import com.chxip.localmusic.util.MLog;
import com.chxip.localmusic.util.ToastUtil;
import com.chxip.localmusic.util.Utils;
import com.chxip.localmusic.util.utilNet.HttpVolley;
import com.chxip.localmusic.util.utilNet.NetCallBackVolley;
import com.chxip.localmusic.util.utilNet.Urls;
import com.chxip.localmusic.view.ImageTextView;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//注册Activity
public class RegisteredActivity extends BaseActivity {

    @BindView(R.id.et_registered_name)
    EditText et_registered_name;
    @BindView(R.id.tv_login_name_detele)
    ImageTextView tv_login_name_detele;
    @BindView(R.id.et_registered_pwd)
    EditText et_registered_pwd;
    @BindView(R.id.tv_login_pwd_according)
    ImageTextView tv_login_pwd_according;
    @BindView(R.id.et_registered_qrPwd)
    EditText et_registered_qrPwd;
    @BindView(R.id.tv_login_qrPwd_according)
    ImageTextView tv_login_qrPwd_according;
    @BindView(R.id.ll_registered)
    LinearLayout ll_registered;
    @BindView(R.id.et_registered_realname)
    EditText et_registered_realname;
    @BindView(R.id.tv_login_realname_detele)
    ImageTextView tv_login_realname_detele;
    @BindView(R.id.et_registered_email)
    EditText et_registered_email;
    @BindView(R.id.rb_registered_men)
    RadioButton rb_registered_men;
    @BindView(R.id.rb_registered_wumen)
    RadioButton rb_registered_wumen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
        ButterKnife.bind(this);
        /**
         * 调用BaseActivity的initBaseView
         * 设置标题上的返回键  默认是关闭Activity
         */
        initBaseViews();
        setTitle("注册");
        //初始化控件 添加监听器
        initViews();

    }


    private void initViews() {
        //注册名字文本变化监听器
        et_registered_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            //“电话号码清除”键显示与否
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    tv_login_name_detele.setVisibility(View.VISIBLE);
                } else {
                    tv_login_name_detele.setVisibility(View.GONE);
                }
            }
        });
        //注册密码文本变化监听器
        et_registered_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            //“密码可见”键显示与否
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    tv_login_pwd_according.setVisibility(View.VISIBLE);
                } else {
                    tv_login_pwd_according.setVisibility(View.GONE);
                }
            }
        });
        //再次输入密码文本变化监听器
        et_registered_qrPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    tv_login_qrPwd_according.setVisibility(View.VISIBLE);
                } else {
                    tv_login_qrPwd_according.setVisibility(View.GONE);
                }
            }
        });
        //昵称文本变化监听器
        et_registered_realname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            //“真实姓名清除”键显示与否
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    tv_login_realname_detele.setVisibility(View.VISIBLE);
                } else {
                    tv_login_realname_detele.setVisibility(View.GONE);
                }
            }
        });
    }

    //默认密码和再次输入密码为不可见
    boolean isShowPassword = true;
    boolean isShowQrPassword = true;

    @OnClick({R.id.tv_login_realname_detele, R.id.tv_login_name_detele, R.id.tv_login_pwd_according, R.id.tv_login_qrPwd_according, R.id.ll_registered})
    public void onViewClicked(View view) {
        switch (view.getId()) {
                //昵称姓名清除键
            case R.id.tv_login_realname_detele:
                et_registered_realname.setText("");
                break;
                //手机号清除键
            case R.id.tv_login_name_detele:
                et_registered_name.setText("");
                break;
                //密码清除键
            case R.id.tv_login_pwd_according:
                if (isShowPassword) {
                    //隐藏回车
                    et_registered_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShowPassword = false;
                    tv_login_pwd_according.setText(getString(R.string.image_login_pwd_hidden));
                    et_registered_pwd.setSelection(et_registered_pwd.getText().length());
                } else {
                    //密码类型
                    et_registered_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isShowPassword = true;
                    tv_login_pwd_according.setText(getString(R.string.image_login_pwd_according));
                    et_registered_pwd.setSelection(et_registered_pwd.getText().length());
                }
                break;
                //再次输入密码清除键
            case R.id.tv_login_qrPwd_according:
                if (isShowQrPassword) {
                    //隐藏回车
                    et_registered_qrPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShowQrPassword = false;
                    tv_login_qrPwd_according.setText(getString(R.string.image_login_pwd_hidden));
                    et_registered_qrPwd.setSelection(et_registered_qrPwd.getText().length());
                } else {
                    //密码类型
                    et_registered_qrPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isShowQrPassword = true;
                    tv_login_qrPwd_according.setText(getString(R.string.image_login_pwd_according));
                    et_registered_qrPwd.setSelection(et_registered_qrPwd.getText().length());
                }
                break;
                //注册事件
            case R.id.ll_registered:
                //实例化一个user对象 将数据传入次对象中
                User user = new User();
                user.setName(et_registered_name.getText().toString());
                user.setPhone(et_registered_name.getText().toString());
                user.setPassword(et_registered_pwd.getText().toString());
                user.setRealName(et_registered_realname.getText().toString());
                user.setEmail(et_registered_email.getText().toString());
                user.setType(1);
                //进行判断
                if (user.getName() == null || user.getName().equals("")) {
                    ToastUtil.show(this, "请输入手机号码");
                    return;
                }
                if (user.getName().length()!=11) {
                    ToastUtil.show(this, "手机号码格式不正确");
                    return;
                }
                if (user.getPassword() == null || user.getPassword().equals("")
                        || et_registered_qrPwd.getText().toString() == null || et_registered_qrPwd.getText().toString().equals("")
                        ) {
                    ToastUtil.show(this, "请输入密码");
                    return;
                }
                if (user.getPassword().length() < 6 || et_registered_qrPwd.getText().toString().length() < 6) {
                    ToastUtil.show(this, "密码长度必须大于6位");
                    return;
                }
                if (!user.getPassword().equals(et_registered_qrPwd.getText().toString())) {
                    ToastUtil.show(this, "两次密码不一致");
                    return;
                }
                if (user.getRealName() == null && user.getRealName().equals("")) {
                    ToastUtil.show(this, "请输入真实姓名/昵称");
                    return;
                }
                if (rb_registered_men.isChecked()) {
                    user.setSex("男");
                } else {
                    user.setSex("女");
                }
                //注册
                Register(user);
                break;
        }
    }
    //弹出对话框
    Dialog loginDialog;
    /**
     *注册
     */
    private void Register(User user) {
        //存入HashMap集合
        HashMap<String, String> map = new HashMap<>();
        map.put("name", user.getName());
        map.put("pwd", user.getPassword());
        map.put("type", user.getType() + "");
        map.put("realName", user.getRealName());
        map.put("phone", user.getPhone());
        map.put("sex", user.getSex());
        map.put("email", user.getEmail());
        //Volley网络请求
        NetCallBackVolley ncbv = new NetCallBackVolley() {
            //重写访问成功的方法
            @Override
            public void onMyRespone(String response) {
                // TODO Auto-generated method stub
                MLog.i("Login登录返回:" + response);
                loginDialog.dismiss();
                try {
                    //将response转为json对象
                    JSONObject jsonObject = new JSONObject(response);
                    //获取jsonObject解析出来的Success字段
                    //为true则注册成功
                    Boolean status = jsonObject.getBoolean("Success");
                    if (status) {
                        ToastUtil.show(RegisteredActivity.this, "注册成功");
                        //RegisteredActivity->finish
                        //回到LoginActivity
                        finish();
                    } else {
                        //获取jsonObject解析出来的Message字段
                        String strmsg = jsonObject.getString("Message");
                        ToastUtil.show(RegisteredActivity.this, strmsg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //重写访问失败的方法
            @Override
            public void onMyErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                loginDialog.dismiss();
                MLog.i(error.toString());
                //显示“网络出现错误!”
                ToastUtil.showNetworkError(RegisteredActivity.this);
            }
        };

        //网络是否可行 返回true则说明网络可行
        if (Utils.isNetworkAvailable(RegisteredActivity.this)) {
            loginDialog = Utils.createLoadingDialog(RegisteredActivity.this, "注册中,请稍后...");
            loginDialog.show();
            /**
             * 利用Volley进行Http访问
             *Urls.Register 返回的是注册
             * ncbv为处理数据的接口
             */
            HttpVolley.volleStringRequestPostLogin(Urls.Register, map, ncbv);
        }
    }


}
