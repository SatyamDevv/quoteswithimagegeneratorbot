package com.satyamdevv.quotesgeneratorr;

import android.Manifest;
import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.*;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.*;
import android.graphics.*;
import android.graphics.Typeface;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.*;
import org.json.*;

public class MainActivity extends Activity {
	
	public final int REQ_CD_FP = 101;
	
	private Timer _timer = new Timer();
	
	private HashMap<String, Object> mapstr = new HashMap<>();
	private double position = 0;
	private double small = 0;
	private String imageLink = "";
	private HashMap<String, Object> quoteMap = new HashMap<>();
	private String quote = "";
	private double halfHeight = 0;
	private String author = "";
	private String quoteAPI = "";
	private String unsplashApiKey = "";
	
	private ArrayList<HashMap<String, Object>> quoteList = new ArrayList<>();
	
	private LinearLayout linear_main;
	private LinearLayout linear1;
	private LinearLayout linear_loading;
	private Button button1;
	private RelativeLayout linearwithimageandtext;
	private Button saveImg;
	private ImageView imageview;
	private LinearLayout textview;
	private TextView quote1;
	private TextView textAuthor;
	private TextView textview1;
	private ProgressBar progressbar2;
	
	private RequestNetwork get_image;
	private RequestNetwork.RequestListener _get_image_request_listener;
	private RequestNetwork getQuotes;
	private RequestNetwork.RequestListener _getQuotes_request_listener;
	private Intent fp = new Intent(Intent.ACTION_GET_CONTENT);
	private TimerTask loading;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);
		
		if (Build.VERSION.SDK_INT >= 23) {
			if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
			||checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
				requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
			} else {
				initializeLogic();
			}
		} else {
			initializeLogic();
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear_main = findViewById(R.id.linear_main);
		linear1 = findViewById(R.id.linear1);
		linear_loading = findViewById(R.id.linear_loading);
		button1 = findViewById(R.id.button1);
		linearwithimageandtext = findViewById(R.id.linearwithimageandtext);
		saveImg = findViewById(R.id.saveImg);
		imageview = findViewById(R.id.imageview);
		textview = findViewById(R.id.textview);
		quote1 = findViewById(R.id.quote1);
		textAuthor = findViewById(R.id.textAuthor);
		textview1 = findViewById(R.id.textview1);
		progressbar2 = findViewById(R.id.progressbar2);
		get_image = new RequestNetwork(this);
		getQuotes = new RequestNetwork(this);
		fp.setType("image/*");
		fp.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				get_image.startRequestNetwork(RequestNetworkController.GET, "https://api.unsplash.com/photos/random/?client_id=GsPhRwPpuhHSVW69LLngHKxuADmhKCjFgcKfLk_2MsI&query=motivation", "motivation", _get_image_request_listener);
				getQuotes.startRequestNetwork(RequestNetworkController.GET, quoteAPI, "random", _getQuotes_request_listener);
			}
		});
		
		saveImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				
				_viewToImage(linearwithimageandtext, FileUtil.getExternalStorageDir().concat("/Alarms".concat("/quote.jpg")));
			}
		});
		
		_get_image_request_listener = new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
				final String _tag = _param1;
				final String _response = _param2;
				final HashMap<String, Object> _responseHeaders = _param3;
				mapstr = new Gson().fromJson(_response, new TypeToken<HashMap<String, Object>>(){}.getType());
				imageLink = _getlinks(mapstr.get("urls").toString());
				Glide.with(getApplicationContext()).load(Uri.parse(imageLink)).into(imageview);
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
				final String _tag = _param1;
				final String _message = _param2;
				
			}
		};
		
		_getQuotes_request_listener = new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
				final String _tag = _param1;
				final String _response = _param2;
				final HashMap<String, Object> _responseHeaders = _param3;
				quoteMap = new Gson().fromJson(_response, new TypeToken<HashMap<String, Object>>(){}.getType());
				quote = quoteMap.get("content").toString();
				author = quoteMap.get("author").toString();
				quote1.setText(quote);
				textAuthor.setText(author);
				//this code create margin on text Accordingly to the image height
				
				int half = imageview.getHeight() / 3;
				
				
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textview.getLayoutParams();
				                            params.setMargins(0, half, 0, 0);
				                            //Resetting the TextView to above the button
				                          
				                            textview.setLayoutParams(params);
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
				final String _tag = _param1;
				final String _message = _param2;
				
			}
		};
	}
	
	private void initializeLogic() {
		quoteAPI = "https://api.quotable.io/random";
		//important
		// please use your own unsplash client id/api key 
		//my api key have limit of 50 request per hour so if the image not loading guess my limit is reached so use your own api key
		unsplashApiKey = "GsPhRwPpuhHSVW69LLngHKxuADmhKCjFgcKfLk_2MsI";
		
		//about quote api
		//change link of quotes api if not working to this link https://free-quotes-api.herokuapp.com/ and change the response key to content to quote
		//else use this api https://api.quotable.io/random this is best api and well maintained available on github for more info
		get_image.startRequestNetwork(RequestNetworkController.GET, "https://api.unsplash.com/photos/random/?client_id=".concat(unsplashApiKey.concat("&query=motivation")), "motivation", _get_image_request_listener);
		getQuotes.startRequestNetwork(RequestNetworkController.GET, quoteAPI, "random", _getQuotes_request_listener);
		// use any font according to your choice here
		quote1.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/courierprimebold.ttf"), 0);
		textAuthor.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/courierprimebold.ttf"), 0);
		linear1.setVisibility(View.GONE);
		loading = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						linear1.setVisibility(View.VISIBLE);
						linear_loading.setVisibility(View.GONE);
					}
				});
			}
		};
		_timer.schedule(loading, (int)(1000));
	}
	
	public String _getlinks(final String _text) {
		//this funtion just return the regular link of the image for the json return by the unsplash api
		int position = _text.indexOf("regular");
		int small = _text.indexOf("small");
		return (_text.substring((int)(position + 8), (int)(small - 2)));
	}
	
	
	public void _viewToImage(final View _save_view, final String _storage_place) {
		
		_save_view.setDrawingCacheEnabled(true); 
		Bitmap b = _save_view.getDrawingCache();
		try {
			java.io.File file = new java.io.File(_storage_place);
			java.io.OutputStream out = new java.io.FileOutputStream(file);
			b.compress(Bitmap.CompressFormat.JPEG, 50, out);
			out.flush();
			out.close();
		} catch (Exception e) { showMessage(e.toString()); }
		
		
		//this function save the view into image file
	}
	
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels() {
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels() {
		return getResources().getDisplayMetrics().heightPixels;
	}
}