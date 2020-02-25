import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class imageResizing extends Example{

    public static WritableImage NearestNeighbour(WritableImage image, int newWidth, int newHeight) {
        WritableImage newImage = new WritableImage(newWidth, newHeight);
        PixelWriter image_writer = newImage.getPixelWriter();
        PixelReader image_reader = image.getPixelReader();

        int j, i, c;
        float y, x;
        int xA, yA;

        if (dim) {
            xA = (int) image.getWidth();
            yA = 113;
        } else {
            xA = (int) image.getWidth();
            yA = (int) image.getHeight();
        }

        float xB = (float) newImage.getWidth();
        float yB = (float) newImage.getHeight();


        for (j = 0; j < yB; j++) {
            for (i = 0; i < xB; i++) {
                y = j * (yA / yB);
                x = i * (xA / xB);
                float col = (float) image_reader.getColor((int) x, (int) y).getRed();
                for (c = 0; c < 3; c++) {
                    image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
                }

            }
        }
        return newImage;

    }

    public static WritableImage bilinearInterpolation(WritableImage image, int newWidth, int newHeight) {
        WritableImage newImage = new WritableImage(newWidth, newHeight);
        PixelReader image_reader = image.getPixelReader();
        PixelWriter image_writer = newImage.getPixelWriter();


        int xA, yA;



            if (dim) {
                xA = (int) image.getWidth();
                yA =  113;
            } else {
                xA = (int) image.getWidth();
                yA = (int) image.getHeight();
        }
        float xB = (float) newImage.getWidth();
        float yB = (float) newImage.getHeight();

        int y, x;
        float ratioX = xA / xB, ratioY = yA / yB;
        float col;

        for (int j = 0; j < yB-1; j++) {
            for (int i = 0; i < xB-1; i++) {
                x =(int) Math.floor((ratioX * i));
                y = (int)Math.floor((ratioY * j));




                float pointA, pointB, pointC, pointD;

                    pointA = (float) image_reader.getColor( x, y).getRed();
                    pointB = (float) image_reader.getColor( x + 1,  y).getRed();
                    pointC = (float) image_reader.getColor(x,  y + 1).getRed();
                    pointD = (float) image_reader.getColor( x + 1,  y + 1).getRed();




                float differenceX = (ratioX * i) - x;
                float differenceY = ((ratioY * j) - y);


                col = (
                        pointA * (1 - differenceX) * (1 - differenceY) + pointB * (differenceX) * (1 - differenceY) +
                                pointC * (differenceY) * (1 - differenceX) + pointD * (differenceX * differenceY));
                //System.out.println(col);
                if (col < 0) {
                    col = 0;
                } else if (col > 1) {
                    col = 1;
                }
                //System.out.println(col);
                for (int c = 0; c < 3; c++) {
                    image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
                }

            }
        }


        return newImage;
    }
}
