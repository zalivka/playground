package com.zalivka.commons.utils;

import android.app.Activity;
import android.os.Bundle;

public class DevToolsActivity extends Activity {

    public static final String EXTRA_ST = "stacktrace";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DevTools.sendStacktrace(this, getIntent().getStringExtra(EXTRA_ST));
    }
}
