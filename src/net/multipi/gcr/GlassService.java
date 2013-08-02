package net.multipi.gcr;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.glass.location.GlassLocationManager;
import com.google.glass.timeline.TimelineHelper;
import com.google.glass.timeline.TimelineProvider;
import com.google.googlex.glass.common.proto.TimelineItem;

import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: marat
 * Date: 02.08.13
 * Time: 11:44
 */
public class GlassService extends Service {

    private static final String HOME_CARD = "home_card";
    public static final String SERVICE_COMMAND = "service_command";
    public static final String COMMAND_RELOAD_RATES = "command_reload_rates";
    public static final String TAG = "glass_rates";

    @Override
    public int onStartCommand(Intent intent, int flags, int startid){
        super.onStartCommand(intent, flags, startid);
        GlassLocationManager.init(this);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String homeCardId = preferences.getString(HOME_CARD, null);
        final TimelineHelper tlHelper = new TimelineHelper();
        final ContentResolver cr = getContentResolver();
        if (intent.hasExtra(SERVICE_COMMAND)) {
            // call commands
            if (COMMAND_RELOAD_RATES.equals(intent.getStringExtra(SERVICE_COMMAND))) {
                RatesHelper.refreshRates(new RatesHelper.RatesLoadCallback() {
                    @Override
                    public void onRatesLoad(List<Rate> rates) {
                        String id = preferences.getString(HOME_CARD, UUID.randomUUID().toString());
                        TimelineItem item = TlCardHelper.buildRatesTlCard(id, GlassService.this, TlCardHelper.buildRatesHtml(rates), tlHelper, cr);
                        TlCardHelper.updateTlCard(GlassService.this, item, tlHelper);
                        Log.v(TAG, "card updated (ok)");
                    }

                    @Override
                    public void onRatesFail(Exception e) {
                        String id = preferences.getString(HOME_CARD, UUID.randomUUID().toString());
                        TimelineItem item = TlCardHelper.buildRatesTlCard(id, GlassService.this, TlCardHelper.buildMessageHtml(getResources().getString(R.string.not_avaiable)), tlHelper, cr);
                        TlCardHelper.updateTlCard(GlassService.this, item, tlHelper);
                        Log.v(TAG, "card updated (fail)");
                    }
                });
                String id = preferences.getString(HOME_CARD, UUID.randomUUID().toString());
                TimelineItem item = TlCardHelper.buildRatesTlCard(id, GlassService.this, TlCardHelper.buildMessageHtml(getResources().getString(R.string.updating)), tlHelper, cr);
                cr.update(TimelineProvider.TIMELINE_URI, TimelineHelper.toContentValues(item), "_id=\""+item.getId()+"\"", new String[]{});
            }
        } else {
            // first start
            if(homeCardId != null){
                TimelineItem timelineItem = tlHelper.queryTimelineItem(cr, homeCardId);
                if (timelineItem!=null && !timelineItem.getIsDeleted()) {
                    Log.v(TAG, "old home deleted");
                    tlHelper.deleteTimelineItem(this, timelineItem);
                }
            }
            RatesHelper.refreshRates(new RatesHelper.RatesLoadCallback() {
                @Override
                public void onRatesLoad(List<Rate> rates) {
                    String id = preferences.getString(HOME_CARD, UUID.randomUUID().toString());
                    TimelineItem item = TlCardHelper.buildRatesTlCard(id, GlassService.this, TlCardHelper.buildRatesHtml(rates), tlHelper, cr);
                    TlCardHelper.updateTlCard(GlassService.this, item, tlHelper);
                    Log.v(TAG, "card updated (ok)");
                }

                @Override
                public void onRatesFail(Exception e) {
                    String id = preferences.getString(HOME_CARD, UUID.randomUUID().toString());
                    TimelineItem item = TlCardHelper.buildRatesTlCard(id, GlassService.this, TlCardHelper.buildMessageHtml(getResources().getString(R.string.not_avaiable)), tlHelper, cr);
                    TlCardHelper.updateTlCard(GlassService.this, item, tlHelper);
                    Log.v(TAG, "card updated (fail)");
                }
            });
            TimelineItem item = TlCardHelper.buildRatesTlCard(GlassService.this, TlCardHelper.buildMessageHtml(getResources().getString(R.string.updating)), tlHelper, cr);
            cr.insert(TimelineProvider.TIMELINE_URI, TimelineHelper.toContentValues(item));
            preferences.edit().putString(HOME_CARD, item.getId()).commit();
            Log.v(TAG, "new home inserted");
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
}
