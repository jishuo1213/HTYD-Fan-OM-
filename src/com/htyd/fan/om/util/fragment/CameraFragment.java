package com.htyd.fan.om.util.fragment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.htyd.fan.om.R;

public class CameraFragment extends Fragment implements OnTouchListener {
	

	public static final String EXTRA_PHOTO_FILENAME = "CrimeCameraFragment.filename";

	private Camera mCamera;
	private SurfaceView mSurfaceView;
	View mProgressContainer;
	private ImageView autoFocusImage;

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_personcard_camera, parent,
				false);
		v.setOnTouchListener(this);
		mProgressContainer = v
				.findViewById(R.id.process_bar);
		autoFocusImage = (ImageView) v.findViewById(R.id.focus_status);
		mProgressContainer.setVisibility(View.INVISIBLE);
		ImageView takePictureButton = (ImageView) v
				.findViewById(R.id.personcard_camera_takePictureButton);
		takePictureButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (mCamera != null) {
					try {
						mCamera.takePicture(mShutterCallback, null, mJpegCallBack);
					} catch (Exception e) {
						e.printStackTrace();
						mCamera.release();
						mCamera = null;
						getActivity().finish();
					}
				}
			}
		});

		mSurfaceView = (SurfaceView) v
				.findViewById(R.id.personcard_camera_surfaceView);
		SurfaceHolder holder = mSurfaceView.getHolder();
		// deprecated, but required for pre-3.0 devices
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(mHolderCallback);
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			mCamera = Camera.open(0);

		} else {
			mCamera = Camera.open();
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}

	/**
	 * a simple algorithm to get the largest size available. For a more robust
	 * version, see CameraPreview.java in the ApiDemos sample app from Android.
	 */
	private Size getBestSupportedSize(List<Size> sizes, int width, int height) {
		Size bestSize = sizes.get(0);
		int largestArea = bestSize.width * bestSize.height;
		for (Size s : sizes) {
			int area = s.width * s.height;
			if (area > largestArea) {
				bestSize = s;
				largestArea = area;
			}
		}
		return bestSize;
	}

	private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
		public void onShutter() {
			// display the progress indicator
			mProgressContainer.setVisibility(View.VISIBLE);
		}
	};
	private Camera.PictureCallback mJpegCallBack = new Camera.PictureCallback() {
		public void onPictureTaken(final byte[] data, Camera camera) {
			File sdCardDir = Environment.getExternalStorageDirectory();
			String filename = UUID.randomUUID().toString() + ".jpg";
			boolean success = true;
			BufferedOutputStream bos = null;
			if (sdCardDir.exists()) {
				File mPictureDir = new File(sdCardDir + "/" + "OmAccessory");
				if (!mPictureDir.exists()) {
					mPictureDir.mkdir();
				}
				File pic = new File(mPictureDir + "/" + filename);
				if (!pic.exists()) {
					try {
						pic.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				try {
					bos = new BufferedOutputStream(new FileOutputStream(pic));
					bos.write(data);
				} catch (FileNotFoundException e) {
					Log.i("fanjishuo____", "FileNotFoundException");
					success = false;
					e.printStackTrace();
				} catch (IOException e) {
					success = false;
					e.printStackTrace();
				} finally {
					if (bos != null)
						try {
							bos.close();
						} catch (IOException e) {
							success = false;
							e.printStackTrace();
						}
				}
				Log.i("fanjishuo____takepicture", "save at " + filename
						+ success);
				if (success) {
					Intent i = new Intent();
					i.putExtra(EXTRA_PHOTO_FILENAME, pic.getAbsolutePath());
					getActivity().setResult(Activity.RESULT_OK, i);
					Log.i("fanjishuo____takepicture", "save at " + filename);
				} else {
					getActivity().setResult(Activity.RESULT_CANCELED);
				}
				getActivity().finish();
			} else {
				Toast.makeText(getActivity(),
						"No SdCard Found,picture will save in phone.",
						Toast.LENGTH_SHORT).show();
				try {
					bos = new BufferedOutputStream(getActivity()
							.openFileOutput(filename, Context.MODE_PRIVATE));
					bos.write(data);
				} catch (FileNotFoundException e) {
					success = false;
					e.printStackTrace();
				} catch (IOException e) {
					success = false;
					e.printStackTrace();
				} finally {
					if (bos != null)
						try {
							bos.close();
						} catch (IOException e) {
							success = false;
							e.printStackTrace();
						}
				}
				if (success) {
					Intent i = new Intent();
					i.putExtra(EXTRA_PHOTO_FILENAME, filename);
					getActivity().setResult(Activity.RESULT_OK, i);
					Log.i("fanjishuo____takepicture", "save at " + filename);
				} else {
					getActivity().setResult(Activity.RESULT_CANCELED);
				}
				getActivity().finish();
			}
		}
	};
	private SurfaceHolder.Callback mHolderCallback = new SurfaceHolder.Callback() {

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
			if (mCamera == null)
				return;
			setCameraParameters(w, h);
			// the surface has changed size; update the camera preview size
			try {
				// mCamera.autoFocus(mAutoFocusCallback);
				mCamera.startPreview();
			} catch (Exception e) {
				mCamera.release();
				mCamera = null;
			}
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// tell the camera to use this surface as its preview area
			try {
				if (mCamera != null) {

					mCamera.setPreviewDisplay(holder);
				}
			} catch (IOException exception) {
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (mCamera != null) {
				mCamera.stopPreview();
			}
		}

	};

	Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if (success) {
				autoFocusImage.setBackgroundResource(R.drawable.camera_red);
			} else {
				autoFocusImage.setBackgroundResource(R.drawable.camera_red);
			}
			hd.sendEmptyMessageDelayed(1213, 1000);
		}
	};

	@SuppressLint("InlinedApi")
	private void setCameraParameters(int w, int h) {
		Camera.Parameters parameters = mCamera.getParameters();
		Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), w,
				h);
		parameters.setPreviewSize(s.width, s.height);
		s = getBestSupportedSize(parameters.getSupportedPictureSizes(), w, h);
		parameters.setPictureSize(s.width, s.height);
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
		mCamera.setParameters(parameters);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			autoFocusImage.setVisibility(View.VISIBLE);
			autoFocusImage.setBackgroundResource(R.drawable.camera_big_white);
			hd.sendEmptyMessage(1212);
			break;
		}
		return true;
	}

	@SuppressLint("HandlerLeak")
	Handler hd = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1212:
				mCamera.autoFocus(mAutoFocusCallback);
				break;
			case 1213:
				autoFocusImage.setVisibility(View.GONE);
				break;
			}
		}
	};
}
