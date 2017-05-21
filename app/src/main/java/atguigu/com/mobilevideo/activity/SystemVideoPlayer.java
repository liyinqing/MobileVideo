package atguigu.com.mobilevideo.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import atguigu.com.mobilevideo.R;

public class SystemVideoPlayer extends AppCompatActivity {

    private VideoView vv;
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video_player);
        vv = (VideoView)findViewById(R.id.vv);

        //得到播放视频的地址
        uri = getIntent().getData();

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
        vv.setVideoURI(uri);
        //设置控制面板
        vv.setMediaController(new MediaController(this));
    }
}
