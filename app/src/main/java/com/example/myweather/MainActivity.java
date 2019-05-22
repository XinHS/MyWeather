package com.example.myweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    RequestQueue queue = null;
    EditText et_location;
    TextView tv_city, tv_date, tv_max_temp, tv_min_temp, tv_temp, tv_cond, tv_dir, tv_comf, tv_suggest,
            tv_pre_day1_maxtmp, tv_pre_day1_mintmp, tv_pre_day1_cond, tv_pre_day1_wind,
            tv_pre_day2_maxtmp, tv_pre_day2_mintmp, tv_pre_day2_cond, tv_pre_day2_wind,
            tv_pre_day3_maxtmp, tv_pre_day3_mintmp, tv_pre_day3_cond, tv_pre_day3_wind,
            title_pre, title_pre_day1, title_pre_day2, title_pre_day3;
    String location;
    Button B_plus;
    Button my_button;
    LinearLayoutCompat ly_container;
    String arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (prefs.getString("weather", null) != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_main);
        ly_container= (LinearLayoutCompat) findViewById(R.id.ly_container);

        queue = Volley.newRequestQueue(this);
        et_location = (EditText) findViewById(R.id.et_location);
        tv_city = (TextView) findViewById(R.id.id_tv_city);
        tv_date = (TextView) findViewById(R.id.id_tv_date);
        tv_max_temp = (TextView) findViewById(R.id.id_tv_max_temp);
        tv_min_temp = (TextView) findViewById(R.id.id_tv_min_temp);
        tv_temp = (TextView) findViewById(R.id.id_tv_temp);
        tv_cond = (TextView) findViewById(R.id.id_tv_cond);
        tv_dir = (TextView) findViewById(R.id.id_tv_dir);
        tv_comf = (TextView) findViewById(R.id.id_tv_comf);
        tv_suggest = (TextView) findViewById(R.id.id_tv_suggest);
         B_plus = findViewById(R.id.plus);

        title_pre = (TextView) findViewById(R.id.title_pre);
        title_pre_day1 = (TextView) findViewById(R.id.title_pre_day1);
        title_pre_day2 = (TextView) findViewById(R.id.title_pre_day2);
        title_pre_day3 = (TextView) findViewById(R.id.title_pre_day3);

        //未来三天天气
        tv_pre_day1_maxtmp = (TextView) findViewById(R.id.tv_pre_day1_maxtmp);
        tv_pre_day1_mintmp = (TextView) findViewById(R.id.tv_pre_day1_mintmp);
        tv_pre_day1_cond = (TextView) findViewById(R.id.tv_pre_day1_cond);
        tv_pre_day1_wind = (TextView) findViewById(R.id.tv_pre_day1_wind);

        tv_pre_day2_maxtmp = (TextView) findViewById(R.id.tv_pre_day2_maxtmp);
        tv_pre_day2_mintmp = (TextView) findViewById(R.id.tv_pre_day2_mintmp);
        tv_pre_day2_cond = (TextView) findViewById(R.id.tv_pre_day2_cond);
        tv_pre_day2_wind = (TextView) findViewById(R.id.tv_pre_day2_wind);

        tv_pre_day3_maxtmp = (TextView) findViewById(R.id.tv_pre_day3_maxtmp);
        tv_pre_day3_mintmp = (TextView) findViewById(R.id.tv_pre_day3_mintmp);
        tv_pre_day3_cond = (TextView) findViewById(R.id.tv_pre_day3_cond);
        tv_pre_day3_wind = (TextView) findViewById(R.id.tv_pre_day3_wind);
        my_button = findViewById(R.id.position_button);
        my_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MyPositionActivity.class);
                startActivity(intent);
            }
        });
        B_plus .setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Tracnsfer.class);
                startActivity(intent);


            }

        });
        Intent intent=getIntent();
        String name=intent.getStringExtra("weather_id");
        et_location.setText(name);

    }

    public void weatherClick(View view) {

        location = et_location.getText().toString();
        String url = "https://free-api.heweather.com/s6/weather?location=" + location + "&key=0754508fbe0f44be915c65a0182b8aa9";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                ly_container.setBackgroundResource(R.mipmap.weather03);
                title_pre.setVisibility(View.VISIBLE);
                title_pre_day1.setVisibility(View.VISIBLE);
                title_pre_day2.setVisibility(View.VISIBLE);
                title_pre_day3.setVisibility(View.VISIBLE);

                System.out.println(jsonObject);
                Gson gson = new Gson();
                WeatherBean weatherBean = gson.fromJson(jsonObject.toString(), WeatherBean.class);
                String cityName = weatherBean.getHeWeather6().get(0).getBasic().getLocation();
                String date = weatherBean.getHeWeather6().get(0).getDaily_forecast().get(0).getDate();
                String maxtemp = weatherBean.getHeWeather6().get(0).getDaily_forecast().get(0).getTmp_max();
                String mintemp = weatherBean.getHeWeather6().get(0).getDaily_forecast().get(0).getTmp_min();
                String temp = weatherBean.getHeWeather6().get(0).getNow().getTmp();
                String cond = weatherBean.getHeWeather6().get(0).getNow().getCond_txt();
                String dir = weatherBean.getHeWeather6().get(0).getNow().getWind_dir();
                String comf = weatherBean.getHeWeather6().get(0).getLifestyle().get(0).getBrf();
                String suggest = weatherBean.getHeWeather6().get(0).getLifestyle().get(0).getTxt();

                String day1_maxtmp=weatherBean.getHeWeather6().get(0).getDaily_forecast().get(0).getTmp_max();
                String day1_mintmp=weatherBean.getHeWeather6().get(0).getDaily_forecast().get(0).getTmp_min();
                String day1_cond=weatherBean.getHeWeather6().get(0).getDaily_forecast().get(0).getCond_txt_n();
                String day1_wind=weatherBean.getHeWeather6().get(0).getDaily_forecast().get(0).getWind_dir();

                String day2_maxtmp=weatherBean.getHeWeather6().get(0).getDaily_forecast().get(1).getTmp_max();
                String day2_mintmp=weatherBean.getHeWeather6().get(0).getDaily_forecast().get(1).getTmp_min();
                String day2_cond=weatherBean.getHeWeather6().get(0).getDaily_forecast().get(1).getCond_txt_n();
                String day2_wind=weatherBean.getHeWeather6().get(0).getDaily_forecast().get(1).getWind_dir();

                String day3_maxtmp=weatherBean.getHeWeather6().get(0).getDaily_forecast().get(2).getTmp_max();
                String day3_mintmp=weatherBean.getHeWeather6().get(0).getDaily_forecast().get(2).getTmp_min();
                String day3_cond=weatherBean.getHeWeather6().get(0).getDaily_forecast().get(2).getCond_txt_n();
                String day3_wind=weatherBean.getHeWeather6().get(0).getDaily_forecast().get(2).getWind_dir();

                tv_city.setText("城市：" + cityName);
                tv_date.setText("日期：" + date);
                tv_max_temp.setText("最高温度：" + maxtemp);
                tv_min_temp.setText("最低温度：" + mintemp);
                tv_temp.setText("实时温度：" + temp);
                tv_cond.setText("天气情况：" + cond);
                tv_dir.setText("风向：" + dir);
                tv_comf.setText("人体感觉：" + comf);
                tv_suggest.setText("建议：" + suggest);

                tv_pre_day1_maxtmp.setText("最高温度:" + day1_maxtmp);
                tv_pre_day1_mintmp.setText("最低温度:" + day1_mintmp);
                tv_pre_day1_cond.setText("天气:" + day1_cond);
                tv_pre_day1_wind.setText("风向:" + day1_wind);

                tv_pre_day2_maxtmp.setText("最高温度:" + day2_maxtmp);
                tv_pre_day2_mintmp.setText("最低温度:" + day2_mintmp);
                tv_pre_day2_cond.setText("天气:" + day2_cond);
                tv_pre_day2_wind.setText("风向:" + day2_wind);

                tv_pre_day3_maxtmp.setText("最高温度:" + day3_maxtmp);
                tv_pre_day3_mintmp.setText("最低温度:" + day3_mintmp);
                tv_pre_day3_cond.setText("天气:" + day3_cond);
                tv_pre_day3_wind.setText("风向:" + day3_wind);
                if (cond.equals("阴")){
                    ly_container.setBackgroundResource(R.mipmap.weather_ying);
                }else if (cond.equals("小雨")){
                    ly_container.setBackgroundResource(R.mipmap.weather_xiaoyu);
                }else if (cond.equals("晴")){
                    ly_container.setBackgroundResource(R.mipmap.weather_qing);
                }else if (cond.equals("大雨")){
                    ly_container.setBackgroundResource(R.mipmap.weather_xiaoyu);
                }else if (cond.equals("多云")){
                    ly_container.setBackgroundResource(R.mipmap.weather_duoyun);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println(volleyError);
            }
        });
        queue.add(request);
    }
}
