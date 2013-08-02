package net.multipi.gcr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: marat
 * Date: 02.08.13
 * Time: 11:49
 */
public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(GlassService.TAG, "handle action: "+intent.getAction());
        Intent i = new Intent(context, GlassService.class);
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            context.startService(i);
        }
        if (context.getResources().getString(R.string.load_rates_action).equals(intent.getAction())) {
            i.putExtra(GlassService.SERVICE_COMMAND, GlassService.COMMAND_RELOAD_RATES);
            context.startService(i);
        }
    }
}
