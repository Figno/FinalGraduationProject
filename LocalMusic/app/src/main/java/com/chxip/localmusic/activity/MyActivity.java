package com.chxip.localmusic.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chxip.localmusic.R;
import com.chxip.localmusic.base.BaseActivity;
import com.chxip.localmusic.entity.User;
import com.chxip.localmusic.util.SharedTools;
import com.chxip.localmusic.view.CustomDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


//我的信息Activity
public class MyActivity extends BaseActivity {


    @BindView(R.id.tv_persion_center_name)
    TextView tv_persion_center_name;
    @BindView(R.id.tv_my_account)
    TextView tv_my_account;
    @BindView(R.id.ll_personal_center)
    LinearLayout ll_personal_center;
    @BindView(R.id.rl_update_password)
    RelativeLayout rl_update_password;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ButterKnife.bind(this);
        //读取刚刚登录的user数据
        user = (User) SharedTools.readObject(SharedTools.User);
        initBaseViews();
        setTitle("我的");

        initViews();
    }

    private void initViews() {
        tv_my_account.setText(user.getName());
        tv_persion_center_name.setText(user.getRealName());
    }

    @OnClick({R.id.ll_exit, R.id.rl_update_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_exit:
                Exit();
                break;
            case R.id.rl_update_password:
                Intent update_password=new Intent(MyActivity.this, UpdatePassWordActivity.class);
                startActivity(update_password);
                break;

        }
    }

    private void Exit(){
        final CustomDialog dialog = new CustomDialog(MyActivity.this, R.style.mystyle, R.layout.system_set_log_out, "提示",
                "确定退出登录吗?");
        dialog.show();
        Button confirm_btndx = (Button) dialog.findViewById(R.id.confirm_btn);

        confirm_btndx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.cancel();
                SharedTools.deleteDate(SharedTools.Password);
                finish();

            }
        });
        Button cancel_btndx = (Button) dialog.findViewById(R.id.cancel_btn);
        cancel_btndx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.cancel();
            }
        });
    }
}
