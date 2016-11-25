package com.standopen.main;

import android.content.Context;
import android.net.Uri;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by StandOpen on 2016/11/25.
 * Author StandOpen
 * Email  standopen@foxmail.com
 * Description:原生与JS交互封装类
 * 具体函数定义方法请看最下方
 */

public class JsBridgeUtil {

    /**
     * 规则前缀，比如zhqd
     */
    private String protocol;

    /**
     * 保存原生函数
     */
    private HashMap<String, Class<?>> nativeFuns;

    private Context context;

    /**
     * 是否进行认证
     */
    private boolean isAuth = false;

    public JsBridgeUtil(Context ctx, String protocol) {
        this.nativeFuns = new HashMap<>();
        this.context = ctx;
        this.protocol = protocol;
    }

    public void  setIsAuth(boolean isAuth)
    {
        this.isAuth = isAuth;
    }


    /**
     * 添加供js调用的函数
     * @param classname
     * @throws functionDuplicateException
     */
    public void addNative(Class<?> classname) throws functionDuplicateException {
        Method[] methods = classname.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            String methodName = methods[i].getName();
            if (this.nativeFuns.containsKey(methodName)) {
                throw new functionDuplicateException(methodName + "函数已经存在");
            }
            this.nativeFuns.put(methodName, classname);
        }
    }

    /**
     * 执行解析
     * @param message
     * @param url
     * @param webView
     * @throws functionNotFoundException
     * @throws NoSuchMethodException
     * @throws JSONException
     * @throws authErrorException
     */
    public void execute(String message, String url, final WebView webView) throws functionNotFoundException, NoSuchMethodException,JSONException,authErrorException {

        System.out.println(message);

        //-- zhqd://method:port?json=

        if(!message.startsWith(this.protocol))
        {
            throw new authErrorException("protocol error");
        }

        //-- 是否进行认证
        if(this.isAuth)
        {
            //--doAuth
        }


        Uri uri = Uri.parse(message);
        String methodName = uri.getHost();
        final String jsCallback = uri.getPort()+"";
        String jsonData = uri.getQueryParameter("json");

        JSONObject obj = new JSONObject(jsonData);


        if (!this.nativeFuns.containsKey(methodName)) {
            throw new functionNotFoundException("该函数不存在，请先进行申明");
        }

        Class<?> classname = this.nativeFuns.get(methodName);
        Method method = classname.getMethod(methodName, Context.class, JSONObject.class, JsCallBack.class);
        try {
            method.invoke(classname.newInstance(), this.context, obj, new JsCallBack() {
                @Override
                public void callback(String msg) {
                    doCallback(webView, jsCallback, msg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 调用js回调函数
     * @param webView
     * @param jsCallback
     * @param msg
     */
    protected void doCallback(WebView webView, String jsCallback, String msg) {
        String url = "javascript:jsBridge.onback('" + jsCallback + "','" + msg + "')";
        webView.loadUrl(url);
    }

    /**
     * 函数执行回调
     */
    public interface JsCallBack {
        abstract public void callback(String msg);
    }

    /**
     * 函数重复异常
     */
    public class functionDuplicateException extends Exception {
        private static final long serialVersionUID = 1200343503299894691L;

        public functionDuplicateException(String message) {
            super(message);
        }
    }

    /**
     * 函数不存在异常
     */
    public class functionNotFoundException extends Exception {
        private static final long serialVersionUID = 2388331119251127805L;

        public functionNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * 认证异常
     */
    public class authErrorException extends Exception {

        private static final long serialVersionUID = 4324502386823730598L;

        public authErrorException(String message) {
            super(message);
        }
    }


    /**
     * 调用函数示例类，其它功能都需要按照这个格式进行定义
     * 最好定义成static类型的
     * 回调的json字符串必须包含,errcode|int,errmsg|string,便于js进行判断
     */
    private class ToastAction
    {
        public  void Toast(Context context,JSONObject obj,JsCallBack callBack)
        {
            try
            {
                JSONObject result = new JSONObject();
                result.put("errcode",0);
                result.put("errmsg","执行成功");
                String json = result.toString();
                callBack.callback(json);
            }
            catch (Exception e)
            {
                //
            }

        }
    }
}
