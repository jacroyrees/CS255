import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import static java.lang.Integer.max;

public class MIP extends Example {
    public static  WritableImage MIPSide() {
        WritableImage image = new WritableImage((int)medical_image.getWidth(), (int)medical_image.getHeight());
        dim = true;
        PixelWriter image_writer = image.getPixelWriter();
        int i, j, k, c;
        short datum;
        float col;
        int dimension = cthead.length, h = (int) image.getHeight();

        for (j = 0; j < h; j++) {
            for (i = 0; i < dimension; i++) {
                short maximum = 0;
                for (k = 0; k < 255; k++) {
                    datum = cthead[i][j][k];
                    if (maximum < datum) {
                        maximum = datum;
                    }
                    col = (((float) maximum - (float) min) / ((float) (max - min)));
                    if (maximum == max(datum, maximum)) {
                        for (c = 0; c < 3; c++) {
                            image_writer.setColor(j, i, Color.color(col, col, col, 1.0));
                        }
                    }
                }
            }
        }
        return image;
    }

    public static WritableImage MIPTop() {
        WritableImage image = new WritableImage((int)medical_image.getWidth(), (int)medical_image.getHeight());
        dim = false;
        PixelWriter image_writer = image.getPixelWriter();
        int i, j, k, c;
        short datum;
        float col;
        int w = (int) image.getWidth(), h = (int) image.getHeight();

        for (j = 0; j < h; j++) {
            for (i = 0; i < w; i++) {
                short maximum = 0;
                for (k = 0; k < 112; k++) {
                    datum = cthead[k][j][i];
                    if (maximum < datum) {
                        maximum = datum;
                    }
                    col = (((float) maximum - (float) min) / ((float) (max - min)));
                    if (maximum == max(datum, maximum)) {
                        for (c = 0; c < 3; c++) {
                            image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
                        }
                    }
                }
            }
        }

        return image;
    }

    public static WritableImage MIPFront() {
        WritableImage image = new WritableImage((int)medical_image.getWidth(), (int)medical_image.getHeight());
        dim = true;
        PixelWriter image_writer = image.getPixelWriter();
        int i, j, k, c;
        short datum;
        float col;
        int dimension = cthead.length, h = (int) image.getHeight();

        for (j = 0; j < h; j++) {
            for (i = 0; i < dimension; i++) {
                short maximum = 0;
                for (k = 0; k < 255; k++) {
                    datum = cthead[i][k][j];
                    if (maximum < datum) {
                        maximum = datum;
                    }
                    col = (((float) maximum - (float) min) / ((float) (max - min)));
                    if (maximum == max(datum, maximum)) {
                        for (c = 0; c < 3; c++) {
                            image_writer.setColor(j, i, Color.color(col, col, col, 1.0));
                        }
                    }
                }
            }
        }
        return image;

    }

}
