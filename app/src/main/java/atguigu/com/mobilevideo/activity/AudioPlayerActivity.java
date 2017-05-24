package atguigu.com.mobilevideo.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import atguigu.com.mobilevideo.IMusicPlayService;
import atguigu.com.mobilevideo.R;
import atguigu.com.mobilevideo.Utils.Utils;
import atguigu.com.mobilevideo.service.MusicPlayService;

import static atguigu.com.mobilevideo.R.id.iv_incon;

public class AudioPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout rlTop;
    private ImageView ivIncon;
    private TextView tvArtist;
    private TextView tvAudioName;
    private LinearLayout llBottom;
    private SeekBar seekAudio;
    private Button btnNormal;
    private Button btnPrevious;
    private Button btnPost;
    private Button btnNext;
    private Button btnLyrics;
    private IMusicPlayService service;
    private int position;
    private MyReceiver receiver;
    private TextView tv_time;
    private Utils utils;
    //服务连接
    private ServiceConnection conn = new ServiceConnection() {
        /**
         *  //服务连接成功后的回调
         * @param name
         * @param iBinder 就是服务中IMusicPlayService的实例
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service = IMusicPlayService.Stub.asInterface(iBinder);

            try {
                if(service != null) {
                    service.openAudio(position);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        //断开连接的时候
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };



    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-05-24 19:20:16 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_audio_player);
        ivIncon = (ImageView)findViewById(iv_incon);

        //初始化后实现动画效果
        ivIncon.setBackgroundResource(R.drawable.now_playing_matrix_selector);
        AnimationDrawable drawable = (AnimationDrawable) ivIncon.getBackground();
        drawable.start();
        tv_time = (TextView)findViewById(R.id.tv_time);
        rlTop = (RelativeLayout)findViewById( R.id.rl_top );
        ivIncon = (ImageView)findViewById( iv_incon );
        tvArtist = (TextView)findViewById( R.id.tv_artist );
        tvAudioName = (TextView)findViewById( R.id.tv_audioName );
        llBottom = (LinearLayout)findViewById( R.id.ll_bottom );
        seekAudio = (SeekBar)findViewById( R.id.seek_audio );
        btnNormal = (Button)findViewById( R.id.btn_normal );
        btnPrevious = (Button)findViewById( R.id.btn_previous );
        btnPost = (Button)findViewById( R.id.btn_post );
        btnNext = (Button)findViewById( R.id.btn_next );
        btnLyrics = (Button)findViewById( R.id.btn_lyrics );

        btnNormal.setOnClickListener( this );
        btnPrevious.setOnClickListener( this );
        btnPost.setOnClickListener( this );
        btnNext.setOnClickListener( this );
        btnLyrics.setOnClickListener( this );

        seekAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-05-24 19:20:16 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == btnNormal ) {
            // Handle clicks for btnNormal
        } else if ( v == btnPrevious ) {
            // Handle clicks for btnPrevious
        } else if ( v == btnPost ) {
            try {
                if(service.isPlayer()){
                    try {
                        service.pause();
                        btnPost.setBackgroundResource(R.drawable.btn_audio_start_selector);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        service.start();
                        btnPost.setBackgroundResource(R.drawable.btn_post_selector);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // Handle clicks for btnPost
        } else if ( v == btnNext ) {
            // Handle clicks for btnNext
        } else if ( v == btnLyrics ) {
            // Handle clicks for btnLyrics
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
       ;findViews();
        startAndBindService();
        getDate();
    }

    private void initData() {
        utils = new Utils(this);
        //注册广播
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicPlayService.OPENCOMPLETE);
        registerReceiver(receiver,filter);
    }
    class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //设置view的数据
            setViewData();
        }
    }
    private final static int PROGRESS = 1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case PROGRESS:
                    try {
                        int currentPosition = service.getCurrentPosition();
                        seekAudio.setProgress(currentPosition);
                        tv_time.setText(utils.stringForTime(currentPosition) +"/"+ utils.stringForTime(service.getDuration()));

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessageDelayed(PROGRESS,1000);

                    break;
            }

        }
    };
    private void setViewData() {
        try {
            String artistName = service.getArtistName();
            String audioName = service.getAudioName();
            int duration = service.getDuration();
            tvArtist.setText(artistName);
            tvAudioName.setText(audioName);
            seekAudio.setMax(duration);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        handler.sendEmptyMessage(PROGRESS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(conn != null){
            unbindService(conn);
            conn = null;
        }
        //解除广播
        if(receiver != null){
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private void getDate() {
        position = getIntent().getIntExtra("position", 0);
    }

    private void startAndBindService() {
        //开启服务
        Intent intent = new Intent(this, MusicPlayService.class);

        bindService(intent,conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }
}
