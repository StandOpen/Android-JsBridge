package com.standopen.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {


    private WebView myweb;
    private JsBridgeUtil jsBridgeUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*

        TextView hello = (TextView)findViewById(R.id.hello);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        int width = outSize.x;
        float dp = px2dip(this,width);

        //float preWidth = (dp-30)

        ViewGroup.LayoutParams params = hello.getLayoutParams();
        int textWidth = params.width;
        int textHeight = textWidth/2;
        params.height = textHeight;
        hello.setLayoutParams(params);


       // Log.i("standopen_log",dp+"|"+helloWid);

       */

        myweb = (WebView)findViewById(R.id.web);
        WebSettings webSettings = myweb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setSupportMultipleWindows(true);
        //加载内容不超过屏幕宽度
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        myweb.setWebChromeClient(new MyWebChromClient());


        jsBridgeUtil = new JsBridgeUtil(MainActivity.this,"zhqd");
        try
        {
            jsBridgeUtil.addNative(JsImageAction.class);
        }
        catch (Exception e)
        {
            //
        }

        myweb.loadUrl("file:///android_asset/demo.html");
        /*
        try
        {
            init();
        }
        catch (Exception e)
        {
            System.out.println(e.getLocalizedMessage());
        }
        */
    }


    protected void init() throws Exception
    {


        JSONObject obj = new JSONObject();
        obj.put("key","value");

        String url = "zhqd://toast:1001?json="+obj.toString();


    }


    class  MyWebChromClient extends WebChromeClient
    {
        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            try{
                jsBridgeUtil.execute(message,url,view);
            }
            catch (Exception e)
            {
                //
            }
            result.cancel();
            return true;
        }
    }




    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static float px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxValue/scale + 0.5f);
    }
}
