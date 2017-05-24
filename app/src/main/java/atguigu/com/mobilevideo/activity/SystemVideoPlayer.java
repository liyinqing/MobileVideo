package atguigu.com.mobilevideo.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import atguigu.com.mobilevideo.R;
import atguigu.com.mobilevideo.Utils.Utils;
import atguigu.com.mobilevideo.domain.LocalVideoInfo;
import atguigu.com.mobilevideo.view.VideoView;

public class SystemVideoPlayer extends AppCompatActivity implements View.OnClickListener {

    private  LinearLayout ll_loging;
    private TextView tv_loging_net;
    private static final int SHOW_NET = 3;
    private static final int FULL_SCREEN = 1;
    private static final int DEFUALT_SCREEN = 0;
    private boolean isFullScreen = false;
    private  final int HIDE_MEDIACONTROLLER =2 ;
    private VideoView vv;
    private Uri uri;
    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvTime;
    private Button btnVoice;
    private SeekBar seekVice;
    private Button btnSwitch;
    private LinearLayout llBottom;
    private TextView currentTime;
    private SeekBar seekVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnPrevious;
    private Button btnPost;
    private Button btnNext;
    private Button btnDefaultScreen;
    private Utils utils;
    private final int PROGRESS = 1;
    private MyBroadcastReceiver receiver;
    //点击某条传过来的位置
    private int position;

    private GestureDetector detector;

    private boolean isNetUri;

    //之前进度
    private int preCurrentPosition;

    private LinearLayout ll_buffer;
    private TextView tv_net;
    //視頻集合
    private ArrayList<LocalVideoInfo> videoInfos;

    AudioManager am ;
    //当前的音量：0~15之间
    private int currentVoice;
    //最大音量
    private int maxVoice;
    //是否静音
    private boolean isMute = false;

    /**
     * 屏幕的高
     */
    private int screenHeight;
    private int screenWidth;
    //视频的原生的宽和高
    private int videoWidth;
    private int videoHeight;



    /**
     * 是否显示控制面板
     */
    private boolean isShowMediaController = false;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_NET:
                    if(isNetUri){
                        String s = utils.showNetSpeed();
                        tv_net.setText(s);
                        tv_loging_net.setText(s);
                        sendEmptyMessageDelayed(SHOW_NET,1000);
                    }

                    break;
                case  PROGRESS:
                    //設置時間
                    tvTime.setText(getSystemTime());
                    //找到當前視頻播放進度，設置到seekbar中
                    int currentPosition = vv.getCurrentPosition();
                    seekVideo.setProgress(currentPosition);
                    currentTime.setText(utils.stringForTime(currentPosition));
                    //设置视频缓存效果
                    if(isNetUri){
                        int bufferPercentage = vv.getBufferPercentage();//0~100;
                        int totalBuffer = bufferPercentage*seekVideo.getMax();
                        int secondaryProgress =totalBuffer/100;
                        seekVideo.setSecondaryProgress(secondaryProgress);
                    }else{
                        seekVideo.setSecondaryProgress(0);
                    }

//                    if(isNetUri){
//                        int duration = currentPosition - preCurrentPosition;
//                        if(duration < 800){
//                            //ka
//                            ll_buffer.setVisibility(View.VISIBLE);
//                        }else{
//                            ll_buffer.setVisibility(View.GONE);
//                        }
//                        preCurrentPosition = currentPosition;
//                    }
                    sendEmptyMessageDelayed(PROGRESS,1000);
                    break;
                case HIDE_MEDIACONTROLLER://隐藏控制面板
                    hideMediaController();
                    break;

            }
        }
    };
    private String getSystemTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        return format;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video_player);
        vv = (VideoView)findViewById(R.id.vv);
        findViews();
        //初始化工具包
        getData();
        setPlay();
        listener();
        registePlayer();
        setButtonStatus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            vv.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    switch (what) {
                        //拖动卡，缓存卡
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            ll_buffer.setVisibility(View.VISIBLE);
                            break;
                        //拖动卡，缓存卡结束
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            ll_buffer.setVisibility(View.GONE);
                            break;
                    }

                    return true;
                }
            });
        }
    }

    private void getData() {
        utils = new Utils(this);


        detector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {

                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Toast.makeText(SystemVideoPlayer.this, "單機", Toast.LENGTH_SHORT).show();
                if (isShowMediaController) {
                    hideMediaController();
                    handler.removeMessages(HIDE_MEDIACONTROLLER);
                } else {
                    showMediaController();
                    handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);

                }
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {

                Toast.makeText(SystemVideoPlayer.this, "长按了", Toast.LENGTH_SHORT).show();
                setStartOrPause();
                super.onLongPress(e);
            }
        });


    }
    /**
     * 隐藏控制面板
     */
    private void hideMediaController() {
        llBottom.setVisibility(View.GONE);
        llTop.setVisibility(View.GONE);
        isShowMediaController = false;
    }
    public void showMediaController() {
        llBottom.setVisibility(View.VISIBLE);
        llTop.setVisibility(View.VISIBLE);

        isShowMediaController = true;
    }
    private void setPlay() {

        //得到播放视频的地址
        uri = getIntent().getData();
        videoInfos = (ArrayList<LocalVideoInfo>) getIntent().getSerializableExtra("Infos");
        position = getIntent().getIntExtra("position",0);

        if(videoInfos != null){
            String data = videoInfos.get(position).getData();
           // vv.setVideoURI(Uri.parse(data));
            tvName.setText(videoInfos.get(position).getName());
            vv.setVideoPath(data);
            isNetUri = utils.isNetUri(data);
        }else
        if(uri != null){
            vv.setVideoURI(uri);
            tvName.setText(uri.toString());
            //设置控制面板
            //vv.setMediaController(new MediaController(this));
            isNetUri = utils.isNetUri(uri.toString());
        }


    }

    private void registePlayer() {
        //註冊廣播
        IntentFilter filter= new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        receiver = new MyBroadcastReceiver();
        registerReceiver(receiver,filter);
    }


    private class  MyBroadcastReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        int level = intent.getIntExtra("level", 0);
        if(level <= 0){
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        }else
        if(level <= 10){
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        }else
        if(level <= 20){
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        }else
        if(level <= 40){
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        }else
        if(level <= 60){
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        }else if(level <= 80){
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        }else if(level<= 100){
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }
}
    //记录坐标
    private float dowY;
    //滑动的初始声音
    private int mVol;
    //滑动的最大区域
    private float touchRang;

    private float startY = 0;//手指按下时的Y坐标
    private float startX = 0;//手指按下时的Y坐标
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();

                        //1.记录相关参数
                        //dowY = event.getY();
                        mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                        touchRang = Math.min(screenHeight,screenWidth);//screenHeight
                       handler.removeMessages(HIDE_MEDIACONTROLLER);
                        break;
                    case MotionEvent.ACTION_MOVE:

                        float endY = event.getY();
                        float distanceY = startY - endY;
                        if (startX > screenWidth / 2) {
                            llTop.setVisibility(View.VISIBLE);
                            //右边
                            //在这里处理音量
                            //2.滑动的时候来到新的位置
                           // float endY = event.getY();
                            //3.计算滑动的距离
                           // float distanceY = dowY - endY;
                            //原理：在屏幕滑动的距离： 滑动的总距离 = 要改变的声音： 最大声音
                            //要改变的声音 = （在屏幕滑动的距离/ 滑动的总距离）*最大声音;
                            float delta = (distanceY/touchRang)*maxVoice;
                            if(delta != 0){
                                //最终声音 = 原来的+ 要改变的声音
                                int mVoice = (int) Math.min(Math.max(mVol+delta,0),maxVoice);
                                //0~15
                                updateVoiceProgress(mVoice);
                            }
                        } else {
                            //屏幕左半部分上滑，亮度变大，下滑，亮度变小
                            final double FLING_MIN_DISTANCE = 0.1;
                            final double FLING_MIN_VELOCITY = 0.0;
                            if (distanceY > FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
                                setBrightness(1000);
                            }
                            if (distanceY < FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
                                setBrightness(-1090);
                            }
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
                        break;
        }
        return super.onTouchEvent(event);
    }

    /*
  * 设置屏幕亮度
  * 0 最暗
  * 1 最亮
  */
    public void setBrightness(float brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
        } else if (lp.screenBrightness < 0.1) {
            lp.screenBrightness = (float) 0.1;
        }
        getWindow().setAttributes(lp);

        //float sb = lp.screenBrightness;
        //brightnessTextView.setText((int) Math.ceil(sb * 100) + "%");
    }
    /**
     * 设置滑动改变声音
     * @param progress
     */
    private void updateVoiceProgress(int progress) {
        currentVoice = progress;
        //真正改变声音
        am.setStreamVolume(AudioManager.STREAM_MUSIC,currentVoice,0);
        //改变进度条
        seekVice.setProgress(currentVoice);
        if(currentVoice <=0){
            isMute = true;
        }else {
            isMute = false;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            currentVoice--;
            updateVoiceProgress(currentVoice);
          //  handler.removeMessages(HIDE_MEDIACONTROL);
          //  handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROL, 5000);
            return true;
        }else if(keyCode ==KeyEvent.KEYCODE_VOLUME_UP){
            currentVoice++;
            updateVoiceProgress(currentVoice);
          //  handler.removeMessages(HIDE_MEDIACONTROL);
          ///  handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROL, 5000);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    private void listener() {
        //监听拖动声音
        seekVice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    updateVoiceProgress(progress);
                }

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        //设置播放器的三个监听
        /**
         * 准备好播放的时候回调
         */
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {


                //得到视频的宽和高
                videoWidth = mp.getVideoWidth();
                videoHeight = mp.getVideoHeight();
                //得到視頻的總時長
                int duration = vv.getDuration();
                //seekbar的長度
                seekVideo.setMax(duration);
                //設置視頻的總時長，需要工具包裝換
                tvDuration.setText(utils.stringForTime(duration));
                //开始播放
                vv.start();
                setVideoType(DEFUALT_SCREEN);
                ll_loging.setVisibility(View.GONE);
                //開始更新播放進度
                handler.sendEmptyMessage(PROGRESS);
                hideMediaController();
                ///setStartOrPause()
                btnPost.setBackgroundResource(R.drawable.btn_post_selector);

            }
        });
        /**
         * 播放出错的回调
         */
        vv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(SystemVideoPlayer.this, "播放出错", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SystemVideoPlayer.this,VitmioVideoPlayer.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Infos", videoInfos);
                intent.putExtras(bundle);
                intent.putExtra("position",position);
                intent.setData(uri);
                startActivity(intent);
                finish();
                return true;
            }
        });
        /**
         * 播放完成的时候回调
         */
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(SystemVideoPlayer.this, "播放完成", Toast.LENGTH_SHORT).show();
                setNextVideo();
                //finish();

            }
        });

        seekVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            /**
             * 用戶拖動的時候回調
             * @param seekBar
             * @param progress
             * @param fromUser 為true 是用戶拖動的
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    vv.seekTo(progress);
                }
            }

            /**
             *
             * @param seekBar
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-05-21 18:26:27 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        //得到屏幕的宽和高
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;

        //初始化声音相关
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        llTop = (LinearLayout)findViewById( R.id.ll_top );
        tvName = (TextView)findViewById( R.id.tv_name );
        ivBattery = (ImageView)findViewById( R.id.iv_battery );
        tvTime = (TextView)findViewById( R.id.tv_time );
        btnVoice = (Button)findViewById( R.id.btn_voice );
        seekVice = (SeekBar)findViewById( R.id.seek_vice );
        btnSwitch = (Button)findViewById( R.id.btn_switch );
        llBottom = (LinearLayout)findViewById( R.id.ll_bottom );
        currentTime = (TextView)findViewById( R.id.current_time );
        seekVideo = (SeekBar)findViewById( R.id.seek_video );
        tvDuration = (TextView)findViewById( R.id.tv_duration );
        btnExit = (Button)findViewById( R.id.btn_exit );
        btnPrevious = (Button)findViewById( R.id.btn_previous );
        btnPost = (Button)findViewById( R.id.btn_post );
        btnNext = (Button)findViewById( R.id.btn_next );
        btnDefaultScreen = (Button)findViewById( R.id.btn_default_screen );

        ll_buffer = (LinearLayout)findViewById(R.id.ll_buffer);
        tv_net = (TextView)findViewById(R.id.tv_net);

        ll_loging = (LinearLayout)findViewById(R.id.ll_loging);
        tv_loging_net = (TextView)findViewById(R.id.tv_loging_net);

        btnVoice.setOnClickListener( this );
        btnSwitch.setOnClickListener( this );
        btnExit.setOnClickListener( this );
        btnPrevious.setOnClickListener( this );
        btnPost.setOnClickListener( this );
        btnNext.setOnClickListener( this );
        btnDefaultScreen.setOnClickListener( this );

        //关联最大音量
        seekVice.setMax(maxVoice);
        //设置当前进度
        seekVice.setProgress(currentVoice);

        handler.sendEmptyMessage(SHOW_NET);


    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-05-21 18:26:27 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */

    private void updateVoice(boolean isMute) {
        if(isMute){
            //静音
            am.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
            seekVice.setProgress(0);
        }else{
            //非静音
            am.setStreamVolume(AudioManager.STREAM_MUSIC,currentVoice,0);
            seekVice.setProgress(currentVoice);
        }
    }
    @Override
    public void onClick(View v) {
        //聲音按鈕
        if ( v == btnVoice ) {
            isMute = !isMute;
            updateVoice(isMute);
        } else
            //選擇播放視頻按鈕
        if ( v == btnSwitch ) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("当前是系统播放器，是否要切换万能播放器播放")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startVitmioPlyer();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        } else
            //返回退出按鈕
        if ( v == btnExit ) {
            finish();
        } else
            //上一個按鈕
        if ( v == btnPrevious ) {
            setPreVideo();
        } else
        //暫停播放按鈕
        if ( v == btnPost ) {
            setStartOrPause();
        } else
        //下一個按鈕
        if ( v == btnNext ) {
            setNextVideo();
        } else
        //切換全屏按鈕
        if ( v == btnDefaultScreen ) {
            if (isFullScreen) {
                //默认
                setVideoType(DEFUALT_SCREEN);
            } else {
                //全屏
                setVideoType(FULL_SCREEN);
            }
        }
        handler.removeMessages(HIDE_MEDIACONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
//       handler.rEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
    }

    private void setNextVideo() {
        position++;
        if (position > 0) {
            //还是在列表范围内容
            LocalVideoInfo videoInfo = videoInfos.get(position);
            ll_loging.setVisibility(View.VISIBLE);
            vv.setVideoPath(videoInfo.getData());
            tvName.setText(videoInfo.getName());
            isNetUri = utils.isNetUri(videoInfo.getData());
            //设置按钮状态
            setButtonStatus();
        }
    }


    /**
     * 设置视频的全屏和默认
     * @param videoType
     */
    private void setVideoType(int videoType) {
        switch (videoType) {
            case FULL_SCREEN:
                isFullScreen = true;
                //按钮状态-默认
                btnDefaultScreen.setBackgroundResource(R.drawable.btn_default_screen_selector);
//                btnDefaultScreen.setBackgroundResource(R.drawable.btn_show_screen_selector);
                //设置视频画面为全屏显示
//                vv.setVideoSize(screenWidth, screenHeight);
                vv.setVideoSize(screenWidth,screenHeight);
                break;
            case DEFUALT_SCREEN:
                isFullScreen = false;
                //按钮状态-全屏
                btnDefaultScreen.setBackgroundResource(R.drawable.btn_show_screen_selector);
//                btnDefaultScreen.setBackgroundResource(R.drawable.btn_default_screen_selector);
                //视频原生的宽和高
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;

                //计算好的要显示的视频的宽和高
                int width = screenWidth;
                int height = screenHeight;

                // for compatibility, we adjust size based on aspect ratio
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
                vv.setVideoSize(width, height);

                break;
        }
    }

    private void startVitmioPlyer() {
        if(vv != null){
            vv.stopPlayback();
        }
        Intent intent = new Intent(this,VitmioVideoPlayer.class);

        if(videoInfos != null && videoInfos.size() >0){
            //传递视频列表
            Bundle bundle = new Bundle();
            bundle.putSerializable("Infos",videoInfos);

            intent.putExtras(bundle);

            //视频的列表中的某条位置
            intent.putExtra("position",position);
        }else  if(uri != null){
            intent.setData(uri);
        }


        startActivity(intent);
        finish();
    }

    private void setStartOrPause() {
        if(vv.isPlaying()){
            vv.pause();
            btnPost.setBackgroundResource(R.drawable.btn_start_selector);
        }else{
            vv.start();
            btnPost.setBackgroundResource(R.drawable.btn_post_selector);
        }
    }

    private void setPreVideo() {
        position--;
        if (position > 0) {
            //还是在列表范围内容
            LocalVideoInfo videoInfo = videoInfos.get(position);
            vv.setVideoPath(videoInfo.getData());
            ll_loging.setVisibility(View.VISIBLE);
            tvName.setText(videoInfo.getName());
            isNetUri = utils.isNetUri(videoInfo.getData());
            //设置按钮状态
            setButtonStatus();
        }
    }
    private void setButtonStatus() {
        if (videoInfos != null && videoInfos.size() > 0) {
            //有视频播放
            setEnable(true);

            if (position == 0) {
                btnPrevious.setBackgroundResource(R.drawable.btn_pre_gray);
                btnPrevious.setEnabled(false);
            }

            if (position == videoInfos.size() - 1) {
                btnNext.setBackgroundResource(R.drawable.btn_next_gray);
                btnNext.setEnabled(false);
            }

        } else if (uri != null) {
            //上一个和下一个不可用点击
            setEnable(false);
        }
    }
    /**
     * 设置按钮是否可以点击
     *
     * @param b
     */
    private void setEnable(boolean b) {
        if (b) {
            //上一个和下一个都可以点击
            btnPrevious.setBackgroundResource(R.drawable.btn_previous_selector);
            btnNext.setBackgroundResource(R.drawable.btn_next_selector);
        } else {
            //上一个和下一个灰色，并且不可用点击
            btnPrevious.setBackgroundResource(R.drawable.btn_pre_gray);
            btnNext.setBackgroundResource(R.drawable.btn_next_gray);
        }
        btnPrevious.setEnabled(b);
        btnNext.setEnabled(b);
    }
    @Override
    protected void onDestroy() {

        //線釋放子類的在釋放父類的
        handler.removeCallbacksAndMessages(null);

        //取消註冊廣播
        if(receiver != null){
            unregisterReceiver(receiver);
            receiver = null;
        }

        super.onDestroy();//如果這個方法釋放父類后置為null那麼這裡有可能有空指針，所以要先釋放子類的



    }
}
