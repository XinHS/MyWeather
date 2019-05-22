package com.example.myweather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MyPositionActivity extends AppCompatActivity {
    public LocationClient mlocationClient;
    private TextView positionText;
    private MapView mapView;
    private BaiduMap baiduMap;
    private boolean isFirstLocation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mlocationClient = new LocationClient(getApplicationContext());//LocationClient构建方法接收Context参数，getApplicationContext获取一个全局Context参数
        mlocationClient.registerLocationListener(new MyPositionActivity.MyLocationListener());//注册定位监听器，获取到位置信息时会回调
        SDKInitializer.initialize(getApplicationContext());//初始化操作，一定要在setContentView之前调用，否则会出错
        setContentView(R.layout.my_position);
        mapView = (MapView) findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);//开启显示位置功能
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        positionText = (TextView) findViewById(R.id.position_text_view);
        List<String> permissionList = new ArrayList<>();
        //ACCESS_COARSE_LOCATION、ACCESS_FINE_LOCATION、READ_PHONE_STATE、WRITE_EXTERNAL_STORAGE都是危险权限，而前两个属于同一个权限组（任取一个），所以只需申请三个权限
        //使用List集合一次性申请三个权限
        if (ContextCompat.checkSelfPermission(MyPositionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MyPositionActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MyPositionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);//将List转换成数组
            ActivityCompat.requestPermissions(MyPositionActivity.this, permissions, 1);//使用此方法一次性申请
        } else {
            requestLocation();
        }

    }
    private void navigateTo(BDLocation bdLocation){
        if (isFirstLocation){//防止多次调用animateMapStatus，移动到当前位置只需程序第一次定位时调用一次即可
            LatLng latLng = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
            baiduMap.animateMapStatus(update);//将地图移动到指定经纬度上
            update = MapStatusUpdateFactory.zoomTo(16f);//zoomTo方法接收一个float型参数，设置缩放级别，返回一个MapStatusUpdate对象
            baiduMap.animateMapStatus(update);//完成缩放
            isFirstLocation = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(bdLocation.getLatitude());
        locationBuilder.longitude(bdLocation.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);

    }

    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mlocationClient.stop();//停止定位
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);//关闭此功能
    }


    private void requestLocation() {
        initLocation();
        mlocationClient.start();//开始定位，定位的结果会回调到监听器中
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);//设置更新间隔，5s更新一下当前位置
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        mlocationClient.setLocOption(option);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {//通过循环判断每一个申请权限
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
//            StringBuilder currentPosition = new StringBuilder();
//            currentPosition.append("纬度：").append(bdLocation.getLatitude()).append("\n");
////            currentPosition.append("经度：").append(bdLocation.getLongitude()).append("\n");
////            currentPosition.append("国家：").append(bdLocation.getCountry()).append("\n");
////            currentPosition.append("省：").append(bdLocation.getProvince()).append("\n");
////            currentPosition.append("市：").append(bdLocation.getCity()).append("\n");
////            currentPosition.append("区：").append(bdLocation.getDistrict()).append("\n");
////            currentPosition.append("街道：").append(bdLocation.getStreet()).append("\n");
////            currentPosition.append("定位方式：");
////            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
////                currentPosition.append("GPS");
////            } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
////                currentPosition.append("网络");
////            }
////
////            positionText.setText(currentPosition);
        if (bdLocation.getLocType() == BDLocation.TypeGpsLocation || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
            navigateTo(bdLocation);
        }
        }
    }
}
