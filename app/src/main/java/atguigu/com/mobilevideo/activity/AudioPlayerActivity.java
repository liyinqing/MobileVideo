package atguigu.com.mobilevideo.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

import atguigu.com.mobilevideo.IMusicPlayService;
import atguigu.com.mobilevideo.R;
import atguigu.com.mobilevideo.Utils.LyricUtils;
import atguigu.com.mobilevideo.Utils.Utils;
import atguigu.com.mobilevideo.domain.LocalVideoInfo;
import atguigu.com.mobilevideo.domain.Lyric;
import atguigu.com.mobilevideo.service.MusicPlayService;
import atguigu.com.mobilevideo.view.LyricView;

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
    private TextView tv_time;
    private Utils utils;
    //判断来自哪里的意图
    private boolean from_notification;

    private LyricView lyricView;

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
                if (from_notification) {
                    setViewData(null);
                } else {
                    if (service != null) {
                        service.openAudio(position);
                        String artistName = service.getArtistName();
                        String audioName = service.getAudioName();
                        tvArtist.setText(artistName);
                        tvAudioName.setText(audioName);
                    }
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
        ivIncon = (ImageView) findViewById(iv_incon);

        //初始化后实现动画效果
        ivIncon.setBackgroundResource(R.drawable.now_playing_matrix_selector);
        AnimationDrawable drawable = (AnimationDrawable) ivIncon.getBackground();
        drawable.start();
        tv_time = (TextView) findViewById(R.id.tv_time);
        rlTop = (RelativeLayout) findViewById(R.id.rl_top);
        ivIncon = (ImageView) findViewById(iv_incon);
        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvAudioName = (TextView) findViewById(R.id.tv_audioName);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        seekAudio = (SeekBar) findViewById(R.id.seek_audio);
        btnNormal = (Button) findViewById(R.id.btn_normal);
        btnPrevious = (Button) findViewById(R.id.btn_previous);
        btnPost = (Button) findViewById(R.id.btn_post);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnLyrics = (Button) findViewById(R.id.btn_lyrics);

        lyricView = (LyricView) findViewById(R.id.lyricView);

        btnNormal.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        btnPost.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnLyrics.setOnClickListener(this);

        seekAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
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
        if (v == btnNormal) {
            setPlayerMode();
        } else if (v == btnPrevious) {
            try {
                service.pre();
                btnPost.setBackgroundResource(R.drawable.btn_audio_post_selector);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (v == btnPost) {
            try {
                if (service.isPlayer()) {
                    try {
                        service.pause();
                        btnPost.setBackgroundResource(R.drawable.btn_audio_start_selector);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
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
        } else if (v == btnNext) {
            try {
                service.next();
                btnPost.setBackgroundResource(R.drawable.btn_audio_post_selector);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (v == btnLyrics) {

        }
    }

    private void setPlayerMode() {
        try {
            int playermode = service.getPlayermode();
            if (playermode == MusicPlayService.PLAYER_NORMAL) {
                playermode = MusicPlayService.PLAYER_SINGLE;
            } else if (playermode == MusicPlayService.PLAYER_SINGLE) {
                playermode = MusicPlayService.PLAYER_ALL;
            } else if (playermode == MusicPlayService.PLAYER_ALL) {
                playermode = MusicPlayService.PLAYER_NORMAL;
            }
            service.setPlayermode(playermode);
            setButtonImage();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void setButtonImage() {
        try {
            int playermode = service.getPlayermode();
            if (playermode == MusicPlayService.PLAYER_NORMAL) {
                btnNormal.setBackgroundResource(R.drawable.btn_now_playing_normal_selector);
                Toast.makeText(AudioPlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
            } else if (playermode == MusicPlayService.PLAYER_SINGLE) {
                btnNormal.setBackgroundResource(R.drawable.btn_now_playing_single_selector);
                Toast.makeText(AudioPlayerActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
            } else if (playermode == MusicPlayService.PLAYER_ALL) {
                btnNormal.setBackgroundResource(R.drawable.btn_now_playing_all_selector);
                Toast.makeText(AudioPlayerActivity.this, "全部循环", Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ;
        findViews();
        initData();
        startAndBindService();
        getDate();

    }

    private void initData() {
        utils = new Utils(this);
        //注册广播
//        receiver = new MyReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(MusicPlayService.OPENCOMPLETE);
//        registerReceiver(receiver,filter);
        //注册EVenBus
        EventBus.getDefault().register(this);

    }
//    class MyReceiver extends BroadcastReceiver{
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            //设置view的数据
//            setViewData(null);
//        }
//    }


    private final static int PROGRESS = 1;
    private final int SHOW_LYRIC = 2;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_LYRIC:
                    try {
                        int currentPosition = service.getCurrentPosition();
                        lyricView.setShowNextLyric(currentPosition);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    removeMessages(SHOW_LYRIC);
                    sendEmptyMessageDelayed(SHOW_LYRIC, 1000);
                    break;

                case PROGRESS:
                    try {
                        int currentPosition = service.getCurrentPosition();
                        seekAudio.setProgress(currentPosition);
                        tv_time.setText(utils.stringForTime(currentPosition) + "/" + utils.stringForTime(service.getDuration()));

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessage(PROGRESS);
                    break;
            }

        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setViewData(LocalVideoInfo videoInfo) {
        try {

            tvArtist.setText(service.getArtistName());
            tvAudioName.setText(service.getAudioName());
            seekAudio.setMax(service.getDuration());
            setButtonImage();

            String audioPath = service.getAudioPath();
            Log.e("TAG","====================================="+audioPath);
            String substring = audioPath.substring(0, audioPath.lastIndexOf("."));
            File file = new File(substring + ".lrc");
            Log.e("TAG","====================================="+file);
            if (!file.exists()) {
                file = new File(substring + ".txt");
                Log.e("TAG","====================================="+file);
            }
            LyricUtils lyricUtils = new LyricUtils();
            lyricUtils.readFile(file);

            ArrayList<Lyric> lyrics = lyricUtils.getLyrics();
            for(int i = 0; i < lyrics.size(); i++) {
                Log.e("name","============"+lyrics.get(i).toString());
            }
            lyricView.setLyrics(lyrics);

            if (lyricUtils.isLyric()) {
                handler.sendEmptyMessage(SHOW_LYRIC);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        handler.sendEmptyMessage(PROGRESS);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (conn != null) {
            unbindService(conn);
            conn = null;
        }
        //解除广播
//        if(receiver != null){
//            unregisterReceiver(receiver);
//            receiver = null;
//        }
        //解除EvenBus
        EventBus.getDefault().unregister(this);
    }

    private void getDate() {
        from_notification = getIntent().getBooleanExtra("from_notification", false);
        if (!from_notification) {
            position = getIntent().getIntExtra("position", 0);
        }

    }

    private void startAndBindService() {
        //开启服务
        Intent intent = new Intent(this, MusicPlayService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }
}
