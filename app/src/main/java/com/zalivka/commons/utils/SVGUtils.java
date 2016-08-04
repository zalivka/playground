package com.zalivka.commons.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import com.caverock.androidsvg.SVGParseException;

import java.io.IOException;

public class SVGUtils {

    public static Bitmap rasterSVG(int bmWidth, int bmHeight, String assetPath, int bgColor, float addScale) {
        com.caverock.androidsvg.SVG svg = null;
        try {
            svg = com.caverock.androidsvg.SVG.getFromAsset(StaticContextHolder.mCtx.getAssets(), assetPath);
        } catch (SVGParseException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        // Create a canvas to draw onto
        if (svg.getDocumentWidth() != -1) {
            int width = (int) svg.getDocumentWidth();
            int height = (int) svg.getDocumentHeight();
            float scale = addScale * Math.max(bmWidth / (float)width, bmHeight / (float)height);
            Bitmap newBM = Bitmap.createBitmap((int)(width * scale), (int)(height * scale), Bitmap.Config.ARGB_8888);
            newBM.eraseColor(bgColor);
            Canvas bmcanvas = new Canvas(newBM);
            svg.setDocumentViewBox(0,0, width / scale, height / scale);
            svg.renderToCanvas(bmcanvas);

            svg.getRootElement().getChildren();

            Log.d("rasterSVG", newBM.getWidth() + " " + newBM.getHeight());
            return newBM;
        }
        return null;
    }

    // TODO merge
    public static Bitmap rasterSVGFromString(int bmWidth, int bmHeight, String svgString, int bgColor, float addScale) {
        com.caverock.androidsvg.SVG svg = null;
        try {
            svg = com.caverock.androidsvg.SVG.getFromString(svgString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        // Create a canvas to draw onto
        if (svg.getDocumentWidth() != -1) {
            int width = (int) svg.getDocumentWidth();
            int height = (int) svg.getDocumentHeight();
            float scale = addScale * Math.max(bmWidth / (float)width, bmHeight / (float)height);
            Bitmap newBM = Bitmap.createBitmap((int)(width * scale), (int)(height * scale), Bitmap.Config.ARGB_8888);
            newBM.eraseColor(bgColor);
            Canvas bmcanvas = new Canvas(newBM);
            svg.setDocumentViewBox(0,0, width / scale, height / scale);
            svg.renderToCanvas(bmcanvas);

            svg.getRootElement().getChildren();

            Log.d("rasterSVG", newBM.getWidth() + " " + newBM.getHeight());
            return newBM;
        }
        return null;
    }
}
