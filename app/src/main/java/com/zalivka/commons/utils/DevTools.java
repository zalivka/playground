package com.zalivka.commons.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import com.zalivka.commons.R;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DevTools {

    public static void sendStacktrace(Activity ctx, Throwable e) {
        sendStacktrace(ctx, toString(e));
    }

    public static void sendStacktrace(final Activity ctx, final String error) {
        AlertDialog.Builder b = new AlertDialog.Builder(ctx);
        b.setTitle(R.string.error);
        b.setMessage(R.string.send_error_report);
        b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "zalivkamobile+dc2stack@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_TEXT,  collectInfo() + "\n\n" + error);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App error");
                ctx.startActivity(Intent.createChooser(emailIntent, "Send report"));
            }
        });
        b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        b.create().show();
    }

    public static void sendStacktrace(Service ctx, Throwable e) {
        Intent intent = new Intent().setClass(ctx, DevToolsActivity.class);
        intent.putExtra(DevToolsActivity.EXTRA_ST, toString(e));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    public static void sendStacktrace(Throwable e) {
        Intent intent = new Intent();
        intent.setClassName(StaticContextHolder.mCtx.getPackageName(), "ru.jecklandin.stickman.utils.DevToolsActivity");
        intent.putExtra(DevToolsActivity.EXTRA_ST, toString(e));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        StaticContextHolder.mCtx.startActivity(intent);
    }

    private static String toString(Throwable e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    private static String collectInfo() {
        String mediaState = Environment.getExternalStorageState() + "\n";

        return mediaState;
    }
}
