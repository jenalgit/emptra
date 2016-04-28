package Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import static Utils.GenericUtils.*;

public class ImageUtils {

    public static void storeBlurredImage(Context context, String filePath, int imageId, int radius) {
        final File pictureFile = new File(filePath);

        if (!pictureFile.exists()) {

            Log.i("Main:storeBlurredImage", "Start -> " + radius + " File " + pictureFile.getAbsolutePath());
            Bitmap blurredImage = CreateBlurredImage(context, imageId, radius);

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                blurredImage.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.close();
            } catch (FileNotFoundException e) {
                LogMe("storeBlurredImage", "File not found: " + e.getMessage(), 3);
            } catch (IOException e) {
                LogMe("storeBlurredImage",
                        "Error accessing file: " + e.getMessage(), 3);
            }
        }
    }


    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }

    private static Bitmap CreateBlurredImage(Context context, int imageId, int radius) {
        Log.i("Main:CreateBlurredImage", "Start : radius = " + radius);
        // Load a clean bitmap and work from that.
        Bitmap originalBitmap = BitmapFactory.decodeResource(context.getResources(), imageId);

        Bitmap tempBitmap = getResizedBitmap(originalBitmap, originalBitmap.getWidth() + 40, originalBitmap.getHeight() - 200);
        Bitmap blurredBitmap;
        //blurredBitmap = Bitmap.createBitmap (originalBitmap.getWidth(), originalBitmap.getHeight(), originalBitmap.getConfig());
        blurredBitmap = Bitmap.createBitmap(tempBitmap);

        RenderScript rs = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(rs, tempBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        Allocation output = Allocation.createTyped(rs, input.getType());
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setInput(input);
        if (radius <= 0 || radius > 25) radius = 15;
        script.setRadius(radius);
        script.forEach(output);
        output.copyTo(blurredBitmap);
        return blurredBitmap;
    }
}
