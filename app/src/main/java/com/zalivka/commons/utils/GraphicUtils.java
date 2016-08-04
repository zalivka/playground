package com.zalivka.commons.utils;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;

public class GraphicUtils {

    public static ColorMatrixColorFilter sPaleFilter;

    public static ColorMatrixColorFilter sPlainGrayFilter;

    static {

        // Pale filter
        float sharpness = 0.2f;
        float[] light1 = {
                sharpness, 0, 0, 0, 0,
                0, sharpness, 0, 0, 0,
                0, 0, sharpness, 0, 0,
                0, 0, 0, 1f, 0};

        float add = 255 * (1 - sharpness);
        float[] light2 = {
                1, 0, 0, 0, add,
                0, 1, 0, 0, add,
                0, 0, 1, 0, add,
                0, 0, 0, 1, 0};

        ColorMatrix lightenMatr = new ColorMatrix();
        lightenMatr.setSaturation(0);
        lightenMatr.postConcat(new ColorMatrix(light1));
        lightenMatr.postConcat(new ColorMatrix(light2));
        sPaleFilter = new ColorMatrixColorFilter(lightenMatr);

        float[] gray = {
                0, 0, 0, 0, 200,
                0, 0, 0, 0, 200,
                0, 0, 0, 0, 200,
                0, 0, 0, 1f, 0};
        sPlainGrayFilter = new ColorMatrixColorFilter(new ColorMatrix(gray));
    }
}
