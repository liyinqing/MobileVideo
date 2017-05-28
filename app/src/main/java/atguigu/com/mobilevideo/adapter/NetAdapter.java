package atguigu.com.mobilevideo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import atguigu.com.mobilevideo.R;
import atguigu.com.mobilevideo.domain.NetAudioInfo;
import atguigu.com.mobilevideo.holder.ADHolder;
import atguigu.com.mobilevideo.holder.GifHolder;
import atguigu.com.mobilevideo.holder.ImageHolder;
import atguigu.com.mobilevideo.holder.TextHolder;
import atguigu.com.mobilevideo.holder.VideoHoder;

/**
 * 作者：李银庆 on 2017/5/27 21:38
 */
public class NetAdapter extends BaseAdapter {

    private final Context context;
    private final List<NetAudioInfo.ListBean> datas;

    public NetAdapter(Context context, List<NetAudioInfo.ListBean> list) {
        this.context = context;
        this.datas = list;
    }
    /**
     * 视频
     */
    private static final int TYPE_VIDEO = 0;

    /**
     * 图片
     */
    private static final int TYPE_IMAGE = 1;

    /**
     * 文字
     */
    private static final int TYPE_TEXT = 2;

    /**
     * GIF图片
     */
    private static final int TYPE_GIF = 3;


    /**
     * 软件推广
     */
    private static final int TYPE_AD = 4;

    //返回总类型数据
    @Override
    public int getViewTypeCount() {
        return 5;
    }

    /**
     * 当前item是什么类型
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        int itemViewType = -1;
        //根据位置，从列表中得到一个数据对象
        NetAudioInfo.ListBean listBean = datas.get(position);
        String type = listBean.getType();//得到类型
        if ("video".equals(type)) {
            itemViewType = TYPE_VIDEO;
        } else if ("image".equals(type)) {
            itemViewType = TYPE_IMAGE;
        } else if ("text".equals(type)) {
            itemViewType = TYPE_TEXT;
        } else if ("gif".equals(type)) {
            itemViewType = TYPE_GIF;
        } else {
            itemViewType = TYPE_AD;//广播
        }
        return itemViewType;
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = initView(convertView, getItemViewType(position), datas.get(position));
        return convertView;
    }
    private View initView(View convertView, int itemViewType,NetAudioInfo.ListBean mediaItem) {
        switch (itemViewType) {
            case TYPE_VIDEO://视频

                VideoHoder videoHoder;
                if (convertView == null) {
                    convertView = View.inflate(context, R.layout.all_video_item, null);
                    videoHoder = new VideoHoder(convertView);
                    convertView.setTag(videoHoder);
                } else {
                    videoHoder = (VideoHoder) convertView.getTag();
                }

                //设置数据
                videoHoder.setData(mediaItem);

                break;
            case TYPE_IMAGE://图片
                ImageHolder imageHolder;
                if (convertView == null) {
                    convertView = View.inflate(context, R.layout.all_image_item, null);
                    imageHolder = new ImageHolder(convertView);
                    convertView.setTag(imageHolder);
                } else {
                    imageHolder = (ImageHolder) convertView.getTag();
                }
                //设置数据
                imageHolder.setData(mediaItem);
                break;
            case TYPE_TEXT://文字

                TextHolder textHolder;
                if (convertView == null) {
                    convertView = View.inflate(context, R.layout.all_text_item, null);
                    textHolder = new TextHolder(convertView);

                    convertView.setTag(textHolder);
                } else {
                    textHolder = (TextHolder) convertView.getTag();
                }
                textHolder.setData(mediaItem);

                break;
            case TYPE_GIF://gif

                GifHolder gifHolder;
                if (convertView == null) {
                    convertView = View.inflate(context, R.layout.all_gif_item, null);
                    gifHolder = new GifHolder(convertView);

                    convertView.setTag(gifHolder);
                } else {
                    gifHolder = (GifHolder) convertView.getTag();
                }

                gifHolder.setData(mediaItem);

                break;
            case TYPE_AD://软件广告

                ADHolder adHolder;
                if (convertView == null) {
                    convertView = View.inflate(context, R.layout.all_ad_item, null);
                    adHolder = new ADHolder(convertView);
                    convertView.setTag(adHolder);
                } else {
                    adHolder = (ADHolder) convertView.getTag();
                }

                break;
        }
        return convertView;
    }
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder viewHolder;
//        if(convertView == null){
//            convertView = View.inflate(context, R.layout.jpg_item,null);
//            viewHolder = new ViewHolder();
//            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
//           viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
//            viewHolder.tv_time = (TextView)convertView.findViewById(R.id.tv_time);
//            viewHolder.tv_tetle = (TextView)convertView.findViewById(R.id.tv_tetle);
//            viewHolder.tv_content = (TextView)convertView.findViewById(R.id.tv_content);
//            convertView.setTag(viewHolder);
//        }else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//        NetAudioInfo.ListBean listBean = datas.get(1);
//        List<NetAudioInfo.ListBean.TopCommentsBean> top_comments = listBean.getTop_comments();
//        NetAudioInfo.ListBean.TopCommentsBean topCommentsBean = top_comments.get(0);
//        viewHolder.tv_name.setText(topCommentsBean.getContent());
//        viewHolder.tv_time.setText(topCommentsBean.getPasstime());
//        return convertView;
//    }
//    class ViewHolder{
//        ImageView iv_icon;
//        TextView tv_name;
//        TextView tv_time;
//        TextView tv_tetle;
//        TextView tv_content;
//    }
}
