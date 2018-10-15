package com.example.user.wifistaterecord;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.user.wifistaterecord.permissions.DynamicPermissions;
import com.example.user.wifistaterecord.room.AppDatabase;
import com.example.user.wifistaterecord.room.WiFIStateData;
import com.example.user.wifistaterecord.room.WiFiStarteDao;
import com.example.user.wifistaterecord.service.WiFiService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private String [] mPermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
    ,Manifest.permission.INTERNET,Manifest.permission.ACCESS_WIFI_STATE};
    private Button mBtnStart;
    private Button mBtnDelete;
    private ListView mListView;
    private List<WiFIStateData> mList;
    private AppDatabase mAppDatabase;
    private WiFiStarteDao mDao;
    private ListViewAdapter mAdapter;
    private TextView mNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DynamicPermissions.I().setInitData(MainActivity.this, mPermissions, new DynamicPermissions.PermissionsTo() {
            @Override
            public void hasAuthorizeinit(Context context) {

            }

            @Override
            public void noAuthorizeinit(Context context) {

            }

            @Override
            public void authorizeinitFinish(Context context) {

            }
        });

        EventBus.getDefault().register(this);
        mAppDatabase = AppDatabase.getI(MainActivity.this);
        mDao = mAppDatabase.dao();
        mBtnStart = (Button)findViewById(R.id.btn_start);
        mListView = (ListView)findViewById(R.id.listView);
        mBtnDelete = (Button)findViewById(R.id.btn_delete);
        mNoData = (TextView)findViewById(R.id.no_data);
        mList = mAppDatabase.dao().getAll();
        if (mList.isEmpty()){
            mListView.setEmptyView(mNoData);
        }else {
            mAdapter = new ListViewAdapter(MainActivity.this,R.layout.listview_item,mList);
            mListView.setAdapter(mAdapter);

        }

        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(mList.isEmpty())) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mDao.delete(mList);
                            Log.e(TAG, "数据清除");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mList.clear();
                                    mAdapter.notifyDataSetChanged();
                                    mListView.postInvalidate();
                                    mListView.setEmptyView(mNoData);
                                    Log.e(TAG, "数据清除,更新UI");

                                }
                            });
                        }
                    }).start();


                }
            }
        });

        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,WiFiService.class);
                    startService(intent);
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.ASYNC,sticky = false)
    public void onGetEvent(MsgData data){
        if (data.getmsg().equals("event")){
            mList = mAppDatabase.dao().getAll();
            mAdapter = new ListViewAdapter(MainActivity.this,R.layout.listview_item,mList);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mListView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            });

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DynamicPermissions.I().onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
}
