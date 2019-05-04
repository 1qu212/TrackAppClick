package cn.xydzjnq.trackappclick;

import android.app.Application;

import cn.xydzjnq.track.api.TrackAPI;

/**
 * Created by 王灼洲 on 2018/7/22
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        initTrackAPI(this);
    }

    /**
     * 初始化埋点 SDK
     *
     * @param application Application
     */
    private void initTrackAPI(Application application) {
        TrackAPI.init(application);
    }
}
