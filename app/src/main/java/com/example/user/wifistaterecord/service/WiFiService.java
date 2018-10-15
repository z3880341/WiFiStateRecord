package com.example.user.wifistaterecord.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

import com.example.user.wifistaterecord.MsgData;
import com.example.user.wifistaterecord.room.AppDatabase;
import com.example.user.wifistaterecord.room.WiFIStateData;
import com.example.user.wifistaterecord.room.WiFiStarteDao;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WiFiService extends Service {
    private IntentFilter mIntentFilter;
    private WiFiChangeReceiver mWiFiChangeReceiver;
    private static final String TAG = "WiFiService";
    private boolean mDisconnectState = true;
    private SimpleDateFormat mDf;
    private AppDatabase mAppDatabase;
    private WiFiStarteDao mDao;
    private MsgData msgData;
    public WiFiService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "WiFi Service 开启");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSSS");

        mAppDatabase = AppDatabase.getI(WiFiService.this);
        mDao = mAppDatabase.dao();
        msgData = new MsgData("event");
        EventBus.getDefault().register(this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.net.wifi.STATE_CHANGE");//WiFi状态变化
        mIntentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");//WiFi开关状态
        mWiFiChangeReceiver = new WiFiChangeReceiver();
        registerReceiver(mWiFiChangeReceiver,mIntentFilter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mWiFiChangeReceiver);
        EventBus.getDefault().unregister(this);
        Log.e(TAG, "WiFi Service 关闭");
    }


    @Subscribe(threadMode = ThreadMode.ASYNC,sticky = false)
    public void onGetEvent(MsgData data){}

    class WiFiChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int switchState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);//得到WiFi开关状态值
                switch (switchState) {
                    case WifiManager.WIFI_STATE_DISABLED://WiFi已关闭
                        WiFIStateData data1 = new WiFIStateData();
                        data1.time = mDf.format(System.currentTimeMillis());
                        data1.state = "WiFi关闭";
                        mDao.insert(data1);
                        EventBus.getDefault().post(msgData);
                        Log.e(TAG, "WiFi关闭");
                        break;
                    case WifiManager.WIFI_STATE_DISABLING://WiFi关闭中
                        Log.e(TAG, "WiFi关闭中");
                        break;
                    case WifiManager.WIFI_STATE_ENABLED://WiFi已开启
                        WiFIStateData data2 = new WiFIStateData();
                        data2.time = mDf.format(System.currentTimeMillis());
                        data2.state = "WiFi开启";
                        mDao.insert(data2);
                        EventBus.getDefault().post(msgData);
                        Log.e(TAG, "WiFi开启");
                        break;
                    case WifiManager.WIFI_STATE_ENABLING://WiFi开启中
                        Log.e(TAG, "WiFi开启中");
                        break;
                    case WifiManager.WIFI_STATE_UNKNOWN://WiFi状态未知
                        WiFIStateData data3 = new WiFIStateData();
                        data3.time = mDf.format(System.currentTimeMillis());
                        data3.state = "WiFi状态未知";
                        mDao.insert(data3);
                        EventBus.getDefault().post(msgData);
                        Log.e(TAG, "WiFi状态未知");
                        break;
                    default:
                        break;
                }
            }

                if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){ //网络状态改变行为
                    Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);//得到信息包
                    if (parcelableExtra != null){
                        NetworkInfo networkInfo = (NetworkInfo)parcelableExtra;//得到网络信息
                        NetworkInfo.DetailedState detailedState = networkInfo.getDetailedState();
                        switch (detailedState){
                            case CONNECTED:
                                mDisconnectState = true;
                                WiFIStateData data4 = new WiFIStateData();
                                data4.time = mDf.format(System.currentTimeMillis());
                                data4.state = "已经连接";
                                mDao.insert(data4);
                                EventBus.getDefault().post(msgData);
                                Log.e(TAG, "已经连接");
                                break;
                            case DISCONNECTED:
                                if (mDisconnectState){
                                    mDisconnectState = false;
                                    WiFIStateData data5 = new WiFIStateData();
                                    data5.time = mDf.format(System.currentTimeMillis());
                                    data5.state = "已经断开";
                                    mDao.insert(data5);
                                    EventBus.getDefault().post(msgData);
                                    Log.e(TAG, "已经断开");
                                }
                                break;
                            case IDLE:

                                WiFIStateData data6 = new WiFIStateData();
                                data6.time = mDf.format(System.currentTimeMillis());
                                data6.state = "空闲中";
                                mDao.insert(data6);
                                EventBus.getDefault().post(msgData);
                                Log.e(TAG, "空闲中");

                                break;
                            case AUTHENTICATING:

                                WiFIStateData data7 = new WiFIStateData();
                                data7.time = mDf.format(System.currentTimeMillis());
                                data7.state = "认证中";
                                mDao.insert(data7);
                                EventBus.getDefault().post(msgData);
                                Log.e(TAG, "认证中");

                                break;
                            case BLOCKED:

                                WiFIStateData data8 = new WiFIStateData();
                                data8.time = mDf.format(System.currentTimeMillis());
                                data8.state = "认证失败";
                                mDao.insert(data8);
                                EventBus.getDefault().post(msgData);
                                Log.e(TAG, "认证失败");

                                break;
                            case CAPTIVE_PORTAL_CHECK:

                                WiFIStateData data9 = new WiFIStateData();
                                data9.time = mDf.format(System.currentTimeMillis());
                                data9.state = "连接检查";
                                mDao.insert(data9);
                                EventBus.getDefault().post(msgData);
                                Log.e(TAG, "连接检查");

                                break;
                            default:
                                break;
                        }
                    }

            }

        }
    }
}
