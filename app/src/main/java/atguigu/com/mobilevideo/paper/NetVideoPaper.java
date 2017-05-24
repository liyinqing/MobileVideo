package atguigu.com.mobilevideo.paper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import atguigu.com.mobilevideo.R;
import atguigu.com.mobilevideo.activity.SystemVideoPlayer;
import atguigu.com.mobilevideo.adapter.NetVideoAdapter;
import atguigu.com.mobilevideo.domain.LocalVideoInfo;
import atguigu.com.mobilevideo.fargment.BaseFragment;


/**
 * 作者：李银庆 on 2017/5/21 12:03
 */
public class NetVideoPaper extends BaseFragment {
    private TextView tv_content;
    private ListView lv;
    private NetVideoAdapter adapter;
    private ArrayList<LocalVideoInfo> videoInfos;
    private MaterialRefreshLayout materialRefreshLayout;
    private SharedPreferences sp;
    private String uri = "http://api.m.mtime.cn/PageSubArea/TrailerList.api";

    private boolean isLogeMove = false;
    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.net_video,null);
        lv = (ListView) view.findViewById(R.id.lv1);
        tv_content = (TextView) view.findViewById(R.id.tv_content1);
        //得到SharedPreferences
        sp = context.getSharedPreferences("atguigu", Context.MODE_PRIVATE);

        materialRefreshLayout = (MaterialRefreshLayout) view.findViewById(R.id.refresh);
        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            //上啦
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                isLogeMove =false;
                getData();
            }
            //加载更多下拉
            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                isLogeMove =true;
                getData();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //MoveInfo.TrailersBean trailersBean = datas.get(position);
                Intent intent = new Intent(context, SystemVideoPlayer.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Infos",videoInfos);
                intent.putExtra("position",position);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        //请求网络之前加载数据
        String json = sp.getString(uri, "");
        processData(json);
        getData();
    }

    public void getData() {
//        RequestParams request= new RequestParams("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
        RequestParams request= new RequestParams(uri);
        x.http().get(request, new Callback.CommonCallback<String>() {
            //连接成功
            @Override
            public void onSuccess(String result) {
                SharedPreferences.Editor edit = sp.edit();
                edit.putString(uri,result);
                edit.commit();
                Log.e("TAG","result--------"+result);
                tv_content.setVisibility(View.GONE);
                processData(result);
                materialRefreshLayout.finishRefresh();
                materialRefreshLayout.finishRefreshLoadMore();
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                materialRefreshLayout.finishRefresh();
                materialRefreshLayout.finishRefreshLoadMore();
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
       if(!isLogeMove){
           videoInfos = new ArrayList<>();
           try {
               JSONObject jsonObject = new JSONObject(result);
               JSONArray trailers = jsonObject.getJSONArray("trailers");
               for(int i = 0; i < trailers.length(); i++) {
                   JSONObject jsonObject1 = trailers.getJSONObject(i);
                   String movieName = jsonObject1.getString("movieName");
                   String url = jsonObject1.getString("url");
                   String videoTitle = jsonObject1.getString("videoTitle");
                   int videoLength = jsonObject1.getInt("videoLength");
                   String coverImg = jsonObject1.getString("coverImg");
                   videoInfos.add(new LocalVideoInfo(movieName,videoTitle,videoLength,url,coverImg));
               }
               if(videoInfos != null && videoInfos.size()>0){
                   adapter = new NetVideoAdapter(context,videoInfos);
                   lv.setAdapter(adapter);
                   tv_content.setVisibility(View.GONE);
               }else{
                   tv_content.setVisibility(View.VISIBLE);
               }
           } catch (JSONException e) {
               e.printStackTrace();
           }
       }else {
           ArrayList<LocalVideoInfo> videoInfos1 = new ArrayList<>();
           try {
               JSONObject jsonObject = new JSONObject(result);
               JSONArray trailers = jsonObject.getJSONArray("trailers");
               for(int i = 0; i < trailers.length(); i++) {
                   JSONObject jsonObject1 = trailers.getJSONObject(i);
                   String movieName = jsonObject1.getString("movieName");
                   String url = jsonObject1.getString("url");
                   String videoTitle = jsonObject1.getString("videoTitle");
                   int videoLength = jsonObject1.getInt("videoLength");
                   String coverImg = jsonObject1.getString("coverImg");
                   videoInfos1.add(new LocalVideoInfo(movieName,videoTitle,videoLength,url,coverImg));
               }
               Log.e("TAG","videoInfos1111111111111============="+videoInfos);
               if(videoInfos1 != null && videoInfos1.size()>0) {

                   videoInfos.addAll(videoInfos1);
                   Log.e("TAG", "videoInfos22222222222222=============" + videoInfos);
                   adapter.notifyDataSetChanged();
               }
           } catch (Exception e) {
               e.printStackTrace();
           }
       }

    }
}
