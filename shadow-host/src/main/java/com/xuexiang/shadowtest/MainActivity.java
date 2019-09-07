package com.xuexiang.shadowtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.xuexiang.shadowcommon.Constant;
import com.xuexiang.shadowtest.plugin.PluginLoadActivity;
import com.xuexiang.xaop.annotation.Permission;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xutil.tip.ToastUtils;

import static com.xuexiang.xaop.consts.PermissionConsts.STORAGE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 扫描跳转Activity RequestCode
     */
    public static final int REQUEST_CODE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_start:
                loadPlugin();
                break;
            default:
                break;
        }
    }

    @Permission(STORAGE)
    private void loadPlugin() {
        Intent intent = new Intent(MainActivity.this, PluginLoadActivity.class);
        intent.putExtra(Constant.KEY_PLUGIN_PART_KEY, Constant.PART_KEY_PLUGIN_MAIN_APP);
        intent.putExtra(Constant.KEY_ACTIVITY_CLASSNAME, "com.xuexiang.xqrcode.ui.CaptureActivity");
        intent.putExtra(Constant.KEY_ACTIVITY_REQUEST_CODE, REQUEST_CODE);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理二维码扫描结果
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            //处理扫描结果（在界面上显示）
            handleScanResult(data);
        }
    }

    /**
     * 处理二维码扫描结果
     * @param data
     */
    private void handleScanResult(Intent data) {
        if (data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                if (bundle.getInt("result_type") == 1) {
                    String result = bundle.getString("result_data");
                    ToastUtils.toast("解析结果:" + result, Toast.LENGTH_LONG);
                } else if (bundle.getInt("result_type") == 2) {
                    ToastUtils.toast("解析二维码失败", Toast.LENGTH_LONG);
                }
            }
        }
    }
}
