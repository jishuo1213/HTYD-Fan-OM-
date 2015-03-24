package com.htyd.fan.om.attendmanage.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
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
import com.htyd.fan.om.attendmanage.attendthread.LoadAttendFromLocal;
import com.htyd.fan.om.attendmanage.attendthread.LoadAttendFromNet;
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
import com.htyd.fan.om.util.fragment.SelectYearMonthDialog;
import com.htyd.fan.om.util.fragment.SelectYearMonthDialog.SelectAttendDateListener;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.https.Utility;
import com.htyd.fan.om.util.ui.TextViewWithBorder;
import com.htyd.fan.om.util.ui.UItoolKit;

public class AttendCalendarNewFragment extends Fragment implements
SelectLocationListener, ChooseAddressListener, InputDoneListener,SelectAttendDateListener {

	public static final int NETSUCCESS = 0x01;
	public static final int NETFAILED = 0x02;
	public static final int LOCALSUCCESS = 0x03;
	public static final int LOCALFAILED = 0x04;
	

	private GridView monthGridView;
	private TextView attendTime, attendLocation, attendState,month,attendRemark;
	private ImageButton refreshImgButton;
	private Button signButton;
	private ArrayList<DateBean> monthList;//日历显示
	protected SparseArray<AttendBean> currentMonthAttendMap;//签到的Map, 0到当前天       0 为1号
	protected SparseArray<AttendBean> otherMonthAttendMap;
	private Calendar selectDay;//当前选中的日期
	private CharSequence location;//定位的位置
	private AttendHandler handler;
	private boolean isLocation,isCurrentMonth;
	private PopupMenu popupMenu;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Calendar c = Calendar.getInstance();
		handler = new AttendHandler(this);
		selectDay = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
		inItMap(selectDay.get(Calendar.DATE) - 1);
		monthList = new ArrayList<DateBean>();
		getMonth(c);
		isCurrentMonth = true;
		queryAttendLocal();
		isLocation = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,  Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_main_page, container, false);
		intiView(v);
		return v;
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
	public void onPause() {
		getActivity().unregisterReceiver(mLocationReceiver);
		OMLocationManager.get(getActivity()).stopLocationUpdate();
		super.onPause();
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
		setMonthTitle();
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
					AddressListDialog dialogList = new AddressListDialog(AttendCalendarNewFragment.this);
					dialogList.show(getActivity().getFragmentManager(), null);
					return true;
				case R.id.menu_select_all:
					SelectLocationDialogFragment dialog = new SelectLocationDialogFragment();
					dialog.setListener(AttendCalendarNewFragment.this);
					dialog.show(getActivity().getFragmentManager(), null);
					return true;
				default:
					return true;
				}
			}
		});
	}

	private void setMonthTitle() {
		Log.i("fanjishuo____setMonthTitle", selectDay.get(Calendar.MONTH)+"");
		month.setText(selectDay.get(Calendar.YEAR) + "年"
				+ (selectDay.get(Calendar.MONTH) + 1) + "月");
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
				fragment.setListener(AttendCalendarNewFragment.this);
				fragment.show(getActivity().getFragmentManager(), null);
				break;
			case R.id.tv_month:
				SelectYearMonthDialog monthDialog = new SelectYearMonthDialog();
				monthDialog.setListener(AttendCalendarNewFragment.this);
				monthDialog.show(getActivity().getFragmentManager(), null);
				break;
			}
		}
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
			int month = c.get(Calendar.MONTH);
			try {
				if (month >= 9) {
					param.put("QDRQ", c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1));
				} else {
					param.put("QDRQ", c.get(Calendar.YEAR) + "-0" + (c.get(Calendar.MONTH) + 1));
				}
				param.put("YHID", Preferences.getUserinfo(getActivity(), "YHID"));
				
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
				currentMonthAttendMap.clear();
				if (isCurrentMonth) {
					resetAttendList();
				} else {
					Calendar c = Calendar.getInstance();
					selectDay.set(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE));
					getMonth(c);
				}
				queryAttendLocal();
				setMonthTitle();
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
	
	private static class AttendHandler extends Handler {
		
		 private WeakReference<AttendCalendarNewFragment> mFragment;
		 
		public AttendHandler(AttendCalendarNewFragment fragment) {
			mFragment = new WeakReference<AttendCalendarNewFragment>(fragment);
		}

		@Override
		public void dispatchMessage(final Message msg) {
			final AttendCalendarNewFragment parentFragment = mFragment.get();
			switch (msg.what) {
			case NETSUCCESS:
				final String result = (String) msg.obj;
				if(result.equals("{\"Rows\":[],\"Total\":0}")){
					UItoolKit.showToastShort(parentFragment.getActivity(), "没有考勤信息");
					break;
				}
				ThreadPool.runMethod(new Runnable() {
					@Override
					public void run() {
						Log.i("fanjishuo_____dispatchMessage", "-----"+result);
						parentFragment.netLoadSuccess(result);
					}
				});
				break;
			case NETFAILED:
				UItoolKit.showToastShort(parentFragment.getActivity(), "没有考勤信息");
				break;
			case LOCALSUCCESS:
				parentFragment.localLoadSuccess((AttendCursor) msg.obj);
				break;
			case LOCALFAILED:
				parentFragment.loadAttendFromNet();
				break;
			default:
				super.dispatchMessage(msg);
			}
		}
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
			mBean.attendRemark = attendRemark.getText().toString();
			mBean.year = selectDay.get(Calendar.YEAR);
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
	
	private class OnDayClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
/*			if (!isFinish) {
				UItoolKit.showToastShort(getActivity(), "数据还没加载完!");
				return;
			}*/
			DateBean mBean = (DateBean) parent.getAdapter().getItem(position);
			if (mBean.state != 1) {
				UItoolKit.showToastShort(getActivity(), "看清楚日期在点啊!");
				return;
			}
			if (mBean.day > Calendar.getInstance().get(Calendar.DATE) && isCurrentMonth) {
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
			if (isCurrentMonth) {
				setDetailView(currentMonthAttendMap.get(mBean.day - 1));
			} else {
				setDetailView(otherMonthAttendMap.get(mBean.day - 1));
			}
			((AttendCalendarGridAdapter) parent.getAdapter()).setSelectPos(position);
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
		
	private void getMonth(Calendar c) {
		monthList.clear();
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
	}
	
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
			if (!isCurrentMonth) {
				if (result.equals("TRUE")) {
					if (mBean.state == 1) {
						UItoolKit.showToastShort(getActivity(), "签到成功");
					} else {
						UItoolKit.showToastShort(getActivity(), "补签成功");
					}
					attendState.setText("已签到");
					int pos = Utils.getCalendarField(mBean.time, Calendar.DATE) - 1;
					monthList.get(pos + getFirstDayPos()).attendState = mBean.state;
					refreshGridView();
					otherMonthAttendMap.put(pos, mBean);
				} else if (result.equals("REPEAT")) {
					UItoolKit.showToastShort(getActivity(), "重复签到");
				} else {
					UItoolKit.showToastShort(getActivity(), "保存至网络错误");
				}
				signButton.setEnabled(true);
				signButton.setText("签到");
				stopTask(this);
				return;
			}
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
				monthList.get(pos + getFirstDayPos()).attendState = mBean.state;
				refreshGridView();
//				setGridView(mBean,pos);
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
	protected void refreshGridView() {
		AttendCalendarGridAdapter adapter = (AttendCalendarGridAdapter)monthGridView.getAdapter();
		adapter.setSelectPos(getFirstDayPos() + selectDay.get(Calendar.DATE) - 1);
		adapter.notifyDataSetChanged();
		setDetailView(currentMonthAttendMap.get(selectDay.get(Calendar.DATE) - 1));
	}

	protected void startTask(AttendBean mBean) {
		new SaveAttendTask().execute(mBean);
	}

	protected void stopTask(SaveAttendTask task) {
		task.cancel(false);
	}
	
	
	@Override
	public void onInputDone(String text) {
		attendRemark.setText(text);
	}

	@Override
	public void onAddressChoose(String address) {
		attendLocation.setText(address);
	}

	@Override
	public void OnSelectLocation(String location) {
		attendLocation.setText(location);
	}
	
	public void queryAttendLocal() {
		int year = selectDay.get(Calendar.YEAR);
		int month = selectDay.get(Calendar.MONTH);
		Log.i("fanjishuo_____queryAttendLocal", "year"+year + "month"+month);
		ThreadPool.runMethod(new LoadAttendFromLocal(handler, year, month, getActivity()));
	}
	
	private void loadAttendFromNet() {
		if (!Utils.isNetWorkEnable()) {
			UItoolKit.showToastShort(getActivity(), "网络连接不可用,无法从网络加载考勤信息");
			return;
		}
		int year = selectDay.get(Calendar.YEAR);
		int month = selectDay.get(Calendar.MONTH) + 1;
		ThreadPool.runMethod(new LoadAttendFromNet(getActivity(), year, month, handler));
	}

	private void netLoadSuccess(String result) {
		if(isCurrentMonth){
			try {
				Utility.handleAttend(OMUserDatabaseManager.getInstance(getActivity()), result);
				queryAttendLocal();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		Calendar c = Calendar.getInstance();
		c.set(selectDay.get(Calendar.YEAR),selectDay.get(Calendar.MONTH),selectDay.get(Calendar.DATE));
		int firstDayPos = getFirstDayPos();
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
				pos = Utils.getCalendarField(mBean.time,Calendar.DATE) - 1;
				otherMonthAttendMap.put(pos, mBean);
				monthList.get(pos + firstDayPos).attendState = mBean.state;
			}
			handler.post(new Runnable() {
				@Override
				public void run() {
					refreshGridView();
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void localLoadSuccess(AttendCursor cursor){
		int firstDayPos = getFirstDayPos();
		Log.i("fanjishuo_____localLoadSuccess", cursor.getCount()+"");
		int pos;
		if (cursor.moveToFirst()) {
			do {
				AttendBean mBean = cursor.getAttend();
				pos = Utils.getCalendarField(mBean.time,Calendar.DATE) - 1;
				currentMonthAttendMap.put(pos, mBean);
				monthList.get(pos + firstDayPos).attendState = mBean.state;
			} while (cursor.moveToNext());
			refreshGridView();
		}
		cursor.close();
	}
	
	private int getFirstDayPos() {
		Calendar c = new GregorianCalendar(selectDay.get(Calendar.YEAR),selectDay.get(Calendar.MONTH),selectDay.get(Calendar.DATE));
		c.set(Calendar.DATE, 1);
		return c.get(Calendar.DAY_OF_WEEK) - 1;
	}

	@Override
	public void onSelectDate(Calendar c) {
		selectDay.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1);
		if (isCurrentMonth(selectDay)) {
			isCurrentMonth = true;
			c.set(Calendar.DATE, 1);
			getMonth(c);
			int firstDayPos = getFirstDayPos();
			for (int i = 0; i < currentMonthAttendMap.size(); i++){
				AttendBean mBean = currentMonthAttendMap.get(i);
				if (mBean != null) {
					monthList.get(Utils.getCalendarField(mBean.time,
							Calendar.DATE) - 1 + firstDayPos).attendState = currentMonthAttendMap
							.get(i).state;
				}
			}
			setMonthTitle();
			refreshGridView();
			return;
		} else {
			isCurrentMonth = false;
		}
		setMonthTitle();
		loadAttendFromNet();
	}

	public boolean isCurrentMonth(Calendar c) {
		Calendar current = Calendar.getInstance();
		return c.get(Calendar.YEAR) == current.get(Calendar.YEAR)
				&& c.get(Calendar.MONTH) == current.get(Calendar.MONTH);
	}
	
	
	private void inItMap(int count) {
		currentMonthAttendMap = new SparseArray<AttendBean>();
		for(int i = 0 ; i < count;i++){
			currentMonthAttendMap.put(i, null);
		}
	}
}
