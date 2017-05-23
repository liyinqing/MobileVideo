package atguigu.com.mobilevideo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

import atguigu.com.mobilevideo.R;
import atguigu.com.mobilevideo.Utils.Utils;
import atguigu.com.mobilevideo.domain.MoveInfo;

/**
 * 作者：李银庆 on 2017/5/21 14:11
 */
public class NetVideoAdapter extends BaseAdapter {
    private final Context context;
    private final List<MoveInfo.TrailersBean> datas;
    private Utils utils;
    ImageOptions imageOptions;

    public NetVideoAdapter(Context context, List<MoveInfo.TrailersBean> lists) {
        this.context =context;
        this.datas = lists;
        utils = new Utils(context);
        imageOptions = new ImageOptions.Builder()
                .setIgnoreGif(false)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setFailureDrawableId(R.drawable.video_default)
                .setLoadingDrawableId(R.drawable.video_default)
                .build();
    }

    @Override
    public int getCount() {
        return datas== null? 0:datas.size();
    }

    @Override
    public MoveInfo.TrailersBean getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_net_video, null);
            viewHolder = new ViewHolder();

            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_duration = (TextView) convertView.findViewById(R.id.tv_content);
            viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MoveInfo.TrailersBean localVideoInfo = datas.get(position);
        viewHolder.tv_name.setText(localVideoInfo.getMovieName());
        //时长都是毫秒，需要转换 大小也要转换成兆 需要工具类
        viewHolder.tv_duration.setText(localVideoInfo.getVideoTitle());
        viewHolder.tv_size.setText(localVideoInfo.getVideoLength()+"秒");

        x.image().bind(viewHolder.iv_icon,localVideoInfo.getCoverImg(),imageOptions);

        return convertView;

    }
    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;
    }
}
