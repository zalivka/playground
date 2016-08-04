package com.zalivka.commons.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.zalivka.commons.R;

public class CommonDialog {

    public static void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

	public static void askForInput(String defaultValue, final PromptDialogCallback callback,
			final Context ctx, String title, final boolean allowOverriding) {
		Builder builder = new Builder(ctx);
		builder.setTitle(title);

		LayoutInflater li = LayoutInflater.from(ctx);
		View view = li.inflate(R.layout.promptdialog, null);
		builder.setView(view);

		final EditText edit = (EditText) view.findViewById(R.id.prompt_edit);

		builder.setPositiveButton(android.R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String text = edit.getEditableText().toString();
				if (! FileUtils.isGoodFileName(text)) {
					Toast.makeText(ctx, ctx.getText(R.string.ill_filename), Toast.LENGTH_LONG).show();
				} else {
					callback.perform(text);
                    hideKeyboard(edit);
				}
			}
		}).setNegativeButton(android.R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		final AlertDialog ad = builder.create();
		edit.setText(defaultValue);
		edit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Button saveBtn = ad.getButton(DialogInterface.BUTTON_POSITIVE);
				TextView warning = (TextView)ad.findViewById(R.id.warning_save);

				if (TextUtils.isEmpty(s.toString())) {
					saveBtn.setEnabled(false);
					warning.setVisibility(View.GONE);
				} else if (! FileUtils.isGoodFileName(s.toString())) {
					saveBtn.setEnabled(false);
					warning.setVisibility(View.VISIBLE);
					warning.setText(R.string.illegal_symbols);
				} else if (callback.hasWarning(s.toString())) {
					saveBtn.setEnabled(allowOverriding);
					warning.setVisibility(View.VISIBLE);
					warning.setText(R.string.file_exists);
				} else {
					saveBtn.setEnabled(true);
					warning.setVisibility(View.GONE);
					saveBtn.setEnabled(true);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		ad.show();
	}

	public interface PromptDialogCallback {
		boolean hasWarning(String s);
		void perform(String s);
	}

	public static AlertDialog askForSure(Context ctx, int title, int msg, OnClickListener ok,
			OnClickListener cancel) {
		Builder b = new Builder(ctx);
		b.setTitle(title);
		b.setMessage(msg);
		b.setPositiveButton(android.R.string.ok, ok == null ? new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		} : ok);
		b.setNegativeButton(android.R.string.cancel, cancel == null ? new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		} :  cancel);
		return b.create();
	}

	public static boolean askClosingConfirmation(final Context ctx, final String tag) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
		if (prefs.getBoolean(tag, false)) {
			return false;
		}
		Builder b = new Builder(ctx);
		b.setMessage(R.string.closing);

		CheckBox never = new CheckBox(ctx);
		never.setText(R.string.never_ask);
		never.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor ed = prefs.edit();
				ed.putBoolean(tag, isChecked);
				ed.commit();
			}
		});
		b.setView(never);
		b.setNegativeButton(R.string.continue_exit, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		b.setPositiveButton(R.string.exit, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				((Activity)ctx).finish();
			}
		});

        Dialog dial = b.create();
        dial.setCanceledOnTouchOutside(false);
        dial.show();
		return true;
	}
	
}
