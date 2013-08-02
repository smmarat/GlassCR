package net.multipi.gcr;

import android.content.ContentResolver;
import android.content.Context;
import com.google.glass.timeline.TimelineHelper;
import com.google.glass.timeline.TimelineProvider;
import com.google.glass.util.SettingsSecure;
import com.google.googlex.glass.common.proto.MenuItem;
import com.google.googlex.glass.common.proto.MenuValue;
import com.google.googlex.glass.common.proto.TimelineItem;

import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: marat
 * Date: 02.08.13
 * Time: 13:40
 */
public class TlCardHelper {

    public static String buildRatesHtml(List<Rate> rates) {
        StringBuilder sb = new StringBuilder();
        sb.append("<article><section><table class=\"align-justify\"><tbody>");
        for (Rate r : rates) {
            sb.append("<tr><td>").append(r.getCcy()).append("</td><td>").append(r.getBig()).append("</td><td>").append(r.getAsc()).append("</td></tr>");
        }
        sb.append("</tbody></table></section></article>");
        return sb.toString();
    }

    public static String buildMessageHtml(String message) {
        StringBuilder sb = new StringBuilder();
        sb.append("<article><section><div class=\"align-center\"><p class=\"text-x-small\">").append(message).append("</p></div></section></article>");
        return sb.toString();
    }

    public static TimelineItem buildRatesTlCard(Context ctx, String html, TimelineHelper tlHelper, ContentResolver cr) {
        return buildRatesTlCard(UUID.randomUUID().toString(), ctx, html, tlHelper, cr);
    }

    public static TimelineItem buildRatesTlCard(String id, Context ctx, String html, TimelineHelper tlHelper, ContentResolver cr) {
        MenuItem delOption = MenuItem.newBuilder().setAction(MenuItem.Action.DELETE).build();
        MenuItem customOption = MenuItem.newBuilder()
                .addValue(
                        MenuValue.newBuilder().
                                setDisplayName(ctx.getResources().getString(R.string.load_rates_label))
                                .build()
                )
                .setAction(MenuItem.Action.BROADCAST)
                .setBroadcastAction(ctx.getResources().getString(R.string.load_rates_action))
                .build();
        TimelineItem.Builder builder = tlHelper.createTimelineItemBuilder(ctx, new SettingsSecure(cr));
        return builder
                .setId(id)
                .setHtml(html)
                .setIsPinned(true)
                .addMenuItem(customOption)
                .addMenuItem(delOption).build();
    }

    public static void updateTlCard(final Context ctx, final TimelineItem item, final TimelineHelper tlHelper) {
        TimelineHelper.Update updater = new TimelineHelper.Update(){
            @Override
            public TimelineItem onExecute() {
                return tlHelper.updateTimelineItem(ctx, item, null, true, false);
            }
        };
        TimelineHelper.atomicUpdateTimelineItemAsync(updater);
    }


}
