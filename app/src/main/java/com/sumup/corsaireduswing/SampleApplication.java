package com.sumup.corsaireduswing;

import android.app.Application;
import com.sumup.merchant.reader.api.SumUpState;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SumUpState.init(this);
    }
}
