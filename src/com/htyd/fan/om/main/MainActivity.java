package com.htyd.fan.om.main;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.attendmanage.fragment.AttendCalendarNewFragment;
import com.htyd.fan.om.main.fragment.SettingFragment;
import com.htyd.fan.om.taskmanage.fragment.TaskListFragment;
import com.htyd.fan.om.util.base.Utils;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.ui.UItoolKit;

public class MainActivity extends FragmentActivity {

	private ViewPager mainViewPager;
	private List<Fragment> fragmentList = new ArrayList<Fragment>();
	private Drawable[] tabDrawable;
	private FragmentPagerAdapter pageAdapter;
	private TabPanel tabPanel;
	public static  int currentPos;
	private  Menu menu;
	//private ActionProvider firstProvider,thirdProvider;
	private long firstBackKeyDown;
	private OnItemChooserListener listener;

	public interface OnItemChooserListener {
		public void onItemChooser(int position);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		forceShowOverflowMenu();
		setContentView(R.layout.activity_main);
		if(savedInstanceState != null){
			currentPos = savedInstanceState.getInt("pagepos");
		}
		/*firstProvider = new AttendOverflowMenu(this);
		thirdProvider = new TaskOvewflowMenu(this);*/
		currentPos = 0;
		loadData();
		init();
		initActionBar();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		fragmentList.clear();
		tabDrawable = null;
		Log.i("fanjishuo____mainActivity", "onDestory");
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("pagepos", currentPos);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.i("fanjishuo___mainActivity", "onConfigurationChanged");
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	@SuppressLint("AlwaysShowAction")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		getMenuInflater().inflate(R.menu.om_over_flow_menu, menu);
		setIconEnable(menu,true);
		if (currentPos == 0) {
			menu.findItem(R.id.menu_inprocess_task).setVisible(false);
			menu.findItem(R.id.menu_create_task).setVisible(false);
			menu.findItem(R.id.menu_finished_task).setVisible(false);
			menu.findItem(R.id.menu_query_task).setVisible(false);
		} else if (currentPos == 1) {
			menu.findItem(R.id.menu_attend_one).setVisible(false);
			menu.findItem(R.id.menu_attend_two).setVisible(false);
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()){
		case R.id.menu_inprocess_task:
			listener.onItemChooser(0);
			return true;
		case R.id.menu_finished_task:
			listener.onItemChooser(1);
			return true;
		case R.id.menu_create_task:
			listener.onItemChooser(2);
			return true;
		case R.id.menu_query_task:
			listener.onItemChooser(3);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}

	private void initActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.bg_top_navigation_bar));
		actionBar.setTitle("终端运维系统");
		actionBar.setDisplayShowHomeEnabled(false);
	}

	private void loadData() {
		Resources r = getResources();
		tabDrawable = new Drawable[8];
		TypedArray imgs = r.obtainTypedArray(R.array.tab_drawable_id);
		AttendCalendarNewFragment tab1 = new AttendCalendarNewFragment();
		TaskListFragment tab3 = new TaskListFragment();
		SettingFragment tab4 = new SettingFragment();
		listener = tab3;
		fragmentList.add(tab1);
		fragmentList.add(tab3);
		fragmentList.add(tab4);
		for (int i = 0; i < 8; i++) {
			tabDrawable[i] = r.getDrawable(imgs.getResourceId(i, -1));
		}
		imgs.recycle();
	}

	private void init() {
		tabPanel = new TabPanel();
		tabPanel.setTabTag();
		mainViewPager = (ViewPager) findViewById(R.id.vp_main_activity);
		FragmentManager fm = getSupportFragmentManager();

		pageAdapter = new FragmentPagerAdapter(fm) {

			@Override
			public int getCount() {
				return fragmentList.size();
			}

			@Override
			public Fragment getItem(int position) {
				return fragmentList.get(position);
			}
		};
		mainViewPager.setAdapter(pageAdapter);
		mainViewPager.setOnPageChangeListener(new PagerChangeListener());
		tabPanel.setImg(currentPos, true);
		Urls.ACCESSORYFILEPATH = Utils.getAccessoryPath();
	}

	private class PagerChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			tabPanel.setImg(currentPos, false);
			tabPanel.setImg(position, true);
			setOvewflowMenu(position);
			currentPos = position;
		}
	}

	private class TabPanel {
		private TextView tabOneTextView, tabThreeTextView,
				tabFourTextView;

		public TabPanel() {
			tabOneTextView = (TextView) findViewById(R.id.tv_tab_one);
			tabThreeTextView = (TextView) findViewById(R.id.tv_tab_three);
			tabFourTextView = (TextView) findViewById(R.id.tv_tab_four);
		}

		public void setTabTag() {
			TabClickListener mClickListener = new TabClickListener();
			tabOneTextView.setTag(0);
			tabThreeTextView.setTag(1);
			tabFourTextView.setTag(2);
			tabOneTextView.setOnClickListener(mClickListener);
			tabThreeTextView.setOnClickListener(mClickListener);
			tabFourTextView.setOnClickListener(mClickListener);
		}

		public void setImg(int pos, boolean ispressed) {
			switch (pos) {
			case 0:
				if (ispressed) {
					setTabImg(tabOneTextView, tabDrawable[4]);
				} else {
					setTabImg(tabOneTextView, tabDrawable[0]);
				}
				break;
			/*case 1:
				if (ispressed) {
					setTabImg(tabTwoTextView, tabDrawable[5]);
				} else {
					setTabImg(tabTwoTextView, tabDrawable[1]);
				}
				break;*/
			case 1:
				if (ispressed) {
					setTabImg(tabThreeTextView, tabDrawable[6]);
				} else {
					setTabImg(tabThreeTextView, tabDrawable[2]);
				}
				break;
			case 2:
				if (ispressed) {
					setTabImg(tabFourTextView, tabDrawable[7]);
				} else {
					setTabImg(tabFourTextView, tabDrawable[3]);
				}
				break;
			}
		}
	}

	private void setTabImg(TextView v, Drawable drawable) {
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		v.setCompoundDrawables(null, null, null, drawable);
	}

	private class TabClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			int pos = (Integer) v.getTag();
			mainViewPager.setCurrentItem(pos, true);
			setOvewflowMenu(pos);
		}
	}

	protected void setOvewflowMenu(int pos) {
		if (menu == null) {
			return;
		}
		switch (pos) {
		case 0:
			setAttendMenu();
			break;
		case 1:
			setTaskMenu();
			break;
		case 2:
			setAllMenu();
			break;
		case 3:
			setAllMenu();
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			long time = System.currentTimeMillis() - firstBackKeyDown;
				if (time <= 1500) {
					currentPos = 0;
					new  LogoutTask().execute();
					this.finish();
					return true;
				} else {
					firstBackKeyDown = System.currentTimeMillis();
					UItoolKit.showToastShort(getBaseContext(), "再按一次退出");
					return true;
				}
		default:
			return super.onKeyDown(keyCode, event);
		}
	}
	
	private class LogoutTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			try {
				NetOperating.getResultFromNet(getBaseContext(), null, Urls.LOGINURL, "Operate=loginOut");
			} catch (Exception e) {
				e.printStackTrace();
			}
			cancel(false);
			return null;
		}
	}
	
	private void forceShowOverflowMenu() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setIconEnable(Menu menu, boolean enable) {
		try {
			Class<?> clazz = Class
					.forName("com.android.internal.view.menu.MenuBuilder");
			Method m = clazz.getDeclaredMethod("setOptionalIconsVisible",
					boolean.class);
			m.setAccessible(true);

			// MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)
			m.invoke(menu, enable);

		} catch (Exception e) {
		}
	}

	private void setAttendMenu() {
		menu.findItem(R.id.menu_inprocess_task).setVisible(false);
		menu.findItem(R.id.menu_create_task).setVisible(false);
		menu.findItem(R.id.menu_finished_task).setVisible(false);
		menu.findItem(R.id.menu_query_task).setVisible(false);
		menu.findItem(R.id.menu_attend_one).setVisible(true);
		menu.findItem(R.id.menu_attend_two).setVisible(true);
	}

	private void setTaskMenu() {
		menu.findItem(R.id.menu_inprocess_task).setVisible(true);
		menu.findItem(R.id.menu_create_task).setVisible(true);
		menu.findItem(R.id.menu_finished_task).setVisible(true);
		menu.findItem(R.id.menu_query_task).setVisible(true);
		menu.findItem(R.id.menu_attend_one).setVisible(false);
		menu.findItem(R.id.menu_attend_two).setVisible(false);
	}
	
	private void setAllMenu(){
		menu.findItem(R.id.menu_inprocess_task).setVisible(false);
		menu.findItem(R.id.menu_create_task).setVisible(false);
		menu.findItem(R.id.menu_finished_task).setVisible(false);
		menu.findItem(R.id.menu_query_task).setVisible(false);
		menu.findItem(R.id.menu_attend_one).setVisible(false);
		menu.findItem(R.id.menu_attend_two).setVisible(false);
	}

}
