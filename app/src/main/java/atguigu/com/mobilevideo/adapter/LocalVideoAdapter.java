package atguigu.com.mobilevideo.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import atguigu.com.mobilevideo.R;
import atguigu.com.mobilevideo.Utils.Utils;
import atguigu.com.mobilevideo.domain.LocalVideoInfo;

/**
 * 作者：李银庆 on 2017/5/21 14:11
 */
public class LocalVideoAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<LocalVideoInfo> datas;
    private final boolean isVideo;
    private Utils utils;

    public LocalVideoAdapter(Context context, ArrayList<LocalVideoInfo> lists,boolean b) {
        this.context =context;
        this.datas = lists;
        this.isVideo = b;
        utils = new Utils(context);
    }



    @Override
    public int getCount() {
        return datas== null? 0:datas.size();
    }

    @Override
    public LocalVideoInfo getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView== null){
            convertView = View.inflate(context, R.layout.item_video,null);
            viewHolder = new ViewHolder();
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
            viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            convertView.setTag(viewHolder);
        }else{
           viewHolder = (ViewHolder) convertView.getTag();
        }
        LocalVideoInfo localVideoInfo = datas.get(position);
        viewHolder.tv_name.setText(localVideoInfo.getName());
        //时长都是毫秒，需要转换 大小也要转换成兆 需要工具类
        viewHolder.tv_duration.setText(utils.stringForTime((int) localVideoInfo.getDuration()));
        viewHolder.tv_size.setText(Formatter.formatFileSize(context,localVideoInfo.getSize()));
        if(!isVideo){
            viewHolder.iv_icon.setImageResource(R.drawable.music_default_bg);
        }

        return convertView;
    }
    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;
    }
}
