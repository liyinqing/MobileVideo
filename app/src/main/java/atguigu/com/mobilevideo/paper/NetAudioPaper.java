package atguigu.com.mobilevideo.paper;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import atguigu.com.mobilevideo.R;
import atguigu.com.mobilevideo.activity.ShowImageAndGifActivity;
import atguigu.com.mobilevideo.adapter.NetAdapter;
import atguigu.com.mobilevideo.domain.NetAudioInfo;
import atguigu.com.mobilevideo.fargment.BaseFragment;
import butterknife.ButterKnife;
import butterknife.InjectView;
/**
 * 作者：李银庆 on 2017/5/21 12:03
 */
public class NetAudioPaper extends BaseFragment implements AdapterView.OnItemClickListener {
    private final static String NETPPATH ="http://s.budejie.com/topic/list/jingxuan/1/budejie-android-6.2.8/0-20.json?market=baidu&udid=863425026599592&appname=baisibudejie&os=4.2.2&client=android&visiting=&mac=98%3A6c%3Af5%3A4b%3A72%3A6d&ver=6.2.8";
    List<NetAudioInfo.ListBean> list;

    @InjectView(R.id.lv)
    ListView lv;
    @InjectView(R.id.tv_content)
    TextView tvContent;
    @InjectView(R.id.progressbar)
    ProgressBar progressBar;
    NetAdapter adapter;

    @Override
    public View initView() {
        View view = View.inflate(getContext(), R.layout.net_audio, null);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        setData();
        lv.setOnItemClickListener(this);
    }

    private void setData() {
        RequestParams request = new RequestParams(NETPPATH);
        x.http().get(request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if(!TextUtils.isEmpty(result)) {
                    progressBar.setVisibility(View.GONE);
                    processDate(result);
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

//    private List<NetAudioBean.ListBean>  parsedJson(String json) {
//        NetAudioBean netAudioBean = new Gson().fromJson(json,NetAudioBean.class);
//        return netAudioBean.getList();
//    }
    private void processDate(String json) {
        NetAudioInfo netAudioInfo = new Gson().fromJson(json, NetAudioInfo.class);
        list = netAudioInfo.getList();
        if(list != null && list.size()>0) {
            tvContent.setVisibility(View.GONE);
            adapter = new NetAdapter(getContext(), list);
            lv.setAdapter(adapter);
        }else {
            tvContent.setVisibility(View.VISIBLE);
        }


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NetAudioInfo.ListBean listEntity = list.get(position);
        if(listEntity !=null ){
            //3.传递视频列表
            Intent intent = new Intent(context,ShowImageAndGifActivity.class);
            if(listEntity.getType().equals("gif")){
                String url = listEntity.getGif().getImages().get(0);
                intent.putExtra("url",url);
                context.startActivity(intent);
            }else if(listEntity.getType().equals("image")){
                String url = listEntity.getImage().getBig().get(0);
                intent.putExtra("url",url);
                context.startActivity(intent);
            }
        }

    }
}
