package com.htyd.fan.om.attendmanage.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.attendmanage.adapter.AttendCalendarGridAdapter;
import com.htyd.fan.om.main.MainActivity;
import com.htyd.fan.om.map.LocationReceiver;
import com.htyd.fan.om.map.OMLocationManager;
import com.htyd.fan.om.model.AttendBean;
import com.htyd.fan.om.model.DateBean;
import com.htyd.fan.om.model.OMLocationBean;
import com.htyd.fan.om.util.base.Preferences;
import com.htyd.fan.om.util.base.ThreadPool;
import com.htyd.fan.om.util.base.Utils;
import com.htyd.fan.om.util.db.OMUserDatabaseHelper.AttendCursor;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.db.SQLSentence;
import com.htyd.fan.om.util.fragment.AddressListDialog;
import com.htyd.fan.om.util.fragment.AddressListDialog.ChooseAddressListener;
import com.htyd.fan.om.util.fragment.InputDialogFragment;
import com.htyd.fan.om.util.fragment.InputDialogFragment.InputDoneListener;
import com.htyd.fan.om.util.fragment.SelectLocationDialogFragment;
import com.htyd.fan.om.util.fragment.SelectLocationDialogFragment.SelectLocationListener;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.https.Utility;
import com.htyd.fan.om.util.loaders.SQLiteCursorLoader;
import com.htyd.fan.om.util.ui.TextViewWithBorder;
import com.htyd.fan.om.util.ui.UItoolKit;

@Deprecated
public class AttendCalendarFragment extends Fragment implements
		SelectLocationListener, ChooseAddressListener, InputDoneListener {

	public static final int MESSAGESUCCESSID = 0x01;
	public static final int MESSAGEFAILID = 0x02;
	
	private static final String MONTHNUM = "monthnum";
	private static final int ATTENDLOADERID = 12;

	private List<DateBean> monthList;// 日历显示的内容
	private LoaderManager mLoadManager;
	private AttendLoaderCallback mCallback;
	private GridView monthGridView;
	private TextView attendTime, attendLocation, attendState,month,attendRemark;
	private ImageButton refreshImgButton;
	private Button signButton;
	private boolean isFinish;// 数据是否加载完成
	protected SparseArray<AttendBean> currentMonthAttendMap;//签到的Map, 0到当前天       0 为1号
	protected SparseArray<AttendBean> otherMonthAttendMap;
	protected Calendar selectDay;
	protected PopupMenu popupMenu;
	private boolean isLocation;
	private String location;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Calendar c = Calendar.getInstance();
		selectDay = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
		isFinish = false;
		monthList = new ArrayList<DateBean>();
		getMonth(c);
		mLoadManager = getActivity().getLoaderManager();
		mCallback = new AttendLoaderCallback();
		Bundle args = new Bundle();
		args.putInt(MONTHNUM, Calendar.getInstance().get(Calendar.MONTH));
		mLoadManager.initLoader(ATTENDLOADERID, args, mCallback);
		initMap(selectDay.get(Calendar.DATE));
		isLocation = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_main_page, container, false);
		intiView(v);
		return v;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(mLocationReceiver,new IntentFilter(OMLocationManager.ACTION_LOCATION));
		if (!isLocation && Utils.isNetWorkEnable()) {
			OMLocationManager.get(getActivity()).setLocCilentOption(null);
			OMLocationManager.get(getActivity()).startLocationUpdate();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(mLocationReceiver);
		OMLocationManager.get(getActivity()).stopLocationUpdate();
		super.onPause();
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}

	private void intiView(View v) {
		SigninClickListener mListener = new SigninClickListener();
		GridView mGridView = (GridView) v.findViewById(R.id.grid_week_chinese);
		monthGridView = (GridView) v.findViewById(R.id.grid_attend_calendar);
		mGridView.setAdapter(new MainPageGridAdapter());
		monthGridView.setAdapter(new AttendCalendarGridAdapter(monthList,
				getActivity()));
		((AttendCalendarGridAdapter)monthGridView.getAdapter()).setSelectPos(selectDay.get(Calendar.DATE) - 1);
		monthGridView.setOnItemClickListener(new OnDayClickListener());
		month = (TextView) v.findViewById(R.id.tv_month);
		month.setText(selectDay.get(Calendar.YEAR) + "年"
				+ (selectDay.get(Calendar.MONTH) + 1) + "月");
		attendTime = (TextView) v.findViewById(R.id.tv_attend_time);
		attendLocation = (TextView) v.findViewById(R.id.tv_attend_address);
		attendState = (TextView) v.findViewById(R.id.tv_attend_state);
		signButton = (Button) v.findViewById(R.id.btn_add_attend);
		attendRemark = (TextView) v.findViewById(R.id.tv_attend_remark);
		refreshImgButton  = (ImageButton) v.findViewById(R.id.img_refresh);
		refreshImgButton.setOnClickListener(mListener);
		signButton.setOnClickListener(mListener);
		attendLocation.setOnClickListener(mListener);
		month.setOnClickListener(mListener);
		attendRemark.setOnClickListener(mListener);
		popupMenu = new PopupMenu(getActivity(), attendLocation);
		popupMenu.inflate(R.menu.select_address_menu);
		popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case R.id.menu_select_common:
					AddressListDialog dialogList = new AddressListDialog(AttendCalendarFragment.this);
					dialogList.show(getActivity().getFragmentManager(), null);
					return true;
				case R.id.menu_select_all:
					SelectLocationDialogFragment dialog = new SelectLocationDialogFragment();
					dialog.setListener(AttendCalendarFragment.this);
					dialog.show(getActivity().getFragmentManager(), null);
					return true;
				default:
					return true;
				}
			}
		});
	}
	
	private void initMap(int count){
		currentMonthAttendMap = new SparseArray<AttendBean>(count);
		for(int i = 0;i <  count;i++){
			currentMonthAttendMap.put(i, null);
		}
	}

	
	@SuppressLint("HandlerLeak")
	private class AttendHandler extends Handler {

		@Override
		public void dispatchMessage(Message msg) {

			switch (msg.what) {
			case MESSAGESUCCESSID:
				String result = (String) msg.obj;
				monthList.clear();
				Calendar c = Calendar.getInstance();
				c.set(2015, 0, 1);
				getMonth(c);
				int count =  c.getActualMaximum(Calendar.DAY_OF_MONTH);
				if (otherMonthAttendMap == null) {
					otherMonthAttendMap = new SparseArray<AttendBean>();
				} else {
					otherMonthAttendMap.clear();
				}
				for(int i = 0 ;i < count;i++){
					otherMonthAttendMap.put(i, null);
				}
				try {
					JSONObject	resultJson = new JSONObject(result);
					JSONArray array = (JSONArray) new JSONTokener(
							resultJson.getString("Rows")).nextValue();
					int jsonCount = array.length();
					int pos;
					for (int i = 0; i < jsonCount; i++) {
						AttendBean mBean = new AttendBean();
						mBean.setFromJson(array.getJSONObject(i));
						pos = Utils.getCalendarField(mBean.time,Calendar.DAY_OF_WEEK) - 1;
						otherMonthAttendMap.put(pos, mBean);
						monthList.get(pos).attendState = mBean.state;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case MESSAGEFAILID:
				UItoolKit.showToastShort(getActivity(), "加载考勤信息失败");
				break;
			default:
				super.dispatchMessage(msg);
			}
		}
	}

	private class SigninClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_add_attend:
				if(TextUtils.isEmpty(attendLocation.getText())){
					UItoolKit.showToastShort(getActivity(), "先选择签到位置");
					return ;
				}
				signButton.setText("正在签到...");
				signButton.setEnabled(false);
				OMLocationManager.get(getActivity()).setLocCilentOption(null);
				OMLocationManager.get(getActivity()).startLocationUpdate();
				break;
			case  R.id.tv_attend_address:
				popupMenu.show();
				break;
			case R.id.img_refresh:
				new RefreshAttendTask().execute();
				v.setEnabled(false);
				break;
			case R.id.tv_attend_remark:
				InputDialogFragment fragment = (InputDialogFragment) InputDialogFragment.newInstance("签到备注", "输入备注信息");
				fragment.setListener(AttendCalendarFragment.this);
				fragment.show(getActivity().getFragmentManager(), null);
				break;
			}
		}
	}
	
	private class MainPageGridAdapter extends BaseAdapter {
		private String[] itemArray;
		private Resources r;

		public MainPageGridAdapter() {
			r = getActivity().getResources();
			itemArray = r.getStringArray(R.array.week_chinese);
		}

		@Override
		public int getCount() {
			return itemArray.length;
		}

		@Override
		public Object getItem(int position) {
			return itemArray[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder mHolder;
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.week_item_layout, null);
				mHolder = new ViewHolder(convertView);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			mHolder.setText((String) getItem(position));
			return convertView;
		}
	}

	private class ViewHolder {

		private TextViewWithBorder itemTextView;

		public ViewHolder(View v) {
			itemTextView = (TextViewWithBorder) v
					.findViewById(R.id.tv_item_weekday);
		}

		public void setText(String text) {
			itemTextView.setText(text);
		}
	}

	private void getMonth(Calendar c) {
//		List<DateBean> monthList;
		int currentMonth = c.get(Calendar.MONTH);
		int weeks = c.get(Calendar.WEEK_OF_MONTH);
		int currentYear = c.get(Calendar.YEAR);
		c.add(Calendar.DATE, -(c.get(Calendar.DAY_OF_WEEK) - 1));
		while(true){
			c.add(Calendar.DATE, 7);
			weeks++;
			if (c.get(Calendar.MONTH) > currentMonth	|| c.get(Calendar.YEAR) > currentYear){
				break;
			}
		}
		c.add(Calendar.DATE, -(weeks - 1) * 7);
		int size = (weeks - 1) * 7;
//		monthList = new ArrayList<DateBean>(size);
		for (int i = 0; i < size; i++) {
			DateBean mBean = new DateBean();
			mBean.day = c.get(Calendar.DATE);
			if (c.get(Calendar.MONTH) != currentMonth) {
				mBean.state = 0;
			} else {
				mBean.state = 1;
			}
			monthList.add(mBean);
			c.add(Calendar.DATE, 1);
		}
//		return monthList;
	}

	private class OnDayClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (!isFinish) {
				UItoolKit.showToastShort(getActivity(), "数据还没加载完!");
				return;
			}
			DateBean mBean = (DateBean) parent.getAdapter().getItem(position);
			if (mBean.state != 1) {
				UItoolKit.showToastShort(getActivity(), "看清楚日期在点啊!");
				return;
			}
			if (mBean.day > Calendar.getInstance().get(Calendar.DATE)) {
				UItoolKit.showToastShort(getActivity(), "穿越中。。。。");
				UItoolKit.showToastShort(getActivity(), "穿越失败。");
				return;
			}
			selectDay.set(Calendar.DATE, mBean.day);
			if (parent.getTag() != null) {
				((View) parent.getTag()).setBackgroundColor(getActivity()
						.getResources().getColor(R.color.activity_bg_color));
			}
			parent.setTag(view);
			view.setBackgroundColor(getActivity().getResources().getColor(
					R.color.orange));
			 setDetailView(currentMonthAttendMap.get(mBean.day - 1));
			((AttendCalendarGridAdapter) parent.getAdapter()).setSelectPos(position);
		}
	}

	private static class AttendBeanCursorLoader extends SQLiteCursorLoader {

		private OMUserDatabaseManager mManager;
		private int monthNum;

		public AttendBeanCursorLoader(Context context, int monthNum) {
			super(context);
			this.monthNum = monthNum;
			mManager = OMUserDatabaseManager.getInstance(context);
		}

		@Override
		protected Cursor loadCursor() {
			mManager.openDb(0);
			return mManager.queryAttendCursor(monthNum,Calendar.getInstance().get(Calendar.YEAR));
		}

		@Override
		protected Cursor loadFromNet() {
			String result = "";
			JSONObject param = new JSONObject();
			try {
				param.put("YHID", Preferences.getUserinfo(getContext(), "YHID"));
				if (monthNum >= 9) {
					param.put("QDRQ", Calendar.getInstance().get(Calendar.YEAR)
							+ "-" + (monthNum + 1));
				} else {
					param.put("QDRQ", Calendar.getInstance().get(Calendar.YEAR)
							+ "-0" + (monthNum + 1));
				}
				result = NetOperating.getResultFromNet(getContext(), param,
						Urls.SAVEATTENDURL, "Operate=getAllKqxxByyhidAndqdrq");
				Utility.handleAttend(mManager, result);
				if(new JSONObject(result).get("Rows").equals("0")){
					return null;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return loadCursor();
		}

	}

	private class AttendLoaderCallback implements LoaderCallbacks<Cursor> {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new AttendBeanCursorLoader(getActivity(),
					args.getInt(MONTHNUM));
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			AttendCursor attendCursor = (AttendCursor) data;
			if (attendCursor != null && attendCursor.moveToFirst()) {
				int pos;
				do {
					AttendBean tempBean = attendCursor.getAttend();
					pos = Utils.getCalendarField(tempBean.time, Calendar.DATE) - 1;
					currentMonthAttendMap.put(pos, tempBean);
					monthList.get(pos).attendState = tempBean.state;
				} while (attendCursor.moveToNext());
				isFinish = true;
				((AttendCalendarGridAdapter)monthGridView.getAdapter()).notifyDataSetChanged();
				setDetailView(currentMonthAttendMap.get(selectDay.get(Calendar.DATE) - 1));
//				OMUserDatabaseManager.getInstance(getActivity()).closeDb();
			} else {
				setDetailView(null);
				UItoolKit.showToastShort(getActivity(), "还没有可加载的数据");
				isFinish = true;
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
	}

protected	void setGridView(AttendBean attendBean, int pos) {
		View v = monthGridView.getChildAt(pos);
		if(v == null){
			return;
		}
		TextView textView = (TextView) v.findViewById(R.id.tv_item_weekday);
		if (attendBean.state == 1) {
			textView.setTextColor(getActivity().getResources().getColor(R.color.green));
		} else if (attendBean.state == 2) {
			textView.setTextColor(getActivity().getResources().getColor(R.color.blue));
		}
	}

	protected void setDetailView(AttendBean mBean) {
		if (mBean == null) {
			attendState.setText("未签到");
			attendTime.setText(Utils.formatTime(selectDay.getTimeInMillis(), "yyyy年MM月dd日"));
			attendLocation.setText(location);
			return;
		}
		attendTime.setText(Utils.formatTime(mBean.time,"yyyy年MM月dd日"));
		if(mBean.choseLocation.length() > 0){
			attendLocation.setText(mBean.choseLocation);
		} else {
			attendLocation.setText(location);
		}
		if(mBean.state == 1){
			attendState.setText("已签到");
		}else if(mBean.state == 2){
			attendState.setText("补签");
		}
		attendRemark.setText(mBean.attendRemark);
	}

	private BroadcastReceiver mLocationReceiver = new LocationReceiver() {

		@Override
		protected void onProviderEnabledChanged(boolean enabled) {
		}

		@Override
		protected void onNetWorkLocationReceived(Context context,
				OMLocationBean loc) {
			if(MainActivity.currentPos != 0){
				return;
			}
			AttendBean mBean = new AttendBean();
			mBean.SetValueBean(loc);
			location = loc.province+loc.city+loc.district;
			if(!isLocation){
				if(attendLocation.getText().length() == 0){
					attendLocation.setText(location);
				}
				isLocation  = true;
				OMLocationManager.get(getActivity()).stopLocationUpdate();
				return;
			}
			if(!attendTime.getText().toString().equals(Utils.formatTime(loc.time, "yyyy年MM月dd日"))){
				mBean.state = 2;
			}else{
				mBean.state = 1;
			}
			mBean.time = selectDay.getTimeInMillis();
			mBean.choseLocation = attendLocation.getText().toString();
			mBean.month = selectDay.get(Calendar.MONTH);
			mBean.year = selectDay.get(Calendar.YEAR);
			mBean.attendRemark = attendRemark.getText().toString();
			OMLocationManager.get(getActivity()).stopLocationUpdate();
			startTask(mBean);
		}
		
		@Override
		protected void onNetDisableReceived(Context context) {
			UItoolKit.showToastShort(getActivity(), "无网络连接时不能签到");
			OMLocationManager.get(getActivity()).stopLocationUpdate();
			signButton.setText("签到");
			signButton.setEnabled(true);
		}
	};

	private class SaveAttendTask extends AsyncTask<AttendBean, Void, String> {

		AttendBean mBean;

		@Override
		protected String doInBackground(AttendBean... params) {
			mBean = params[0];
			String result;
			try {
				JSONObject param = mBean.toJson();
				param.put("YHID",
						Preferences.getUserinfo(getActivity(), "YHID"));
				result = NetOperating.getResultFromNet(getActivity(), param,
						Urls.SAVEATTENDURL, "Operate=saveKqxx");
			} catch (JSONException e) {
				e.printStackTrace();
				return "FALSE";
			} catch (InterruptedException e) {
				e.printStackTrace();
				return "FALSE";
			} catch (ExecutionException e) {
				e.printStackTrace();
				return "FALSE";
			} catch (Exception e) {
				e.printStackTrace();
				return "FALSE";
			}
			JSONObject json;
			try {
				json = new JSONObject(result);
				return json.getString("RESULT");
			} catch (JSONException e) {
				e.printStackTrace();
				return "FALSE";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("TRUE")) {
				OMUserDatabaseManager.getInstance(getActivity()).openDb(1);
				ThreadPool.runMethod(new Runnable() {
				@Override
					public void run() {
						OMUserDatabaseManager.getInstance(getActivity()).insertAttendBean(mBean);
					}
				});
				if (mBean.state == 1) {
					UItoolKit.showToastShort(getActivity(), "签到成功");
				} else {
					UItoolKit.showToastShort(getActivity(), "补签成功");
				}
				attendState.setText("已签到");
				int pos = Utils.getCalendarField(mBean.time, Calendar.DAY_OF_WEEK) - 1;
				monthList.get(pos).attendState = mBean.state;
				((AttendCalendarGridAdapter)monthGridView.getAdapter()).notifyDataSetChanged();
				setGridView(mBean,pos);
				currentMonthAttendMap.put(pos, mBean);
			} else if (result.equals("REPEAT")){
				UItoolKit.showToastShort(getActivity(), "重复签到");
			} else if (result.equals("REPEAT_TRUE")){
				UItoolKit.showToastShort(getActivity(), "签到又成功");
				int pos = Utils.getCalendarField(mBean.time, Calendar.DATE) - 1;
				int id = currentMonthAttendMap.get(pos).attendId;
				mBean.attendId = id;
				currentMonthAttendMap.put(pos, mBean);
				ThreadPool.runMethod(new Runnable() {
					@Override
						public void run() {
							OMUserDatabaseManager.getInstance(getActivity()).updateAttend(mBean);
						}
					});
			} else {
				UItoolKit.showToastShort(getActivity(), "保存至网络错误");
			}
			signButton.setEnabled(true);
			signButton.setText("签到");
			stopTask(this);
		}

	}

	protected void startTask(AttendBean mBean) {
		new SaveAttendTask().execute(mBean);
	}

	protected void stopTask(SaveAttendTask task) {
		task.cancel(false);
	}

	@Override
	public void OnSelectLocation(String location) {
		attendLocation.setText(location);
	}

	@Override
	public void onAddressChoose(String address) {
		attendLocation.setText(address);
	}
	
	private class RefreshAttendTask extends AsyncTask<Void, Void, Boolean>{

		private OMUserDatabaseManager mManger;
		
		public RefreshAttendTask() {
			super();
			mManger = OMUserDatabaseManager.getInstance(getActivity());
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			String result = "";
			JSONObject param = new JSONObject();
			Calendar c = Calendar.getInstance();
			try {
				param.put("YHID", Preferences.getUserinfo(getActivity(), "YHID"));
				param.put("QDRQ", c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1));
				result = NetOperating.getResultFromNet(getActivity(), param,
						Urls.SAVEATTENDURL, "Operate=getAllKqxxByyhidAndqdrq");
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			mManger.clearFeedTable(SQLSentence.TABLE_CHECK);
			try {
				Utility.handleAttend(mManger, result);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Bundle args = new Bundle();
				args.putInt(MONTHNUM, Calendar.getInstance()
						.get(Calendar.MONTH));
				currentMonthAttendMap.clear();
				resetAttendList();
				mLoadManager.restartLoader(ATTENDLOADERID, args, mCallback);
				UItoolKit.showToastShort(getActivity(), "刷新成功");
			} else {
				UItoolKit.showToastShort(getActivity(), "加载数据失败");
			}
			refreshImgButton.setEnabled(true);
			cancel(false);
		}
	}
	
	protected void resetAttendList(){
		int length = monthList.size();
		for (int i = 0; i < length; i++) {
			monthList.get(i).attendState = 0;
		}
	}

	@Override
	public void onInputDone(String text) {
 		attendRemark.setText(text);
	}
}
