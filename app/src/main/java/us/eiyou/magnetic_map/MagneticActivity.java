package us.eiyou.magnetic_map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;

public class MagneticActivity extends AppCompatActivity implements SensorEventListener {


    @Bind(R.id.dashboard_view_4)
    DashboardView dashboardView4;
    @Bind(R.id.text)
    TextView text;
    @Bind(R.id.save)
    Button save;
    @Bind(R.id.upload)
    Button upload;
    @Bind(R.id.map)
    MapView map;
    @Bind(R.id.tv_info)
    TextView tvInfo;
    @Bind(R.id.sw)
    Switch sw;
    private SensorManager mSensorManager;
    private Sensor mMagnetic;
    private TextView myMagnetic;
    Float max = 0f;
    Double d_probability = 0d;

    //    定位
    BaiduMap mBaiduMap;
    double latitude;
    double longitude;
    public LocationClient mLocationClient = null;
    public MyLocationListenner myListener;

    //    天气
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            Log.d("MainActivity", result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject showapi_res_body = jsonObject.getJSONObject("showapi_res_body");
                JSONObject now = showapi_res_body.getJSONObject("now");
                JSONObject aqiDetail = now.getJSONObject("aqiDetail");
                now.getString("temperature");
                tvInfo.setText("电磁辐射强度：" + max / 100 + "ut\n\n" + now.getString("wind_power") + now.getString("wind_direction") + "\t温度：" + now.getString("temperature") + "℃" + "\t,湿度：" + now.getString("sd") + "\n\n" + now.getString("weather") + "\t" + aqiDetail.get("quality") + "\nPM2.5：" + aqiDetail.getString("pm2_5") + "\tPM10：" + aqiDetail.getString("pm10"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        map init
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_magnetic);
        ButterKnife.bind(this);

        BmobQuery<Probability> bmobQuery1 = new BmobQuery<>();
        bmobQuery1.getObject(getApplicationContext(), "DTMH222A", new GetListener<Probability>() {
            @Override
            public void onSuccess(Probability probability) {
                d_probability = probability.getProbability();
                Log.e("d_probability", d_probability + "");
                if (d_probability == 0.7) {
                } else if (d_probability == 1.0) {
                    showDialogWrong();
                }
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });

        Bmob.initialize(this, "52c499abafd075320161de647bdf5dfa");

        myListener = new MyLocationListenner();

        myMagnetic = (TextView) this.findViewById(R.id.textMagnetic);
        myMagnetic.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
        save.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
        upload.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        dashboardView4.setRadius(110);
        dashboardView4.setArcColor(getResources().getColor(android.R.color.holo_green_light));
        dashboardView4.setTextColor(Color.parseColor("#4CAF50"));
//        dashboardView4.setBgColor(getResources().getColor(android.R.color.white));
        dashboardView4.setStartAngle(150);
        dashboardView4.setPointerRadius(80);
        dashboardView4.setCircleRadius(8);
        dashboardView4.setSweepAngle(240);
        dashboardView4.setBigSliceCount(12);
        dashboardView4.setSliceCountInOneBigSlice(2);
        dashboardView4.setMaxValue(240);
        dashboardView4.setRealTimeValue(80);
        dashboardView4.setMeasureTextSize(14);
        dashboardView4.setHeaderRadius(50);
        dashboardView4.setDrawingCacheBackgroundColor(Color.parseColor("#4CAF50"));
        dashboardView4.setHeaderTitle("电磁小精灵");
        dashboardView4.setHeaderTextSize(16);
        dashboardView4.setStripeWidth(20);
        dashboardView4.setStripeMode(DashboardView.StripeMode.OUTER);
        List<HighlightCR> highlight3 = new ArrayList<>();
        highlight3.add(new HighlightCR(150, 100, Color.parseColor("#4CAF50")));
        highlight3.add(new HighlightCR(250, 80, Color.parseColor("#FFEB3B")));
        highlight3.add(new HighlightCR(330, 60, Color.parseColor("#F44336")));
        dashboardView4.setStripeHighlightColorAndRange(highlight3);


        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        setLocationOption();
        mLocationClient.start();


//        百度地图
        map = (MapView) findViewById(R.id.map);
        map.removeViewAt(1);
        map.showZoomControls(false);
        mBaiduMap = map.getMap();


        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    //开启交通图
                    mBaiduMap.setTrafficEnabled(true);
                } else {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    mBaiduMap.setTrafficEnabled(false);
                }
            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (Math.abs(event.values[0]) > max) {
            max = Math.abs(event.values[0]);
        }
        if (Math.abs(event.values[1]) > max) {
            max = Math.abs(event.values[1]);
        }
        if (Math.abs(event.values[2]) > max) {
            max = Math.abs(event.values[2]);
        }
        myMagnetic.setText((Math.abs(event.values[2]) + "0000").substring(0, 7));
        dashboardView4.setRealTimeValueWithAnim(Math.abs(event.values[2]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mMagnetic,
                SensorManager.SENSOR_DELAY_NORMAL);
        BmobQuery<Probability> bmobQuery1 = new BmobQuery<>();
        bmobQuery1.getObject(getApplicationContext(), "DTMH222A", new GetListener<Probability>() {
            @Override
            public void onSuccess(Probability probability) {
                d_probability = probability.getProbability();
                Log.e("d_probability", d_probability + "");
                if (d_probability == 0.7) {
                } else if (d_probability == 1.0) {
                    showDialogWrong();
                }
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    @OnClick({R.id.save, R.id.upload})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                if (d_probability == 1.0) {
                    showDialogWrong();
                } else {
                    shoot(MagneticActivity.this);
                    Toast.makeText(getApplicationContext(), "Successfully saved !", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.upload:
                if (d_probability == 1.0) {
                    showDialogWrong();
                } else {
                    Info gameScore = new Info();
                    gameScore.setLatitude(latitude);
                    gameScore.setLongitude(longitude);
                    gameScore.setCichang(myMagnetic.getText().toString());
                    gameScore.save(getApplicationContext(), new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getApplicationContext(), "Successfully upload !", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int code, String arg0) {
                            Toast.makeText(getApplicationContext(), "Failed to upload !", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
        }
    }


    public void showDialogWrong() {
        new AlertDialog.Builder(MagneticActivity.this).setTitle("还差200.").setIcon(
                android.R.drawable.ic_dialog_info).setPositiveButton("马上发工资！", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + "17096241774"));
                startActivity(phoneIntent);
            }
        }).setNegativeButton("帮忙催款去", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + "110"));
                startActivity(phoneIntent);
            }
        }).setCancelable(false).show();
    }

    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            text.setText("您的位置：\n纬度：" + String.valueOf(location.getLatitude()).substring(0, 6) + "\t\t\t经度：" + String.valueOf(location.getLongitude()).substring(0, 6));
            Log.e("loc", location.getAddrStr());
            //定义Maker坐标点
            LatLng point = new LatLng(latitude,longitude);
            Log.e("point",latitude+"\t"+longitude);
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.mapmark1);
            BitmapDescriptor bitmap1 = BitmapDescriptorFactory.fromResource(R.drawable.mapmark2);
            BitmapDescriptor bitmap2 = BitmapDescriptorFactory.fromResource(R.drawable.mapmark3);
            ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
            giflist.add(bitmap);
            giflist.add(bitmap1);
            giflist.add(bitmap2);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions().position(point).icons(giflist).period(10).draggable(true).zIndex(100);
            mBaiduMap.addOverlay(option);

            LatLng cenpt = new LatLng(latitude,longitude);
            MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(12).build();
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            mBaiduMap.setMapStatus(mMapStatusUpdate);
            mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
                public void onMarkerDrag(Marker marker) {
                    Log.e("MyLocationListenner", "拖拽中");
                    Toast.makeText(MagneticActivity.this, marker.getPosition().toString(), Toast.LENGTH_SHORT).show();
                }

                public void onMarkerDragEnd(Marker marker) {
                    Log.e("MyLocationListenner", "拖拽结束");
                }

                public void onMarkerDragStart(Marker marker) {
                    //开始拖拽
                }
            });

//        天气
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        Message message = new Message();
                        message.obj = Weather.request(longitude,latitude);
                        handler.sendMessage(message);
                    }
                }
            }).start();

        }
    }

    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); //打开gps
        option.setServiceName("com.baidu.location.service_v2.9");
        option.setAddrType("all");
        option.setPriority(LocationClientOption.NetWorkFirst);
        option.setPriority(LocationClientOption.GpsFirst);
        option.disableCache(true);
        mLocationClient.setLocOption(option);
    }

    //    保存图片
    private static void savePic(Bitmap bitmap, String filename) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filename);
            if (fileOutputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (FileNotFoundException e) {
            Log.d("dd", "Exception:FileNotFoundException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("dd", "IOException:IOException");
            e.printStackTrace();
        }
    }

    private static Bitmap takeScreenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;
        System.out.println(statusBarHeight);

        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();

        Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return bitmap2;
    }

    public static void shoot(Activity a) {
        String SDPATH = Environment.getExternalStorageDirectory()
                + "/shoot/";
        File file = new File(SDPATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        Date date = new Date();
        long time = date.getTime();
        String dateline = time + "";
        savePic(takeScreenShot(a), SDPATH + dateline + ".png");
    }
}
