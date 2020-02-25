import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Axis extends Example{

    public static WritableImage topAxis() {
        dim = false;
        WritableImage image = medical_image;
        //Get image dimensions, and declare loop variables
        int w = (int) image.getWidth(), h = (int) image.getHeight(), i, j, c, k;
        PixelWriter image_writer = image.getPixelWriter();

        float col;
        short datum;
        //Shows how to loop through each pixel and colour
        //Try to always use j for loops in y, and i for loops in x
        //as this makes the code more readable
        for (j = 0; j < h; j++) {
            for (i = 0; i < w; i++) {
                //at this point (i,j) is a single pixel in the image
                //here you would need to do something to (i,j) if the image size
                //does not match the slice size (e.g. during an image resizing operation
                //If you don't do this, your j,i could be outside the array bounds
                //In the framework, the image is 256x256 and the data set slices are 256x256
                //so I don't do anything - this also leaves you something to do for the assignment
                datum = cthead[top][j][i]; //get values from slice 76 (change this in your assignment)
                //calculate the colour by performing a mapping from [min,max] -> [0,255]
                col = (((float) datum - (float) min) / ((float) (max - min)));
                for (c = 0; c < 3; c++) {
                    //and now we are looping through the bgr components of the pixel
                    //set the colour component c of pixel (i,j)
                    image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
                    //					data[c+3*i+3*j*w]=(byte) col;
                } // colour loop
            } // column loop
        } // row loop
        return image;
    }


    public static WritableImage frontAxis() {
        dim = true;
        WritableImage image = medical_image;
        //Get image dimensions, and declare loop variables
        int dimension = (int) cthead.length, h = (int) image.getHeight(), i, j, c, k;
        PixelWriter image_writer = image.getPixelWriter();

        float col;
        short datum;
        //Shows how to loop through each pixel and colour
        //Try to always use j for loops in y, and i for loops in x
        //as this makes the code more readable
        for (j = 0; j < h; j++) {
            for (i = 0; i < dimension; i++) {
                //at this point (i,j) is a single pixel in the image
                //here you would need to do something to (i,j) if the image size
                //does not match the slice size (e.g. during an image resizing operation
                //If you don't do this, your j,i could be outside the array bounds
                //In the framework, the image is 256x256 and the data set slices are 256x256
                //so I don't do anything - this also leaves you something to do for the assignment
                datum = cthead[i][front][j]; //get values from slice 76 (change this in your assignment)
                //calculate the colour by performing a mapping from [min,max] -> [0,255]
                col = (((float) datum - (float) min) / ((float) (max - min)));
                for (c = 0; c < 3; c++) {
                    //and now we are looping through the bgr components of the pixel
                    //set the colour component c of pixel (i,j)
                    image_writer.setColor(j, i, Color.color(col, col, col, 1.0));
                    //					data[c+3*i+3*j*w]=(byte) col;
                } // colour loop
            } // column loop
        } // row loop
        return image;
    }




    public static WritableImage sideAxis() {
        dim = true;
        WritableImage image = medical_image;
        //Get image dimensions, and declare loop variables
        int dimension = (int) cthead.length, h = (int) image.getHeight(), i, j, c, k;
        PixelWriter image_writer = image.getPixelWriter();

        float col;
        short datum;
        //Shows how to loop through each pixel and colour
        //Try to always use j for loops in y, and i for loops in x
        //as this makes the code more readable
        for (j = 0; j < h; j++) {
            for (i = 0; i < dimension; i++) {
                //at this point (i,j) is a single pixel in the image
                //here you would need to do something to (i,j) if the image size
                //does not match the slice size (e.g. during an image resizing operation
                //If you don't do this, your j,i could be outside the array bounds
                //In the framework, the image is 256x256 and the data set slices are 256x256
                //so I don't do anything - this also leaves you something to do for the assignment
                datum = cthead[i][j][side]; //get values from slice 76 (change this in your assignment)
                //calculate the colour by performing a mapping from [min,max] -> [0,255]
                col = (((float) datum - (float) min) / ((float) (max - min)));
                for (c = 0; c < 3; c++) {
                    //and now we are looping through the bgr components of the pixel
                    //set the colour component c of pixel (i,j)
                    image_writer.setColor(j, i, Color.color(col, col, col, 1.0));
                    //					data[c+3*i+3*j*w]=(byte) col;
                } // colour loop
            } // column loop
        } // row loop
        return image;


    }




}
