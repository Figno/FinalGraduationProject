package com.chxip.localmusic.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class SpeechService extends Service {
    private String TAG = SpeechService.class.getSimpleName();

    private speechDemo speechDemo = new speechDemo();



    // 语音唤醒对象
    private VoiceWakeuper mIvw;
    // 唤醒结果内容
    private String resultString;
    // 识别结果内容
    private String recoString;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    // 云端语法文件
    private String mCloudGrammar = null;
    // 本地语法文件
    private String mLocalGrammar = null;
    private int curThresh = 1450;
    // 云端语法id
    private String mCloudGrammarID;







    private SpeechRecognizer mIat;
    private SharedPreferences mSharedPreferences;
    private int ret = 0;
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private String language="zh_cn";
    private String resultType = "json";





    public class speechDemo extends Binder{
        public void showTio(){
            showTip("=========onShowTio======");
        }

    }

    private void showTip(String str) {
        Log.d(TAG, str);
    }

    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, resultType);


        if(language.equals("zh_cn")) {
            String lag = mSharedPreferences.getString("iat_language_preference",
                    "mandarin");
            Log.e(TAG,"language:"+language);
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }else {
            mIat.setParameter(SpeechConstant.LANGUAGE, language);
        }
        Log.e(TAG,"last language:"+mIat.getParameter(SpeechConstant.LANGUAGE));

        //此处用于设置dialog中不显示错误码信息
        //mIat.setParameter("view_tips_plain","false");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));
    }

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
            showTip(text);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

    }
    private int flg = 0;
    /**
     * 听写监听器。
     */
    private final RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            showTip(error.getPlainDescription(true));

        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            String str = JsonParser.parseIatResult(results.getResultString());
            Log.d(TAG, results.getResultString());
            System.out.println(flg++);
            if (resultType.equals("json")) {
                printResult(results);
            }
            if (!(str.equals("。"))){
                Intent startMainActivity = new Intent();
                startMainActivity.putExtra("result",str);
                startMainActivity.setAction("com.lgr.speech");
                sendBroadcast(startMainActivity);
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据：" + data.length);
        }
        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            }
        }
    };

    @Override
    public void onCreate() {
        showTip("======onCreated======");

        mIvw = VoiceWakeuper.createWakeuper(this, null);
        // 初始化识别对象---唤醒+识别,用来构建语法
        //mAsr = SpeechRecognizer.createRecognizer(this, null);
        mCloudGrammar = readFile(this, "wake_grammar_sample.abnf", "utf-8");
        mLocalGrammar = readFile(this, "wake.bnf", "utf-8");
        startSpeech();




        super.onCreate();
    }

    private void startSpeech() {
        mIvw = VoiceWakeuper.getWakeuper();
        final String resPath = ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "ivw/5e88509e.jet");
        // 清空参数
        mIvw.setParameter(SpeechConstant.PARAMS,null);
        // 设置识别引擎
        mIvw.setParameter(SpeechConstant.ENGINE_TYPE,mEngineType);
        // 设置唤醒资源路径
        mIvw.setParameter(ResourceUtil.IVW_RES_PATH,resPath);
        mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:"+ curThresh);
        // 设置唤醒+识别模式
        mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
        mIvw.setParameter(SpeechConstant.KEEP_ALIVE,"1");
        // 设置返回结果格式
        mIvw.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 设置云端识别使用的语法id
        mIvw.setParameter(SpeechConstant.CLOUD_GRAMMAR, mCloudGrammarID);
        mIvw.startListening(mWakeuperListener);
    }

    private String readFile(SpeechService mContext, String file, String code) {
        int len = 0;
        byte[] buf = null;
        String result = "";
        try {
            InputStream in = mContext.getAssets().open(file);
            len = in.available();
            buf = new byte[len];
            in.read(buf, 0, len);

            result = new String(buf, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    private WakeuperListener mWakeuperListener = new WakeuperListener() {

        @Override
        public void onResult(WakeuperResult result) {
            try {
                String text = result.getResultString();
                JSONObject object;
                object = new JSONObject(text);
                StringBuffer buffer = new StringBuffer();
                buffer.append("【RAW】 "+text);
                buffer.append("\n");
                buffer.append("【操作类型】"+ object.optString("sst"));
                buffer.append("\n");
                buffer.append("【唤醒词id】"+ object.optString("id"));
                buffer.append("\n");
                buffer.append("【得分】" + object.optString("score"));
                buffer.append("\n");
                buffer.append("【前端点】" + object.optString("bos"));
                buffer.append("\n");
                buffer.append("【尾端点】" + object.optString("eos"));
                resultString =buffer.toString();
            } catch (JSONException e) {
                resultString = "结果解析出错";
                e.printStackTrace();
            }
            showTip(resultString);
            mIat = SpeechRecognizer.createRecognizer(SpeechService.this, mInitListener);
            //showTip(mIat.toString());
            mSharedPreferences = getSharedPreferences("com.iflytek.setting", 0);
            setParam();
            ret = mIat.startListening(mRecognizerListener);
            //textView.setText(resultString);
        }

        @Override
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
        }

        @Override
        public void onBeginOfSpeech() {
            showTip("开始说话");
        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {
            Log.d(TAG, "eventType:"+eventType+ "arg1:"+isLast + "arg2:" + arg2);
            // 识别结果
            if (SpeechEvent.EVENT_IVW_RESULT == eventType) {
                RecognizerResult reslut = ((RecognizerResult)obj.get(SpeechEvent.KEY_EVENT_IVW_RESULT));
                recoString += JsonParser.parseGrammarResult(reslut.getResultString());
                showTip(recoString);
                //textView.setText(recoString);
            }
        }

        @Override
        public void onVolumeChanged(int volume) {
            // TODO Auto-generated method stub

        }

    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showTip("======onStartCommand======");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        showTip("======onBind()======");
        return speechDemo;
    }
}
