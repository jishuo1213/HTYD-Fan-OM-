package com.htyd.fan.om.attendmanage.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.attendmanage.adapter.AttendCalendarGridAdapter;
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
import com.htyd.fan.om.util.fragment.AddressListDialog;
import com.htyd.fan.om.util.fragment.AddressListDialog.ChooseAddressListener;
import com.htyd.fan.om.util.fragment.SelectLocationDialogFragment;
import com.htyd.fan.om.util.fragment.SelectLocationDialogFragment.SelectLocationListener;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.https.Utility;
import com.htyd.fan.om.util.loaders.SQLiteCursorLoader;
import com.htyd.fan.om.util.ui.TextViewWithBorder;
import com.htyd.fan.om.util.ui.UItoolKit;

public class AttendCalendarFragment extends Fragment implements SelectLocationListener, ChooseAddressListener {

	private static final String MONTHNUM = "monthnum";
	private static final int ATTENDLOADERID = 12;

	private List<DateBean> monthList;// 日历显示的内容
	private LoaderManager mLoadManager;
	private AttendLoaderCallback mCallback;
	private GridView monthGridView;
	private TextView attendTime, attendLocation, attendState;
	private Button signButton;
	public static int currentSelect;// 当前选中的天的位置
	private int firstDayPosition;// 这个月一号的位置
	private boolean isFinish;// 数据是否加载完成
	protected SparseArray<AttendBean> attendMap;//签到的Map, 0 - 当前天------ 0 为1号
	protected Calendar selectDay;
	protected PopupMenu popupMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Calendar c = Calendar.getInstance();
		selectDay = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
		isFinish = false;
		monthList = getMonth(c);
		mLoadManager = getActivity().getLoaderManager();
		mCallback = new AttendLoaderCallback();
		Bundle args = new Bundle();
		args.putInt(MONTHNUM, Calendar.getInstance().get(Calendar.MONTH));
		mLoadManager.initLoader(ATTENDLOADERID, args, mCallback);
		initMap(currentSelect - firstDayPosition + 1);
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
	}

	@Override
	public void onStop() {
		getActivity().unregisterReceiver(mLocationReceiver);
		super.onStop();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		getActivity().registerReceiver(mLocationReceiver,new IntentFilter(OMLocationManager.ACTION_LOCATION));
	}

	private void intiView(View v) {
		SigninClickListener mListener = new SigninClickListener();
		GridView mGridView = (GridView) v.findViewById(R.id.grid_week_chinese);
		monthGridView = (GridView) v.findViewById(R.id.grid_attend_calendar);
		mGridView.setAdapter(new MainPageGridAdapter());
		monthGridView.setAdapter(new AttendCalendarGridAdapter(monthList,
				getActivity()));
		monthGridView.setOnItemClickListener(new OnDayClickListener());
		TextView month = (TextView) v.findViewById(R.id.tv_month);
		month.setText(selectDay.get(Calendar.YEAR) + "年"
				+ (selectDay.get(Calendar.MONTH) + 1) + "月");
		attendTime = (TextView) v.findViewById(R.id.tv_attend_time);
		attendLocation = (TextView) v.findViewById(R.id.tv_attend_address);
		attendState = (TextView) v.findViewById(R.id.tv_attend_state);
		signButton = (Button) v.findViewById(R.id.btn_add_attend);
		signButton.setOnClickListener(mListener);
		attendLocation.setOnClickListener(mListener);
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
		attendMap = new SparseArray<AttendBean>(count);
		for(int i = 0;i <  count;i++){
			attendMap.put(i, null);
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

	private List<DateBean> getMonth(Calendar c) {
		List<DateBean> monthList;
		int currentMonth = c.get(Calendar.MONTH);
		int weeks = c.get(Calendar.WEEK_OF_MONTH);
		int currentDate = c.get(Calendar.DATE);
		int currentYear = c.get(Calendar.YEAR);
		c.add(Calendar.DATE, -currentDate);
		firstDayPosition = c.get(Calendar.DAY_OF_WEEK);
		currentSelect = firstDayPosition + currentDate - 1;
		c.add(Calendar.DATE, currentDate);
		int week = 0;
		c.add(Calendar.DATE, -(c.get(Calendar.DAY_OF_WEEK) - 1));
		c.add(Calendar.DATE, -(weeks - 1) * 7);
		if (c.get(Calendar.YEAR) < currentYear) {
			while (c.get(Calendar.MONTH) % 11 <= currentMonth) {
				c.add(Calendar.DATE, 7);
				week++;
			}
		} else {
			while (c.get(Calendar.MONTH) <= currentMonth && c.get(Calendar.YEAR) == currentYear) {
				c.add(Calendar.DATE, 7);
				week++;
			}
		}
		c.add(Calendar.DATE, -week * 7);
		int size = week * 7;
		monthList = new ArrayList<DateBean>(size);
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
		return monthList;
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
				UItoolKit.showToastShort(getActivity(), "看清楚日期在点啊浑淡!");
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
			 setDetailView(attendMap.get(mBean.day - 1));
			//setDetailView(null);
			currentSelect = position;
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
			return mManager.queryAttendCursor(monthNum);
		}

		@Override
		protected Cursor loadFromNet() {
			String result = "";
			JSONObject param = new JSONObject();
			try {
				param.put("YHID", Preferences.getUserinfo(getContext(), "YHID"));
				param.put("QDRQ", Calendar.getInstance().get(Calendar.YEAR) + "-" + (monthNum + 1));
				result = NetOperating.getResultFromNet(getContext(), param,
						Urls.SAVEATTENDURL, "Operate=getAllKqxxByyhidAndqdrq");
				Utility.handleAttend(mManager, result);
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
					attendMap.put(pos, tempBean);
					setGridView(tempBean,firstDayPosition+pos);
					monthList.get(firstDayPosition+pos).attendState = tempBean.state;
				} while (attendCursor.moveToNext());
				isFinish = true;
				setDetailView(attendMap.get(currentSelect - firstDayPosition));
				OMUserDatabaseManager.getInstance(getActivity()).closeDb();
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

	void setGridView(AttendBean attendBean, int pos) {
		View v = monthGridView.getChildAt(pos);
		if(v == null){
			return;
		}
		TextView textView = (TextView) v.findViewById(R.id.tv_item_weekday);
		if(attendBean.state == 1){
			textView.setTextColor(getActivity().getResources().getColor(R.color.green));
		}else if(attendBean.state == 2){
			textView.setTextColor(getActivity().getResources().getColor(R.color.blue));
		}
	}

	protected void setDetailView(AttendBean mBean) {
		if (mBean == null) {
			attendState.setText("未签到");
			attendTime.setText(Utils.formatTime(selectDay.getTimeInMillis(), "yyyy年MM月dd日"));
			attendLocation.setText(null);
			return;
		}
		attendTime.setText(Utils.formatTime(mBean.time,"yyyy年MM月dd日"));
		attendLocation.setText(mBean.choseLocation);
		if(mBean.state == 1){
			attendState.setText("已签到");
		}else if(mBean.state == 2){
			attendState.setText("补签");
		}
	}

	private BroadcastReceiver mLocationReceiver = new LocationReceiver() {

		@Override
		protected void onProviderEnabledChanged(boolean enabled) {
		}

		@Override
		protected void onNetWorkLocationReceived(Context context,
				OMLocationBean loc) {
			if(!isVisible()){
				return;
			}
			AttendBean mBean = new AttendBean();
			mBean.SetValueBean(loc);
			if(!attendTime.getText().toString().equals(Utils.formatTime(loc.time, "yyyy年MM月dd日"))){
				mBean.state = 2;
			}else{
				mBean.state = 1;
			}
			mBean.time = selectDay.getTimeInMillis();
			mBean.choseLocation = attendLocation.getText().toString();
			mBean.month = selectDay.get(Calendar.MONTH);
			startTask(mBean);
		}

		@Override
		protected void onGPSLocationReceived(Context context, OMLocationBean loc) {

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
				int pos = Utils.getCalendarField(mBean.time, Calendar.DATE) - 1;
				monthList.get(firstDayPosition + pos).attendState = mBean.state;
				setGridView(mBean, firstDayPosition + pos);
				attendMap.put(pos, mBean);
			} else if (result.equals("REPEAT")){
				UItoolKit.showToastShort(getActivity(), "一天签十次也不会涨工资的");
			}else{
				UItoolKit.showToastShort(getActivity(), "保存至网络错误");
			}
			signButton.setEnabled(true);
			signButton.setText("签到");
			OMLocationManager.get(getActivity()).stopLocationUpdate();
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
}
