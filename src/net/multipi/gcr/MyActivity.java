package net.multipi.gcr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MyActivity extends Activity {

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(this, GlassService.class));
        finish();
    }
}
