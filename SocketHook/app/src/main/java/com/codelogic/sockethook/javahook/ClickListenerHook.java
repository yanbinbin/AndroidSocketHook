package com.codelogic.sockethook.javahook;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by YanBinbin on 2018/7/1
 * Desc: 简单的利用反射hook点击事件
 */
public class ClickListenerHook{
    private static class Holder {
        private static ClickListenerHook sInstance = new ClickListenerHook();
    }

    private ClickListenerHook() {}

    public static ClickListenerHook getInstance() {
        return Holder.sInstance;
    }

    public <S extends View>  void hookOnClickListener(@NonNull S target){
        try {
            // 得到View的ListenerInfo对象
            Method getListenerInfo = View.class.getDeclaredMethod("getListenerInfo");
            getListenerInfo.setAccessible(true);
            Object listenerInfo = getListenerInfo.invoke(target);

            // 得到原始的OnClickListener
            Class<?> listenerInfoClz = Class.forName("android.view.View$ListenerInfo");
            Field mOnClickListener = listenerInfoClz.getDeclaredField("mOnClickListener");
            mOnClickListener.setAccessible(true);
            View.OnClickListener originListener = (View.OnClickListener) mOnClickListener.get(listenerInfo);

            // 用自定的ClickListener代替体统原来的Listener
            HookedClickListener hookedClickListener = new HookedClickListener(originListener);
            mOnClickListener.set(listenerInfo, hookedClickListener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class HookedClickListener implements View.OnClickListener {
        private View.OnClickListener origin;

        public HookedClickListener(View.OnClickListener listener) {
            this.origin = listener;
        }

        @Override
        public void onClick(View v) {
            Log.w("bb", "Before hook...");
            if (origin != null) {
                origin.onClick(v);
            }
            Log.w("bb", "After hook...");
        }
    }
}
