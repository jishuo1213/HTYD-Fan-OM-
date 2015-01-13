package com.htyd.fan.om.util.fragment;

import java.io.File;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.htyd.fan.om.util.base.PictureUtils;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.ui.UItoolKit;



public class PhotoViewer extends Activity implements OnTouchListener {
	
	public static final String PICTUREPATH = "filepath";
	
	public static final int RESULT_CODE_NOFOUND = 200;
	private long time;
	private Boolean boolend=true;
	private Float xx,yy;
	
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    DisplayMetrics dm;
    ImageView imgView;
    Bitmap bitmap;

    /** ��С���ű���*/
    float minScaleR = 1.0f;
    /** ������ű���*/
    static final float MAX_SCALE = 10f;

    /** ��ʼ״̬*/
    static final int NONE = 0;
    /** �϶�*/
    static final int DRAG = 1;
    /** ����*/
    static final int ZOOM = 2;
    
    /** ��ǰģʽ*/
    int mode = NONE;

    PointF prev = new PointF();
    PointF mid = new PointF();
    float dist = 1f;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // ��ȡͼƬ��Դ
		//bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aaaaa);
        String path = getIntent().getStringExtra(PICTUREPATH);
        imgView = new ImageView(PhotoViewer.this);
        setContentView(imgView);
        imgView.setScaleType(ScaleType.MATRIX);
		File file  = new File(path);
		if (!file.exists()) {
			OMUserDatabaseManager.getInstance(getBaseContext()).detelteTaskAccessory(path);
			imgView.setImageDrawable(null);
			UItoolKit.showToastShort(getBaseContext(), "文件不存在，是不是被你删除了");
			return;
		}
		bitmap = PictureUtils.getScaledBitmap(PhotoViewer.this, path);
		imgView.setImageBitmap(bitmap);// ���ؼ�
        imgView.setOnTouchListener(this);// ���ô�������
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);// ��ȡ�ֱ���
        minZoom();
        center();
        imgView.setImageMatrix(matrix);
    }

    @Override
    protected void onDestroy() {
    	PictureUtils.cleanImageView(imgView);
    	super.onDestroy();
    }
    
    public void SureOnClick(View v)
    {
    	
    }
    
    /**
     * ��������
     */
    @SuppressLint("ClickableViewAccessibility")
	public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        // ���㰴��
        case MotionEvent.ACTION_DOWN:
            savedMatrix.set(matrix);
            prev.set(event.getX(), event.getY());
            mode = DRAG;
            if(boolend){
             	time = new Date().getTime();//��¼��һ�ΰ���ʱ��
             	xx=event.getX();
             	yy = event.getY();
             	boolend = false;
             }
            break;
        // ���㰴��
        case MotionEvent.ACTION_POINTER_DOWN:
            dist = spacing(event);
            // �����������������10�����ж�Ϊ���ģʽ
            if (spacing(event) > 10f) {
                savedMatrix.set(matrix);
                midPoint(mid, event);
                mode = ZOOM;
            }
            break;
        case MotionEvent.ACTION_UP:
        	if((new Date().getTime()-time)<300 && (new Date().getTime()-time)>30 && Math.abs((event.getX()-xx)) <= 10f &&Math.abs((event.getY()-yy)) <= 10f ){
        		finish(); 
        	}
        	boolend=true;
        	break;
        case MotionEvent.ACTION_POINTER_UP:
            mode = NONE;
            //savedMatrix.set(matrix);
            break;
        case MotionEvent.ACTION_MOVE:
            if (mode == DRAG) {
                matrix.set(savedMatrix);
                matrix.postTranslate(event.getX() - prev.x, event.getY()
                        - prev.y);
            } else if (mode == ZOOM) {
                float newDist = spacing(event);
                if (newDist > 10f) {
                    matrix.set(savedMatrix);
                    float tScale = newDist / dist;
                    matrix.postScale(tScale, tScale, mid.x, mid.y);
                }
            }
            break;
        }
        imgView.setImageMatrix(matrix);
        CheckView();
        return true;
    }

    /**
     * ���������С���ű������Զ�����
     */
    private void CheckView() {
        float p[] = new float[9];
        matrix.getValues(p);
        if (mode == ZOOM) {
            if (p[0] < minScaleR) {
            	//Log.d("", "��ǰ���ż���:"+p[0]+",��С���ż���:"+minScaleR);
                matrix.setScale(minScaleR, minScaleR);
            }
            if (p[0] > MAX_SCALE) {
            	//Log.d("", "��ǰ���ż���:"+p[0]+",������ż���:"+MAX_SCALE);
                matrix.set(savedMatrix);
            }
        }
        center();
    }

    /**
     * ��С���ű��������Ϊ100%
     */
    private void minZoom() {
        minScaleR = Math.min(
                (float) dm.widthPixels / (float) bitmap.getWidth(),
                (float) dm.heightPixels / (float) bitmap.getHeight());
        if (minScaleR < 1.0) {
            matrix.postScale(minScaleR, minScaleR);
        }
    }

    private void center() {
        center(true, true);
    }

    /**
     * �����������
     */
    protected void center(boolean horizontal, boolean vertical) {

        Matrix m = new Matrix();
        m.set(matrix);
        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        if (vertical) {
            // ͼƬС����Ļ��С���������ʾ��������Ļ���Ϸ������������ƣ��·�������������
            int screenHeight = dm.heightPixels;
            if (height < screenHeight) {
                deltaY = (screenHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < screenHeight) {
                deltaY = imgView.getHeight() - rect.bottom;
            }
        }

        if (horizontal) {
            int screenWidth = dm.widthPixels;
            if (width < screenWidth) {
                deltaX = (screenWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < screenWidth) {
                deltaX = screenWidth - rect.right;
            }
        }
        matrix.postTranslate(deltaX, deltaY);
    }

    /**
     * ����ľ���
     */
    @SuppressLint("FloatMath")
	private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /**
     * ������е�
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
    
  
}