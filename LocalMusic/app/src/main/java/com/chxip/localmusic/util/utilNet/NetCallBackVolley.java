package com.chxip.localmusic.util.utilNet;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Volley 网络访问处理重写
 */
public abstract class NetCallBackVolley implements Response.Listener<String>, Response.ErrorListener{
	//失败
	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub
		onMyErrorResponse(error);
	}
	//成功
	@Override
	public void onResponse(String response) {
		// TODO Auto-generated method stub

		onMyRespone(response);

	}

	/**
	 * 网络访问成功返回
	 */
	public abstract void onMyRespone(String response);
	/**
	 * 网络访问失败返回
	 */
	public abstract void onMyErrorResponse(VolleyError error);
}
