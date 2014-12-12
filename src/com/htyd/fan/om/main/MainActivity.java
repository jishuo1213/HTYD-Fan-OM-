package com.htyd.fan.om.main;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.ActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.attendmanage.fragment.AttendCalendarFragment;
import com.htyd.fan.om.main.fragment.SettingFragment;
import com.htyd.fan.om.taskmanage.fragment.TaskListFragment;
import com.htyd.fan.om.util.ui.AttendOverflowMenu;
import com.htyd.fan.om.util.ui.TaskOvewflowMenu;

public class MainActivity extends FragmentActivity {

	private ViewPager mainViewPager;
	private List<Fragment> fragmentList = new ArrayList<Fragment>();
	private Drawable[] tabDrawable;
	private FragmentPagerAdapter pageAdapter;
	private TabPanel tabPanel;
	private int currentPos;
	private static Menu menu;
	private ActionProvider firstProvider,thirdProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		firstProvider = new AttendOverflowMenu(this);
		thirdProvider = new TaskOvewflowMenu(this);
		loadData();
		init();
		initActionBar();
		Log.i("fanjishuo_____onCreate", "mainOnCreate");
	}

	@Override
	protected void onDestroy() {
		fragmentList.clear();
		tabDrawable = null;
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MainActivity.menu = menu;
		getMenuInflater().inflate(R.menu.menu_popup, menu);
		MenuItem menuItem = menu.findItem(R.id.more_menu);
		menuItem.setActionProvider(firstProvider);
		menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
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
		AttendCalendarFragment tab1 = new AttendCalendarFragment();
//		TodoReminde tab2 = new TodoReminde();
		TaskListFragment tab3 = new TaskListFragment();
		SettingFragment tab4 = new SettingFragment();
		TaskOvewflowMenu temp = (TaskOvewflowMenu)thirdProvider;
		temp.setListener(tab3);
		fragmentList.add(tab1);
//		fragmentList.add(tab2);
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
		tabPanel.setImg(0, true);
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
	//		tabTwoTextView = (TextView) findViewById(R.id.tv_tab_two);
			tabThreeTextView = (TextView) findViewById(R.id.tv_tab_three);
			tabFourTextView = (TextView) findViewById(R.id.tv_tab_four);
		}

		public void setTabTag() {
			TabClickListener mClickListener = new TabClickListener();
			tabOneTextView.setTag(0);
	//		tabTwoTextView.setTag(1);
			tabThreeTextView.setTag(1);
			tabFourTextView.setTag(2);
			tabOneTextView.setOnClickListener(mClickListener);
	//		tabTwoTextView.setOnClickListener(mClickListener);
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
		if(menu == null){
			return;
		}
		MenuItem menuItem = menu.findItem(R.id.more_menu);
		switch(pos){
		case 0:
			menuItem.setVisible(true);
			menuItem.setActionProvider(firstProvider);
			menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			break;
		case 1:
			menuItem.setVisible(true);
			menuItem.setActionProvider(thirdProvider);
			menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			break;
		case 2:
			menuItem.setVisible(false);
			break;
		case 3:
			menuItem.setVisible(false);
			break;
		}
	}
}
