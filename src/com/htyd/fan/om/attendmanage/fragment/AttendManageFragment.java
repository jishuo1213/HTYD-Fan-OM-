package com.htyd.fan.om.attendmanage.fragment;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.AttendBean;
import com.htyd.fan.om.model.OMLocationBean;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.ui.UItoolKit;

public class AttendManageFragment extends Fragment {

	private ViewPanel mPanel;
	private AttendBean mBean;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBean = new AttendBean();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.attend_manage_fragment, container,
				false);
		intiView(v);
		return v;
	}

	private void intiView(View v) {
		mPanel = new ViewPanel(v);
	}

	@SuppressLint("SimpleDateFormat")
	public void updateUI(OMLocationBean loc) {
		mBean.time = loc.time;
		mBean.latitude = loc.latitude;
		mBean.longitude = loc.longitude;
		mBean.SetValueBean(loc);
		mPanel.setLocation(mBean.getAddress());
		mPanel.setTime(new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")
				.format(mBean.time));
		mPanel.setSignButtonEnable(true);
	}

	private class ViewPanel {
		private TextView locTextView, timeTextView;
		private Button mButton;

		public ViewPanel(View v) {
			locTextView = (TextView) v.findViewById(R.id.tv_loction);
			timeTextView = (TextView) v.findViewById(R.id.tv_time);
			mButton = (Button) v.findViewById(R.id.btn_sign_in);
			mButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startTask(mBean);
				}
			});
			mButton.setEnabled(false);
		}

		public void setLocation(String loc) {
			locTextView.setText(loc);
		}

		public void setTime(String time) {
			timeTextView.setText(time);
		}

		public void setSignButtonEnable(boolean enable) {
			mButton.setEnabled(enable);
		}
	}

	private class SaveAttendTask extends AsyncTask<AttendBean, Void, Boolean> {

		@Override
		protected Boolean doInBackground(AttendBean... params) {
			AttendBean mBean = params[0];
			boolean result;
			try {
				result = NetOperating.saveAttendToSever(getActivity(),
						mBean);
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			} catch (ExecutionException e) {
				e.printStackTrace();
				return false;
			}
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				/*
				 * OMUserDatabaseManager.getInstance(getActivity())
				 * .insertAttendBean(mBean);
				 */
			} else {
				UItoolKit.showToastShort(getActivity(), "保存至网络不成功，检查网络设置");
			}
			stopTask(this);
		}

	}

	private void startTask(AttendBean mBean) {
		new SaveAttendTask().execute(mBean);
	}

	private void stopTask(SaveAttendTask task) {
		task.cancel(false);
	}
}
