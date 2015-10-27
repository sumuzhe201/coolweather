package com.coolweather.app.activity;

import com.coolweather.app.R;
//import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener{

	private LinearLayout weatherInfoLayout;
	/**
	 * 鐢ㄤ簬鏄剧ず鍩庡競鍚�
	 */
	private TextView cityNameText;
	/**
	 * 鐢ㄤ簬鏄剧ず鍙戝竷鏃堕棿
	 */
	private TextView publishText;
	/**
	 * 鐢ㄤ簬鏄剧ず澶╂皵鎻忚堪淇℃伅
	 */
	private TextView weatherDespText;
	/**
	 * 鐢ㄤ簬鏄剧ず姘旀俯1
	 */
	private TextView temp1Text;
	/**
	 * 鐢ㄤ簬鏄剧ず姘旀俯2
	 */
	private TextView temp2Text;
	/**
	 * 鐢ㄤ簬鏄剧ず褰撳墠鏃ユ湡
	 */
	private TextView currentDateText;
	/**
	 * 鍒囨崲鍩庡競鎸夐挳
	 */
//	private Button switchCity;
	/**
	 * 鏇存柊澶╂皵鎸夐挳
	 */
//	private Button refreshWeather;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		// 鍒濆鍖栧悇鎺т欢
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
	//	switchCity = (Button) findViewById(R.id.switch_city);
	//	refreshWeather = (Button) findViewById(R.id.refresh_weather);
		String countyCode = getIntent().getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCode)) {
			// 鏈夊幙绾т唬鍙锋椂灏卞幓鏌ヨ澶╂皵
			publishText.setText("同步中....");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else {
			// 娌℃湁鍘跨骇浠ｅ彿鏃跺氨鐩存帴鏄剧ず鏈湴澶╂皵
			showWeather();
		}
	//	switchCity.setOnClickListener(this);
	//	refreshWeather.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
	//	switch (v.getId()) {
	//	case R.id.switch_city:
	//		Intent intent = new Intent(this, ChooseAreaActivity.class);
	//		intent.putExtra("from_weather_activity", true);
	//		startActivity(intent);
	//		finish();
	//		break;
	//	case R.id.refresh_weather:
	//		publishText.setText("同步中....");
	//		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	//		String weatherCode = prefs.getString("weather_code", "");
	//		if (!TextUtils.isEmpty(weatherCode)) {
	//			queryWeatherInfo(weatherCode);
	//		}
	//		break;
	//	default:
	//		break;
	//	}
	}
	
	/**
	 * 鏌ヨ鍘跨骇浠ｅ彿鎵�瀵瑰簲鐨勫ぉ姘斾唬鍙枫��
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}

	/**
	 * 鏌ヨ澶╂皵浠ｅ彿鎵�瀵瑰簲鐨勫ぉ姘斻��
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}
	
	/**
	 * 鏍规嵁浼犲叆鐨勫湴鍧�鍜岀被鍨嬪幓鍚戞湇鍔″櫒鏌ヨ澶╂皵浠ｅ彿鎴栬�呭ぉ姘斾俊鎭��
	 */
	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(final String response) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						// 浠庢湇鍔″櫒杩斿洖鐨勬暟鎹腑瑙ｆ瀽鍑哄ぉ姘斾唬鍙�
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if ("weatherCode".equals(type)) {
					// 澶勭悊鏈嶅姟鍣ㄨ繑鍥炵殑澶╂皵淇℃伅
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							showWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						publishText.setText("同步失败");
					}
				});
			}
		});
	}
	
	/**
	 * 浠嶴haredPreferences鏂囦欢涓鍙栧瓨鍌ㄧ殑澶╂皵淇℃伅锛屽苟鏄剧ず鍒扮晫闈笂銆�
	 */
	private void showWeather() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText( prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	//	Intent intent = new Intent(this, AutoUpdateService.class);
	//	startService(intent);
	}

}