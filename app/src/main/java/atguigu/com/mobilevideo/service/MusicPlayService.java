package atguigu.com.mobilevideo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;

import atguigu.com.mobilevideo.IMusicPlayService;
import atguigu.com.mobilevideo.R;
import atguigu.com.mobilevideo.activity.AudioPlayerActivity;
import atguigu.com.mobilevideo.domain.LocalVideoInfo;

public class MusicPlayService extends Service {

    private IMusicPlayService.Stub stub = new IMusicPlayService.Stub() {
        MusicPlayService service = MusicPlayService.this;
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
        }
        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }
        @Override
        public void start() throws RemoteException {
            service.start();
        }
        @Override
        public void pause() throws RemoteException {
            service.pause();
        }
        @Override
        public String getArtistName() throws RemoteException {
            return service.getArtistName();
        }
        @Override
        public String getAudioName() throws RemoteException {
            return service.getAudioName();
        }
        @Override
        public String getAudioPath() throws RemoteException {
            return service.getAudioPath();
        }
        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }
        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }
        @Override
        public void seekTo(int position) throws RemoteException {
            service.seekTo(position);
        }
        @Override
        public void next() throws RemoteException {
            service.next();
        }
        @Override
        public void pre() throws RemoteException {
            service.pre();
        }
        @Override
        public boolean isPlayer() throws RemoteException {
            return mediaPlayer.isPlaying();
        }
        @Override
        public int getPlayermode() throws RemoteException {
            return service.getPlayermode();
        }

        @Override
        public void setPlayermode(int playermode) throws RemoteException {
            service.setPlayermode(playermode);
        }


    };

    private ArrayList<LocalVideoInfo> lists;
    private MediaPlayer mediaPlayer;
    //记录那条信息
    private int position;
    //记录这条的全部信息
    private LocalVideoInfo videoInfo;
   // public  final static  String OPENCOMPLETE = "com.atguigu.OPENCOMPLETE";
    //通知管理
    private NotificationManager nm ;

    //正常模式 顺序播放
    public  final static int PLAYER_NORMAL = 1;
    //单曲模式循环
    public final static int PLAYER_SINGLE = 2;
    //全部循环
    public final static int PLAYER_ALL = 3;
    //播放模式
    private int playermode =PLAYER_NORMAL;
    private boolean isCompletion = false;

    private SharedPreferences sp ;

    public int getPlayermode() {
        return playermode;
    }

    public void setPlayermode(int playermode) {
        this.playermode = playermode;
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("playermode",playermode);
        edit.commit();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences("atguigu",MODE_PRIVATE);
        playermode = sp.getInt("playermode",getPlayermode());
        Log.e("TAG","MusicPlayService=====onCreate");
        getData();
    }
    private void getData() {
        new Thread() {
            public void run() {
                lists = new ArrayList<>();
                ContentResolver resolver = getContentResolver();
                //得到外部视频存储的Uri
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//                Uri uri = MediaStore.Video.Media.INTERNAL_CONTENT_URI;
                //得到相应列的数据
                String[] pros = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//视频姓名
                        MediaStore.Audio.Media.DURATION,//时长
                        MediaStore.Audio.Media.SIZE,//大小
                        MediaStore.Audio.Media.DATA,//地址
                        MediaStore.Audio.Media.ARTIST //地址
                };
                Cursor cursor = resolver.query(uri, pros, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        lists.add(new LocalVideoInfo(name, duration, size, data,artist));
                    }
                }
                cursor.close();
            }
        }.start();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }
    /**
     * +    * 根据位置播放一个音频
     * +     * @param position
     */
    private void openAudio(int position) {
        this.position = position;
        if(lists != null && lists.size()>0) {
            if (position < lists.size()) {
                videoInfo = lists.get(position);
                if (mediaPlayer != null) {
                    mediaPlayer.reset();
                    mediaPlayer = null;
                }
                try {
                    mediaPlayer = new MediaPlayer();
                    //设置播放地址
                    mediaPlayer.setDataSource(videoInfo.getData());
                    //实现三个监听：准备播放完成、播放出错、播放完成
                    mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                    mediaPlayer.setOnErrorListener(new MyOnErrorListener());
                    mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                    mediaPlayer.prepareAsync();
                    if(playermode == PLAYER_SINGLE){
                        isCompletion = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        //准备好
        @Override
        public void onPrepared(MediaPlayer mp) {
            //当Activity要获取视频信息的时候需要视频准备好的时候，所以要在这里发送广播
            //notifyChangs(OPENCOMPLETE);
            EventBus.getDefault().post(videoInfo);
            start();
        }
    }

//    private void notifyChangs(String opencomplete) {
//        Intent intent = new Intent(opencomplete);
//        sendBroadcast(intent);
//    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener{
        //播放出错
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            next();
            return true;
        }
    }
    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener{
        //播放完成
        @Override
        public void onCompletion(MediaPlayer mp) {
            isCompletion = true;
            next();
        }
    }
    /**
     * 播放音频
     */
    private void start() {
        mediaPlayer.start();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra("from_notification",true);
        PendingIntent pi = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notifyaction = new Notification.Builder(this)
                .setSmallIcon(R.drawable.notification_music_playing)
                .setContentTitle("321音乐")
                .setContentText("正在播放"+videoInfo.getName())
                .setContentIntent(pi)
                .build();

        nm.notify(1,notifyaction);
    }
    /**
     * 暂停音频
     */
    private void pause() {
        mediaPlayer.pause();
        //停止时 通知取消
        nm.cancel(1);
    }
    /**
     * +     * 得到演唱者
     * +     * @return
     */
    private String getArtistName() {
        return videoInfo.getArtist();

    }
    /**
     * +    * 得到歌曲名
     * +     * @return
     */
    private String getAudioName() {
        return videoInfo.getName();

    }
    /**
     * +     * 得到歌曲路径
     * +     * @return
     */
    private String getAudioPath() {
        return videoInfo.getData();
    }
    /**
     * +     * 得到总时长
     * +     * @return
     */
    private int getDuration() {
        return (int) videoInfo.getDuration();
    }
    /**
     * +     * 得到当前播放进度
     * +     * @return
     */
    private int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();

    }
    /**
     * +     * 音频拖动
     * +     *
     * +     * @param position
     * +
     */
    private void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }
    /**
     * +     * 播放下一个
     * +
     */
    private void next() {
        //得到那种模式的当前位置
        setModePosition();
        //根据位置播放
        openModePosition();
    }

    private void openModePosition() {
        if(playermode == PLAYER_NORMAL){
            if(position > lists.size()-1){
                position = lists.size()-1;
            }
            openAudio(position);
        }else if(playermode == PLAYER_SINGLE){
            openAudio(position);
        }else if(playermode == PLAYER_ALL){
            openAudio(position);
        }
    }

    private void setModePosition() {
        if(playermode == PLAYER_NORMAL){
            position++;
        }else if(playermode == PLAYER_SINGLE){
            if(!isCompletion) {
                position++;
            }
        }else if(playermode == PLAYER_ALL){
            position++;
            if(position > lists.size()-1){
                position = 0 ;
            }
        }

    }

    /**
     * +     * 播放上一个
     * +
     */
    private void pre() {
        if(playermode == PLAYER_NORMAL){
            position--;
            if(position < 0){
                position = 0 ;
            }
            openAudio(position);
        }else if(playermode == PLAYER_SINGLE){
            position--;
            if(position < 0){
                position = 0 ;
            }
            openAudio(position);
        }else if(playermode == PLAYER_ALL){
            position--;
            if(position <0){
                position = lists.size()-1;
            }
            openAudio(position);
        }
    }

}
