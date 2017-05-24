package atguigu.com.mobilevideo.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
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
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;

public class VitmioVideoPlayer extends AppCompatActivity implements View.OnClickListener {

    private static final int SHOW_NET = 3;
    private  final int HIDE_MEDIACONTROLLER =2 ;
    private VideoView  vv;
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
                    String s = utils.showNetSpeed();
                    tv_net.setText(s);
                    sendEmptyMessageDelayed(SHOW_NET,1000);
                    break;
                case  PROGRESS:
                    //設置時間
                    tvTime.setText(getSystemTime());
                    //找到當前視頻播放進度，設置到seekbar中
                    int currentPosition = (int) vv.getCurrentPosition();
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

                    if(isNetUri){
                        int duration = currentPosition - preCurrentPosition;
                        if(duration < 500){
                            //ka
                            ll_buffer.setVisibility(View.VISIBLE);
                        }else{
                            ll_buffer.setVisibility(View.GONE);
                        }
                        preCurrentPosition = currentPosition;
                    }
                    sendEmptyMessageDelayed(PROGRESS,1000);

                case HIDE_MEDIACONTROLLER://隐藏控制面板
                    //hideMediaController();
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
        Vitamio.isInitialized(getApplicationContext());
        setContentView(R.layout.activity_vitamio_video_player);
        vv = (VideoView) findViewById(R.id.vv);
        findViews();
        //初始化工具包
        getData();
        setPlay();
        listener();
        registePlayer();
        setButtonStatus();

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
                Toast.makeText(VitmioVideoPlayer.this, "單機", Toast.LENGTH_SHORT).show();
                if (isShowMediaController) {
                    showMediaController();

                   // handler.removeMessages(HIDE_MEDIACONTROLLER);
                } else {
                    hideMediaController();
                   // handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {

                Toast.makeText(VitmioVideoPlayer.this, "长按了", Toast.LENGTH_SHORT).show();
                setStartOrPause();
                super.onLongPress(e);
            }
        });


    }
    /**
     * 隐藏控制面板
     */
    private void hideMediaController() {
        llBottom.setVisibility(View.INVISIBLE);
        llTop.setVisibility(View.GONE);
        isShowMediaController = true;
    }
    public void showMediaController() {
        llBottom.setVisibility(View.VISIBLE);
        llTop.setVisibility(View.VISIBLE);
        isShowMediaController = false;
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
        }

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
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //1.记录相关参数
                        dowY = event.getY();
                        mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                        touchRang = Math.min(screenHeight,screenWidth);//screenHeight
                       // handler.removeMessages(HIDE_MEDIACONTROLLER);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //2.滑动的时候来到新的位置
                        float endY = event.getY();
                        //3.计算滑动的距离
                        float distanceY = dowY - endY;
                        //原理：在屏幕滑动的距离： 滑动的总距离 = 要改变的声音： 最大声音
                        //要改变的声音 = （在屏幕滑动的距离/ 滑动的总距离）*最大声音;
                        float delta = (distanceY/touchRang)*maxVoice;


                        if(delta != 0){
                            //最终声音 = 原来的+ 要改变的声音
                            int mVoice = (int) Math.min(Math.max(mVol+delta,0),maxVoice);
                            //0~15
                            updateVoiceProgress(mVoice);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                       // handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
                        break;
        }
        return super.onTouchEvent(event);
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

                //得到視頻的總時長
                int duration = (int) vv.getDuration();
                //seekbar的長度
                seekVideo.setMax(duration);
                //設置視頻的總時長，需要工具包裝換
                tvDuration.setText(utils.stringForTime(duration));
                //开始播放
                vv.start();
                //開始更新播放進度
                handler.sendEmptyMessage(PROGRESS);
               // hideMediaController();
            }
        });
        /**
         * 播放出错的回调
         */
        vv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                showErrorDialog();
                return true;
            }
        });
        /**
         * 播放完成的时候回调
         */
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(VitmioVideoPlayer.this, "播放完成", Toast.LENGTH_SHORT).show();
                finish();
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

    private void showErrorDialog() {
        new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("播放錯誤")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
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
                                    startSystemPlyer();
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

        }
    }

    private void setNextVideo() {
        position++;
        if (position > 0) {
            //还是在列表范围内容
            LocalVideoInfo videoInfo = videoInfos.get(position);
            vv.setVideoPath(videoInfo.getData());
            tvName.setText(videoInfo.getName());
            //设置按钮状态
            setButtonStatus();
        }
    }

    private void startSystemPlyer() {
        if(vv != null){
            vv.stopPlayback();
        }
        Intent intent = new Intent(this,SystemVideoPlayer.class);

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
            tvName.setText(videoInfo.getName());

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
