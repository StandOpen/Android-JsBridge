package com.standopen.main;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONObject;

/**
 * Created by StandOpen on 2016/11/25.
 * Author StandOpen
 * Email  standopen@foxmail.com
 * Description:
 */

public class JsImageAction {

    public static void toast(Context context, JSONObject obj, JsBridgeUtil.JsCallBack callBack)
    {
        try {
            Toast.makeText(context,obj.getString("key"),Toast.LENGTH_LONG).show();
            callBack.callback("这是来自原生代码的回调内容，是json 形式的");
        }
        catch (Exception e)
        {
            //
        }
    }

}
