package atguigu.com.mobilevideo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import atguigu.com.mobilevideo.R;
import atguigu.com.mobilevideo.Utils.JsonParser;
import atguigu.com.mobilevideo.adapter.SearchVideoAdapter;
import atguigu.com.mobilevideo.domain.SousuoInfo;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_sousuo;
    ImageView iv_voice;
    TextView tv_sousuo;
    ListView lv;
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    public static final String NET_SEARCH_URL = "http://hot.news.cntv.cn/index.php?controller=list&action=searchList&sort=date&n=20&wd=";
    private  List<SousuoInfo.ItemsBean> items;
    private SearchVideoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        et_sousuo = (EditText)findViewById(R.id.et_sousuo);
        iv_voice = (ImageView)findViewById(R.id.iv_voice);
        tv_sousuo = (TextView)findViewById(R.id.tv_sousuo);
        lv = (ListView)findViewById(R.id.lv);

        iv_voice.setOnClickListener(this);
        tv_sousuo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_voice :
            showVodioDigol();
                break;
            case R.id.tv_sousuo:
                toSearch();
                Toast.makeText(SearchActivity.this, et_sousuo.getText().toString().trim(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void toSearch() {
        String trim = et_sousuo.getText().toString().trim();
        if(!TextUtils.isEmpty(trim)){
            String uri = NET_SEARCH_URL + trim;
            getDataFromNet(uri);
        }else {
            Toast.makeText(SearchActivity.this, "请输入年要搜索的内容", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDataFromNet(String uri) {
         final RequestParams request = new RequestParams(uri);
        x.http().get(request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("li","请求成功=====result"+result);
                processDate(result);
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

    private void processDate(String result) {
        SousuoInfo sousuoInfo = new Gson().fromJson(result, SousuoInfo.class);
        items = sousuoInfo.getItems();
        if(items != null && items.size()>0){
            adapter = new SearchVideoAdapter(this,items);
            lv.setAdapter(adapter);
        }

    }

    private void showVodioDigol() {

//1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, new MyInitListener());
//2.设置accent、language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
//若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解
//结果
// mDialog.setParameter("asr_sch", "1");
// mDialog.setParameter("nlp_version", "2.0");
//3.设置回调接口
        mDialog.setListener(new MyRecognizerDialogListener());
//4.显示dialog，接收语音输入
        mDialog.show();
    }
    class MyRecognizerDialogListener implements RecognizerDialogListener{

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            printResult(recognizerResult);
        }

        @Override
        public void onError(SpeechError speechError) {

        }
    }
    class MyInitListener implements InitListener{

        @Override
        public void onInit(int i) {

        }
    }

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mIatResults.put(sn, text);
        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        String s = resultBuffer.toString();
        String replace = s.replace("。", "");
        et_sousuo.setText(replace);
        et_sousuo.setSelection(et_sousuo.length());
    }
}
