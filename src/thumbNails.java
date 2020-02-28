

import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import javax.swing.*;
import java.awt.*;

public class thumbNails extends Example{

    public static WritableImage[] viewThumbnails(){
        dim = false;
        int thumbWidth = 90, thumbHeight = 90;
        side = 0;
        top = 0;
        front = 0;
        for (int i = 0; i < 256; i++) {

            WritableImage image = (imageResizing.bilinearInterpolation(Axis.sideAxis(), thumbWidth, thumbHeight));
            thumbNail[i] = image;
            side++;
        }
        for (int j = 256; j < 512; j++) {
            WritableImage image = (imageResizing.bilinearInterpolation(Axis.frontAxis(), thumbWidth, thumbHeight));
            thumbNail[j] = image;
            front++;
        }
        for (int k = 512; k < 625; k++) {
            WritableImage image = (imageResizing.bilinearInterpolation(Axis.topAxis(), thumbWidth, thumbHeight));
            thumbNail[k] = image;
            top++;
        }


        WritableImage imageSide = (imageResizing.bilinearInterpolation(MIP.MIPSide(), thumbWidth, thumbHeight));
        thumbNail[625] = imageSide;


        WritableImage imageFront = (imageResizing.bilinearInterpolation(MIP.MIPFront(), thumbWidth, thumbHeight));
        thumbNail[626] = imageFront;

        WritableImage imageTop = (imageResizing.bilinearInterpolation(MIP.MIPTop(), thumbWidth, thumbHeight));
        thumbNail[627] = imageTop;


        return thumbNail;
        }


    }
