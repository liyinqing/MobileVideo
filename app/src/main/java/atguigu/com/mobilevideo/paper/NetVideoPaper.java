package atguigu.com.mobilevideo.paper;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import atguigu.com.mobilevideo.R;
import atguigu.com.mobilevideo.activity.SystemVideoPlayer;
import atguigu.com.mobilevideo.adapter.NetVideoAdapter;
import atguigu.com.mobilevideo.domain.MoveInfo;
import atguigu.com.mobilevideo.fargment.BaseFragment;

/**
 * 作者：李银庆 on 2017/5/21 12:03
 */
public class NetVideoPaper extends BaseFragment {
    private TextView tv_content;
    private ListView lv;
    private List<MoveInfo.TrailersBean> datas;
    private NetVideoAdapter adapter;

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.net_video,null);
        lv = (ListView) view.findViewById(R.id.lv);
        tv_content = (TextView) view.findViewById(R.id.tv_content);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MoveInfo.TrailersBean trailersBean = datas.get(position);
                Intent intent = new Intent(context, SystemVideoPlayer.class);
                intent.setDataAndType(Uri.parse(trailersBean.getUrl()),"video/*");
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        getData();
    }

    public void getData() {
        RequestParams request= new RequestParams("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
        x.http().get(request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    private void processData(String result) {
        MoveInfo moveInfo = new Gson().fromJson(result, MoveInfo.class);
        datas = moveInfo.getTrailers();
        if(datas != null && datas.size()>0){
            tv_content.setVisibility(View.GONE);
            adapter = new NetVideoAdapter(context,datas);
            lv.setAdapter(adapter);
        }else{
            tv_content.setVisibility(View.VISIBLE);
        }


    }
}
