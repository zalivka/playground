package com.zalivka.commons.utils;

import android.content.Intent;
import android.text.TextUtils;

public class Market {
	
	public final static MarketName sMarket = StaticContextHolder.mCtx.getPackageName().contains("underground") ? MarketName.AMAZON : MarketName.GPLAY;

    public enum MarketName {GPLAY, SAMSUNG, AMAZON, NO};

	public static Intent startBuyActivity(String what) {
		if (true || !PrefUtils.getBoolean(PrefUtils.DEBUG_PURCHASE_SCREEN, false)) {
			Intent intent = new Intent();
			intent.setClassName(StaticContextHolder.mCtx.getPackageName(), "checkout.app.PurchaseActivity3");
			return intent;
		} else {
			Intent intent = new Intent();
			intent.setClassName(StaticContextHolder.mCtx.getPackageName(), "ru.jecklandin.stickman.billing3.GridPurchaseActivity");
			if (!TextUtils.isEmpty(what) && what.contains(":")) { // item
				intent.putExtra("extra_item", what);
			}
			return intent;
		}
	}

    public static boolean isGplay() {
		return sMarket == MarketName.GPLAY && !isPaid();
	}

	public static boolean isAmazon() {
		return sMarket == MarketName.AMAZON;
	}

    public static boolean isPaid() {
        return "com.zalivka.animation".equals(StaticContextHolder.mCtx.getPackageName());
    }



	public static boolean isNewVersion() {
		return "com.zalivka.animation2".equals(StaticContextHolder.mCtx.getPackageName());
	}

}
