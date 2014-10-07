/*
 * Copyright 2014 Alexander Oprisnik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oprisnik.semdroid;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.oprisnik.semdroid.service.AnalysisIntentService;

public class NotificationHelper {

    public static void createAnalysisFinishedNotification(Context context, String packageName, String resultFile, int notificationId) {


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(context.getString(R.string.notification_analysis_finished_title));
        builder.setContentText(packageName);

        //workaround for bug: https://code.google.com/p/android/issues/detail?id=61850
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            getContentIntent(context, packageName, resultFile).cancel();
        }

        builder.setContentIntent(getContentIntent(context, packageName, resultFile))
                .setTicker(context.getString(R.string.notification_analysis_finished_ticker, packageName))
                .setSmallIcon(R.drawable.ic_launcher_flat)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationId, builder.build());
    }

    protected static PendingIntent getContentIntent(Context context, String packageName, String resultFile) {
        Intent i = new Intent(context, AnalysisResultsOverviewActivity.class);
        i.putExtra(AnalysisResultsOverviewFragment.KEY_PACKAGE_NAME, packageName);
        i.putExtra(AnalysisIntentService.KEY_RESULTS_KEY, resultFile);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, i, 0);
        return contentIntent;
    }
}
