package atguigu.com.mobilevideo.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import atguigu.com.mobilevideo.R;
import atguigu.com.mobilevideo.Utils.Utils;
import atguigu.com.mobilevideo.domain.NetAudioInfo;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * 作者：李银庆 on 2017/5/28 09:24
 */
public class VideoHoder extends BaseVideoHoder{

    Utils utils;
    TextView tvContext;
    JCVideoPlayerStandard jcvVideoplayer;
    TextView tvPlayNums;
    TextView tvVideoDuration;
    ImageView ivCommant;
    TextView tvCommantContext;


    public VideoHoder(View convertView) {
        super(convertView);
        //中间公共部分 -所有的都有
        tvContext = (TextView) convertView.findViewById(R.id.tv_context);
        utils = new Utils(context);
        tvPlayNums = (TextView) convertView.findViewById(R.id.tv_play_nums);
        tvVideoDuration = (TextView) convertView.findViewById(R.id.tv_video_duration);
        ivCommant = (ImageView) convertView.findViewById(R.id.iv_commant);
        tvCommantContext = (TextView) convertView.findViewById(R.id.tv_commant_context);
        jcvVideoplayer = (JCVideoPlayerStandard) convertView.findViewById(R.id.jcv_videoplayer);
    }
    public void setData(NetAudioInfo.ListBean mediaItem) {
        super.setData(mediaItem);

        //设置文本-所有的都有,只有广告没有哦
        tvContext.setText(mediaItem.getText() + "_" + mediaItem.getType());

        //视频特有的------------------------
        //第一个参数是视频播放地址，第二个参数是显示封面的地址，第三参数是标题
        boolean setUp = jcvVideoplayer.setUp(
                mediaItem.getVideo().getVideo().get(0), JCVideoPlayer.SCREEN_LAYOUT_LIST,
                "");
        //加载图片
        if (setUp) {
//                ImageLoader.getInstance().displayImage(mediaItem.getVideo().getThumbnail().get(0),
//                        jcvVideoplayer.thumbImageView);
            Glide.with(context).load(mediaItem.getVideo().getThumbnail().get(0)).into(jcvVideoplayer.thumbImageView);
        }
        tvPlayNums.setText(mediaItem.getVideo().getPlaycount() + "次播放");
        tvVideoDuration.setText(utils.stringForTime(mediaItem.getVideo().getDuration() * 1000) + "");

    }
}
