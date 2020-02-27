import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


public class invert extends Example {

    public static WritableImage invertImage() {
        WritableImage image = medical_image;

        PixelReader image_reader = image.getPixelReader();
        PixelWriter image_writer = image.getPixelWriter();
        int j, i, c;

        int xA, yA;



        if (dim) {
            xA = (int) image.getWidth();
            yA = 113;
        } else {
            xA = (int) image.getWidth();
            yA = (int) image.getHeight();
        }




        for (j = 0; j < yA; j++) {
            for (i = 0; i < xA; i++) {
                float col = (float) image_reader.getColor(i, j).getRed();
                col = 1 - col;
                for (c = 0; c < 3; c++) {
                    image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
                }

            }
        }
        return image;

    }

}
