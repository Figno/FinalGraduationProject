package com.chxip.localmusic.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.chxip.localmusic.R;
import com.chxip.localmusic.base.BaseActivity;
import com.chxip.localmusic.entity.User;
import com.chxip.localmusic.util.Constants;
import com.chxip.localmusic.util.MLog;
import com.chxip.localmusic.util.SharedTools;
import com.chxip.localmusic.util.ToastUtil;
import com.chxip.localmusic.util.Utils;
import com.chxip.localmusic.util.utilNet.HttpVolley;
import com.chxip.localmusic.util.utilNet.NetCallBackVolley;
import com.chxip.localmusic.util.utilNet.Urls;
import com.chxip.localmusic.view.ImageTextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//更新密码Activity
public class UpdatePassWordActivity extends BaseActivity {

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case Constants.SUCCESS:


                    break;
                case Constants.ERROR:
                    String msgStr = msg.obj.toString();
                    if (msgStr == null || msgStr.equals("")) {
                        ToastUtil.showNetworkError(UpdatePassWordActivity.this);
                    } else {
                        ToastUtil.show(UpdatePassWordActivity.this, msgStr);
                    }

                    break;
            }
        }
    };

    @BindView(R.id.et_update_password_original)
    EditText et_update_password_original;
    @BindView(R.id.tv_update_password_original_according)
    ImageTextView tv_update_password_original_according;
    @BindView(R.id.et_update_password_new)
    EditText et_update_password_new;
    @BindView(R.id.tv_update_password_new_according)
    ImageTextView tv_update_password_new_according;
    @BindView(R.id.et_update_password_new_confirm)
    EditText et_update_password_new_confirm;
    @BindView(R.id.tv_update_password_new_confirm_according)
    ImageTextView tv_update_password_new_confirm_according;
    @BindView(R.id.ll_update_password_determine)
    LinearLayout ll_update_password_determine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pass_word);
        ButterKnife.bind(this);
        user= (User) SharedTools.readObject(SharedTools.User);
        initBaseViews();
        setTitle(getString(R.string.update_password_title));

        initViews();
    }

    User user;

    private void initViews(){
        et_update_password_original.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()>0){
                    tv_update_password_original_according.setVisibility(View.VISIBLE);
                }else{
                    tv_update_password_original_according.setVisibility(View.GONE);
                }
            }
        });

        et_update_password_new.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()>0){
                    tv_update_password_new_according.setVisibility(View.VISIBLE);
                }else{
                    tv_update_password_new_according.setVisibility(View.GONE);
                }
            }
        });

        et_update_password_new_confirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()>0){
                    tv_update_password_new_confirm_according.setVisibility(View.VISIBLE);
                }else{
                    tv_update_password_new_confirm_according.setVisibility(View.GONE);
                }
            }
        });
    }

    @OnClick({R.id.ll_update_password_determine,R.id.tv_update_password_original_according, R.id.tv_update_password_new_according, R.id.tv_update_password_new_confirm_according})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_update_password_original_according:
                et_update_password_original.setText("");
                break;
            case R.id.tv_update_password_new_according:
                et_update_password_new.setText("");
                break;
            case R.id.tv_update_password_new_confirm_according:
                et_update_password_new_confirm.setText("");
                break;
            case R.id.ll_update_password_determine:
                String old=et_update_password_original.getText().toString();
                String newOne=et_update_password_new.getText().toString();
                String newTwo=et_update_password_new_confirm.getText().toString();
                if(old.equals("")){
                    ToastUtil.show(this,"请输入原密码!");
                    return;
                }
                if(newOne.equals("")){
                    ToastUtil.show(this,"请输入新密码!");
                    return;
                }
                if(newTwo.equals("")){
                    ToastUtil.show(this,"请确认新密码!");
                    return;
                }
                if(newOne.length()<6 || newTwo.length()<6){
                    ToastUtil.show(this,"密码长度不能小于6位!");
                    return;
                }

                if(!newOne.equals(newTwo)){
                    ToastUtil.show(this,"新密码不一致!");
                    return;
                }
                ChangePassword(old,newOne);


                break;
        }
    }


    Dialog dialog;
    /**
     * 修改密码
     */
    private void ChangePassword(String oldpass,String newpass) {

        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                MLog.i("修改密码:" + response);
                dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    //获取jsonObject解析出来的Success字段
                    //为true即修改成功
                    Boolean status = jsonObject.getBoolean("Success");
                    if (status) {
                        ToastUtil.show(UpdatePassWordActivity.this,"修改成功,请重新登录!");
                        Thread thread=new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    //线程休眠1000ms
                                    Thread.sleep(1000);
                                    //清除数据
                                    SharedTools.deleteDate(SharedTools.Password);
                                    /**
                                     * getPackageManager():获取一个PackageManager对象
                                     *getLunchIntentForPackage()：获取这个application的包名
                                     * 修改密码后先退出应用再调用第三方应用(本app)
                                     * 即退出后再回到本app的LoginActivity界面
                                     */
                                    Intent i = UpdatePassWordActivity.this.getPackageManager().
                                            getLaunchIntentForPackage(UpdatePassWordActivity.this.getPackageName());
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    UpdatePassWordActivity.this.startActivity(i);
                                    System.exit(0);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();
                    } else {
                        String strmsg = jsonObject.getString("Message");
                        Message msg = new Message();
                        msg.obj = strmsg;
                        msg.what = Constants.ERROR;
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onMyErrorResponse(VolleyError error) {
                MLog.i(error.toString());
                //显示“网络出现错误！”
                ToastUtil.showNetworkError(UpdatePassWordActivity.this);
                dialog.dismiss();
            }
        };
        //检测当前网络
        if (Utils.isNetworkAvailable(this)) {
            dialog=Utils.createLoadingDialog(this,getString(R.string.app_dialog_save));
            dialog.show();
            String path = Urls.ChangePassword+"?oldPwd="+oldpass+"&newPwd="+newpass+"&id="+user.getId()+"&RealName="+user.getRealName();
            MLog.i("修改密码：" + path);
            /**
             * 利用Volley进行Http访问
             * path:由上面传回的为修改密码的访问路径
             * ncbv为处理数据的接口
             */
            HttpVolley.volleStringRequestGetString(path, ncbv);
        }
    }





}
