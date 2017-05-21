package atguigu.com.mobilevideo.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import atguigu.com.mobilevideo.R;

public class SystemVideoPlayer extends AppCompatActivity implements View.OnClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video_player);
        vv = (VideoView)findViewById(R.id.vv);
        findViews();
        //得到播放视频的地址
        uri = getIntent().getData();
        listener();

        vv.setVideoURI(uri);
        //设置控制面板
        //vv.setMediaController(new MediaController(this));
    }

    private void listener() {
        //设置播放器的三个监听
        /**
         * 准备好播放的时候回调
         */
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //开始播放
                vv.start();
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
                Toast.makeText(SystemVideoPlayer.this, "播放完成", Toast.LENGTH_SHORT).show();
                finish();
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


}
