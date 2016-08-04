package com.zalivka.commons.utils;

import com.google.gson.Gson;

public class Stuff {

    public interface IPurchases {
        boolean isUnlocked(String what);
    }

    public interface ISceneConnection {
        boolean isItemInUse(String itemName);
        void updateCustomItems();
    }

    public static IPurchases sPurchasesInterface;
    public static ISceneConnection sSceneInterface;

    public static Gson sGson = new Gson();

}
