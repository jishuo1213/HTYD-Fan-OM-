package com.htyd.fan.om.util.fragment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.util.ui.UItoolKit;

public class RecodingDialogFragment extends DialogFragment {

	public static final String FILEPATHARRAY = "filepath";

	AudioRecord audioRecord;
	boolean isSaveFile;
	int buffersize;
	boolean isRecording;
	String filePath;
	List<String> saveFileList;
	boolean isPlaying;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isSaveFile = false;
		isRecording = false;
		isPlaying = false;
		filePath = "";
		saveFileList = new ArrayList<String>();
		initAudioRecord();
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View v = getActivity().getLayoutInflater().inflate(
				R.layout.accessory_dialog_layout, null);
		initView(v);
		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(v);
		builder.setTitle("录音");
		builder.setPositiveButton("完成录音",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						sendResult();
					}
				});
		return builder.create();
	}

	@Override
	public void onDestroy() {
		if (!isSaveFile && filePath.length() > 0) {
			File file = new File(filePath);
			file.delete();
			UItoolKit.showToastShort(getActivity(), "最后的录音没保存就删了呦");
		}
		audioRecord.release();
		audioRecord = null;
		super.onDestroy();
	}

	private void initView(View v) {
		TextView startRecoding, endRecoding, playRecoding, saveFile;
		startRecoding = (TextView) v.findViewById(R.id.tv_start_recoding);
		endRecoding = (TextView) v.findViewById(R.id.tv_end_recording);
		playRecoding = (TextView) v.findViewById(R.id.tv_play);
		saveFile = (TextView) v.findViewById(R.id.tv_save_file);
		startRecoding.setOnClickListener(AccessoryListener);
		endRecoding.setOnClickListener(AccessoryListener);
		playRecoding.setOnClickListener(AccessoryListener);
		saveFile.setOnClickListener(AccessoryListener);
	}

	private void initAudioRecord() {
		int frequency = 11025;
		int chanelConfiguration = AudioFormat.CHANNEL_IN_MONO;
		int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
		buffersize = AudioRecord.getMinBufferSize(frequency,
				chanelConfiguration, audioEncoding);
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
				chanelConfiguration, audioEncoding, buffersize);
	}

	void sendResult() {
		if (getTargetFragment() == null) {
			return;
		}
		Intent intent = new Intent();
		int size = saveFileList.size();
		String[] temp = new String[size];
		for (int i = 0; i < size; i++) {
			temp[i] = saveFileList.get(i);
		}
		intent.putExtra(FILEPATHARRAY, temp);
		getTargetFragment().onActivityResult(getTargetRequestCode(),
				Activity.RESULT_OK, intent);
	}

	private OnClickListener AccessoryListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_start_recoding:
				if (isRecording) {
					UItoolKit.showToastShort(getActivity(), "已经在录音了啊混蛋");
					return;
				}
				if (!isSaveFile && filePath.length() > 0) {
					File file = new File(filePath);
					file.delete();
					filePath = "";
				}
				isSaveFile = false;
				isRecording = true;
				String fileName = UUID.randomUUID().toString() + ".pcm";
				startTask(fileName, "record");
				break;
			case R.id.tv_end_recording:
				if (!isRecording) {
					UItoolKit.showToastShort(getActivity(), "未曾开始，安能结束？");
					return;
				}
				isRecording = false;
				break;
			case R.id.tv_play:
				if (isRecording) {
					UItoolKit.showToastShort(getActivity(), "正在录音，放不了");
					return;
				}
				if (filePath.length() == 0) {
					UItoolKit.showToastShort(getActivity(), "没有录音，想听的话你自己唱吧");
					return;
				}
				if (isPlaying) {
					UItoolKit.showToastShort(getActivity(), "正在播放");
					return;
				}
				isPlaying = true;
				startTask(filePath, "play");
				break;
			case R.id.tv_save_file:
				if (isRecording) {
					UItoolKit.showToastShort(getActivity(), "正在录音，保存个蛋啊");
					return;
				}
				if (!(filePath.length() > 0)) {
					UItoolKit.showToastShort(getActivity(), "没录音保存个蛋啊");
					return;
				}
				if (saveFileList.contains(filePath)) {
					UItoolKit.showToastShort(getActivity(), "保存完了还点个蛋啊");
					return;
				}
				isSaveFile = true;
				saveFileList.add(filePath);
				UItoolKit.showToastShort(getActivity(), "保存成功");
				break;
			}
		}
	};

	private class RecordAndPlayTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPostExecute(Boolean result) {
			if (!result || result == null) {
				UItoolKit.showToastShort(getActivity(), "出现错误");
			} else {
				isPlaying = false;
			}
		}

		@Override
		protected Boolean doInBackground(String... params) {
			if (params[1].equals("record")) {
				return record(params[0]);
			} else if (params[1].equals("play")) {
				return playback(params[0]);
			} else {
				return null;
			}
		}

	}

	void startTask(String fileName, String mode) {
		new RecordAndPlayTask().execute(new String[] { fileName, mode });
	}

	void stopTask(RecordAndPlayTask task) {
		task.cancel(false);
	}

	boolean record(String fileName) {
		File sdCardDir = Environment.getExternalStorageDirectory();
		DataOutputStream dos = null;
		boolean success = true;
		if (sdCardDir.exists()) {
			File mAccessoryDir = new File(sdCardDir + "/" + "OmAccessory");
			File rec = null;
			if (!mAccessoryDir.exists()) {
				mAccessoryDir.mkdir();
			}
			rec = new File(mAccessoryDir + "/" + fileName);
			if (!rec.exists()) {
				try {
					rec.createNewFile();
				} catch (IOException e) {
					success = false;
					e.printStackTrace();
					return success;
				}
			}
			try {
				dos = new DataOutputStream(new BufferedOutputStream(
						new FileOutputStream(rec)));
				short[] buffer = new short[buffersize];
				audioRecord.startRecording();
				while (isRecording) {
					int bufferReadResult = audioRecord.read(buffer, 0,
							buffersize);
					for (int i = 0; i < bufferReadResult; i++) {
						dos.writeShort(buffer[i]);
					}
				}
				audioRecord.stop();
			} catch (FileNotFoundException e) {
				success = false;
				e.printStackTrace();
				return success;
			} catch (IOException e) {
				success = false;
				e.printStackTrace();
				return success;
			} finally {
				try {
					dos.close();
				} catch (IOException e) {
					success = false;
					e.printStackTrace();
					return success;
				}
			}
			if (success && rec != null) {
//				copyWaveFile(rec.getAbsolutePath(),sdCardDir + "/" + "OmAccessory" +"/" +fileName);
				filePath = rec.getAbsolutePath();
			}
			return success;
		} else {
			try {
				dos = new DataOutputStream(new BufferedOutputStream(
						getActivity().openFileOutput("tempFile.raw",
								Context.MODE_PRIVATE)));
				short[] buffer = new short[buffersize];
				audioRecord.startRecording();
				while (isRecording) {
					int bufferReadResult = audioRecord.read(buffer, 0,
							buffersize);
					for (int i = 0; i < bufferReadResult; i++) {
						dos.write(buffer[i]);
					}
				}
				audioRecord.stop();
			} catch (FileNotFoundException e) {
				success = false;
				e.printStackTrace();
				return success;
			} catch (IOException e) {
				success = false;
				e.printStackTrace();
				return success;
			} finally {
				try {
					dos.close();
				} catch (IOException e) {
					success = false;
					e.printStackTrace();
					return success;
				}
			}
			if (success) {
				filePath = getActivity().getFileStreamPath(fileName)
						.getAbsolutePath();
			}
			return success;
		}
	}

	protected boolean playback(String filePath) {
		int frequency = 11025;
		int channelConfiguration = AudioFormat.CHANNEL_OUT_MONO;
		int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
		File file = new File(filePath);
		DataInputStream dis = null;
		if (!file.exists()) {
			return false;
		}
		
		int audioLength = (int) (file.length());
		
		short[] audio = new short[audioLength];
	
		try {
			dis = new DataInputStream(new BufferedInputStream(
					new FileInputStream(file)));
			int i = 0;
			while (dis.available() > 0) {
				audio[i] = dis.readShort();
				i++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				dis.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
				frequency, channelConfiguration, audioEncoding, audioLength,
				AudioTrack.MODE_STREAM);
		audioTrack.play();
		audioTrack.write(audio, 0, audioLength);
		audioTrack.stop();
		audioTrack.release();
		audioTrack = null;
		return true;
	}

	/*private void copyWaveFile(String inFilename, String outFilename) {
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = 44100;
		int channels = 2;
		long byteRate = 16 * 44100 * channels / 8;
		byte[] data = new byte[buffersize];
		try {
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;
			WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
					longSampleRate, channels, byteRate);
			while (in.read(data) != -1) {
				out.write(data);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File file = new File(inFilename);
		file.delete();
	}

	protected void WriteWaveFileHeader(FileOutputStream out,
			long totalAudioLen, long totalDataLen, long longSampleRate,
			int channels, long byteRate) throws IOException {
		byte[] header = new byte[44];
		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8); // block align
		header[33] = 0;
		header[34] = 16; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
		out.write(header, 0, 44);
	}*/
/*	private static final String RIFF_HEADER = "RIFF"; 
	private static final String WAVE_HEADER = "WAVE"; 
	private static final String FMT_HEADER = "fmt ";
	private static final String DATA_HEADER = "data"; 
	private static final int HEADER_SIZE = 44; 
	private static final String CHARSET = "ASCII";  ...  
	public static WavInfo readHeader(InputStream wavStream) throws IOException, DecoderException { 
		ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE); 
		buffer.order(ByteOrder.LITTLE_ENDIAN); 
		wavStream.read(buffer.array(), buffer.arrayOffset(), buffer.capacity()); 
		buffer.rewind(); buffer.position(buffer.position() + 20); 
		int format = buffer.getShort(); 
		checkFormat(format == 1, "Unsupported encoding: " + format); // 1 means // Linear // PCM 
		int channels = buffer.getShort(); 
		checkFormat(channels == 1 || channels == 2, "Unsupported channels: " + channels);
		int rate = buffer.getInt(); 
		checkFormat(rate <= 48000 && rate >= 11025, "Unsupported rate: " + rate); 
		buffer.position(buffer.position() + 6);
		int bits = buffer.getShort(); checkFormat(bits == 16, "Unsupported bits: " + bits); 
		int dataSize = 0; while (buffer.getInt() != 0x61746164) { // "data" marker Log.d(TAG, "Skipping non-data chunk");
			int size = buffer.getInt(); wavStream.skip(size); buffer.rewind();
			wavStream.read(buffer.array(), buffer.arrayOffset(), 8); 
			buffer.rewind(); } dataSize = buffer.getInt(); checkFormat(dataSize > 0, "wrong datasize: " + dataSize); 
			return new WavInfo(new FormatSpec(rate, channels == 2), dataSize);
	}
} */
}
