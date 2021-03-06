package atguigu.com.mobilevideo.paper;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import atguigu.com.mobilevideo.R;
import atguigu.com.mobilevideo.activity.SystemVideoPlayer;
import atguigu.com.mobilevideo.adapter.LocalVideoAdapter;
import atguigu.com.mobilevideo.domain.LocalVideoInfo;
import atguigu.com.mobilevideo.fargment.BaseFragment;

/**
 * 作者：李银庆 on 2017/5/21 12:03
 */
public class LocalVideoPaper extends BaseFragment implements AdapterView.OnItemClickListener {

    // private TextView tv;
    private TextView tv_content;
    private View view;
    private ListView lv;
    private LocalVideoAdapter adapter;
    private ArrayList<LocalVideoInfo> lists;
    private final int POSITION = 1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case  POSITION:
                    if(lists != null && lists.size()>0){
                        tv_content.setVisibility(View.GONE);
                        //显示数据
                        adapter = new LocalVideoAdapter(context,lists,true);
                        Log.e("TAG",lists+"-----------------------------");
                        lv.setAdapter(adapter);
                    }else{
                        tv_content.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };
    @Override
    public View initView() {
//        tv = new TextView(context);
//        return tv;
        view = View.inflate(context, R.layout.local_video,null);
        lv = (ListView) view.findViewById(R.id.lv2);
        tv_content = (TextView) view.findViewById(R.id.tv_content2);

        //点击lv实现播放
        lv.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        //  tv.setText("本地音乐");
        //添加数据,得到内存卡里面的数据，数据大量需要开启子线程小心耗时操作，需要内容提供者
        getData();


    }

    private void getData() {
        new Thread(){
            public void run(){
                lists = new ArrayList<LocalVideoInfo>();
                ContentResolver resolver = context.getContentResolver();
                //得到外部视频存储的Uri

                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                Uri uri = MediaStore.Video.Media.INTERNAL_CONTENT_URI;
                //得到相应列的数据
                String[] pros ={
                        MediaStore.Video.Media.DISPLAY_NAME,//视频姓名
                        MediaStore.Video.Media.DURATION,//时长
                        MediaStore.Video.Media.SIZE,//大小
                        MediaStore.Video.Media.DATA//地址
                };
                Cursor cursor = resolver.query(uri, pros, null, null, null);
                if(cursor != null){
                    while (cursor.moveToNext()){
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        long duration = cursor.getLong(cursor.getColumnIndex( MediaStore.Video.Media.DURATION));
                        long size = cursor.getLong(cursor.getColumnIndex( MediaStore.Video.Media.SIZE));
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        Log.e("TAG","name-----"+name +"duration----"+duration+"size----"+size+"data---"+data);
                        lists.add(new LocalVideoInfo(name,duration,size,data));
                    }
                    cursor.close();
                }
                handler.sendEmptyMessage(POSITION);
            }
        }.start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//        //调用系统的播放器
//        Intent intent = new Intent();
//        String data= adapter.getItem(position).getData();
//        intent.setDataAndType(Uri.parse(data),"video/*");
//        startActivity(intent);
        //自定义播放器
        Intent intent = new Intent(context,SystemVideoPlayer.class);
//        String data= adapter.getItem(position).getData();
//        intent.setDataAndType(Uri.parse(data),"video/*");
       // LocalVideoInfo item = adapter.getItem(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Infos", lists);
        intent.putExtras(bundle);
        intent.putExtra("position",position);
        startActivity(intent);
    }
}
