package atguigu.com.mobilevideo.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import atguigu.com.mobilevideo.R;
import atguigu.com.mobilevideo.domain.NetAudioInfo;

/**
 * 作者：李银庆 on 2017/5/28 10:08
 */
public class ImageHolder extends BaseVideoHoder{
    TextView tvContext;
    ImageView ivImageIcon;

    public ImageHolder(View convertView) {
        super(convertView);
        //中间公共部分 -所有的都有
        tvContext = (TextView) convertView.findViewById(R.id.tv_context);
        ivImageIcon = (ImageView) convertView.findViewById(R.id.iv_image_icon);

    }

    public void setData(NetAudioInfo.ListBean mediaItem) {
        super.setData(mediaItem);
        //设置文本-所有的都有
        tvContext.setText(mediaItem.getText() + "_" + mediaItem.getType());
        //图片特有的

        ivImageIcon.setImageResource(R.drawable.bg_item);
        if (mediaItem.getImage() != null && mediaItem.getImage() != null && mediaItem.getImage().getSmall() != null) {
            Glide.with(context).load(mediaItem.getImage().getDownload_url().get(0)).placeholder(R.drawable.bg_item).error(R.drawable.bg_item).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivImageIcon);
        }


    }

}
