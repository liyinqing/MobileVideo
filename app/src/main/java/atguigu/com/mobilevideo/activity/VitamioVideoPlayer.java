package atguigu.com.mobilevideo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
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

public class VitamioVideoPlayer extends AppCompatActivity implements View.OnClickListener {

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

    private boolean isNetUri;

    //之前进度
    private int preCurrentPosition;

    private LinearLayout ll_buffer;
    private TextView tv_net;
    //視頻集合
    private ArrayList<LocalVideoInfo> videoInfos;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
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


                    handler.sendEmptyMessageDelayed(PROGRESS,1000);
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
        vv = (VideoView)findViewById(R.id.vv);
        findViews();
        //初始化工具包
        utils = new Utils();
        setPlay();
        //
        listener();
        registePlayer();

    }

    private void setPlay() {

        //得到播放视频的地址
        uri = getIntent().getData();
        videoInfos = (ArrayList<LocalVideoInfo>) getIntent().getSerializableExtra("Infos");
        position = getIntent().getIntExtra("position",0);

        if(videoInfos != null){
            String data = videoInfos.get(position).getData();
           // vv.setVideoURI(Uri.parse(data));
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
    private void listener() {
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
            }
        });
        /**
         * 播放出错的回调
         */
        vv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
        /**
         * 播放完成的时候回调
         */
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(VitamioVideoPlayer.this, "播放完成", Toast.LENGTH_SHORT).show();
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

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-05-21 18:26:27 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
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
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-05-21 18:26:27 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        //聲音按鈕
        if ( v == btnVoice ) {

        } else
            //選擇播放視頻按鈕
        if ( v == btnSwitch ) {

        } else
            //返回退出按鈕
        if ( v == btnExit ) {

        } else
            //上一個按鈕
        if ( v == btnPrevious ) {
            setPreVideo();
        } else
        //暫停播放按鈕
        if ( v == btnPost ) {
            if(vv.isPlaying()){
                vv.pause();
                btnPost.setBackgroundResource(R.drawable.btn_start_selector);
            }else{
                vv.start();
                btnPost.setBackgroundResource(R.drawable.btn_post_selector);
            }
        } else
        //下一個按鈕
        if ( v == btnNext ) {

        } else
        //切換全屏按鈕
        if ( v == btnDefaultScreen ) {

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
