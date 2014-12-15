package com.htyd.fan.om.util.fragment;

import java.io.File;

import android.app.DialogFragment;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.htyd.fan.om.util.base.PictureUtils;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.ui.UItoolKit;

public class ImageFragment extends DialogFragment {
	public static final String EXTRA_IMAGE_PATH = "com.bignerdranch.android.criminalintent.control.camera.iamge_paths";
	
	private ImageView mImageView;
	
	public static  ImageFragment newInstance (String imagePath){
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_IMAGE_PATH, imagePath);
		
		ImageFragment fragment  = new ImageFragment();
		fragment.setArguments(args);
		fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mImageView = new ImageView(getActivity());
		String path = (String) getArguments().getSerializable(EXTRA_IMAGE_PATH);
		File file  = new File(path);
		if (!file.exists()) {
			OMUserDatabaseManager.getInstance(getActivity()).detelteTaskAccessory(path);
			mImageView.setImageDrawable(null);
			UItoolKit.showToastShort(getActivity(), "文件不存在，是不是被你删除了");
		} else {
			BitmapDrawable image = PictureUtils.getScaledDrawable(getActivity(), path);
			mImageView.setImageDrawable(image);
		}
		return mImageView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		PictureUtils.cleanImageView(mImageView);
	}
	
	
}
