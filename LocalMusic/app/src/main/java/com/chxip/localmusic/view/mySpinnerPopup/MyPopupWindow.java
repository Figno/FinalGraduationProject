package com.chxip.localmusic.view.mySpinnerPopup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.chxip.localmusic.R;

import java.util.List;


public class MyPopupWindow extends PopupWindow implements OnItemClickListener {

//    private List<String> mItems;
//    private MyPopupWindow mWindow;


    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        MyPopupWindow.this.dismiss();

    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
    }


    public int position() {

        return 0;
    }

}
