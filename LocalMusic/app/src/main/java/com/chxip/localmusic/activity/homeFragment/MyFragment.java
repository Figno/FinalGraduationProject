package com.chxip.localmusic.activity.homeFragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.chxip.localmusic.R;

import com.chxip.localmusic.activity.LoginActivity;
import com.chxip.localmusic.activity.UpdatePassWordActivity;
import com.chxip.localmusic.entity.User;
import com.chxip.localmusic.util.SharedTools;
import com.chxip.localmusic.util.utilNet.Urls;
import com.chxip.localmusic.view.ImageTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;



public class MyFragment extends Fragment {
    View view;
    @BindView(R.id.tv_persion_center_name)
    TextView tv_persion_center_name;
    @BindView(R.id.ll_personal_center)
    LinearLayout ll_personal_center;
    @BindView(R.id.iv_personal_center_system_set)
    ImageTextView iv_personal_center_system_set;
    @BindView(R.id.rl_personal_center_system_set)
    RelativeLayout rl_personal_center_system_set;
    @BindView(R.id.iv_personal_center_out_login)
    ImageTextView iv_personal_center_out_login;
    @BindView(R.id.rl_personal_center_out_login)
    RelativeLayout rl_personal_center_out_login;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.my_fragment, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        user = (User) SharedTools.readObject(SharedTools.User);
        if (user != null) {
            tv_persion_center_name.setText(user.getRealName());
            Glide.with(this)
                    .load(Urls.BASE_IMG + "/" + user.getImageUrl())
                    .thumbnail(0.1f)
                    .placeholder(R.mipmap.default_avatar)
                    .error(R.mipmap.default_avatar);
        }
    }

    User user;

    //private void initViews() { }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //解绑服务
        unbinder.unbind();
    }


    @OnClick({R.id.rl_personal_center_system_set, R.id.rl_personal_center_out_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_personal_center_system_set:
                Intent intent = new Intent(getActivity(), UpdatePassWordActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_personal_center_out_login:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("提示");
                builder.setMessage("确定退出登陆吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedTools.deleteDate(SharedTools.Password);
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
                break;
        }
    }
}