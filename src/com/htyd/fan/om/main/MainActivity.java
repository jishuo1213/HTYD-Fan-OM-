package com.htyd.fan.om.main;

import java.util.ArrayList;
import java.util.List;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.main.fragment.TabFourFragment;
import com.htyd.fan.om.main.fragment.TabOneFragment;
import com.htyd.fan.om.main.fragment.TabThreeFragment;
import com.htyd.fan.om.main.fragment.TabTwoFragment;

public class MainActivity extends FragmentActivity {

	private ViewPager mainViewPager;
	private List<Fragment> fragmentList = new ArrayList<Fragment>();
	private Drawable[] tabDrawable;
	private FragmentPagerAdapter pageAdapter;
	private TabPanel tabPanel;
	private int currentPos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		loadData();
		init();
	}

	@Override
	protected void onDestroy() {
		fragmentList.clear();
		tabDrawable = null;
		super.onDestroy();
	}

	private void loadData() {
		Resources r = getResources();
		tabDrawable = new Drawable[8];
		TypedArray imgs = r.obtainTypedArray(R.array.tab_drawable_id);
		TabOneFragment tab1 = new TabOneFragment();
		TabTwoFragment tab2 = new TabTwoFragment();
		TabThreeFragment tab3 = new TabThreeFragment();
		TabFourFragment tab4 = new TabFourFragment();
		fragmentList.add(tab1);
		fragmentList.add(tab2);
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
			currentPos = position;
		}
	}

	private class TabPanel {
		private TextView tabOneTextView, tabTwoTextView, tabThreeTextView,
				tabFourTextView;

		public TabPanel() {
			tabOneTextView = (TextView) findViewById(R.id.tv_tab_one);
			tabTwoTextView = (TextView) findViewById(R.id.tv_tab_two);
			tabThreeTextView = (TextView) findViewById(R.id.tv_tab_three);
			tabFourTextView = (TextView) findViewById(R.id.tv_tab_four);
		}

		public void setTabTag() {
			TabClickListener mClickListener = new TabClickListener();
			tabOneTextView.setTag(0);
			tabTwoTextView.setTag(1);
			tabThreeTextView.setTag(2);
			tabFourTextView.setTag(3);
			tabOneTextView.setOnClickListener(mClickListener);
			tabTwoTextView.setOnClickListener(mClickListener);
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
			case 1:
				if (ispressed) {
					setTabImg(tabTwoTextView, tabDrawable[5]);
				} else {
					setTabImg(tabTwoTextView, tabDrawable[1]);
				}
				break;
			case 2:
				if (ispressed) {
					setTabImg(tabThreeTextView, tabDrawable[6]);
				} else {
					setTabImg(tabThreeTextView, tabDrawable[2]);
				}
				break;
			case 3:
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
			int pos =  (Integer) v.getTag();
			mainViewPager.setCurrentItem(pos, true);
		}
	}
}
