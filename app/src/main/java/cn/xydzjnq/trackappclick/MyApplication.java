package cn.xydzjnq.trackappclick;

import android.app.Application;

import cn.xydzjnq.track.api.TrackAPI;

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
