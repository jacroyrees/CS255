import java.io.FileInputStream;
import java.io.FileNotFoundException; 
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableListValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.image.*;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.*;
import java.nio.IntBuffer;

import static java.lang.Integer.max;


// OK this is not best practice - maybe you'd like to create
// a volume data class?
// I won't give extra marks for that though.

public class Example extends Application {
    short cthead[][][]; //store the 3D volume data set (z x y)
    short min, max; //min/max value in the 3D volume data set
    private int top = 0; //Global variables to take in the value of the slice
    private int side = 0;
    private int front = 0;
    int width = 256;
    int height = 256;


    WritableImage medical_image = new WritableImage(width, height);

    ImageView displayImg = new ImageView(medical_image);


    @Override
    public void start(Stage stage) throws FileNotFoundException, IOException {
        stage.setTitle("CThead Viewer");
        GridPane root = new GridPane();

        ReadData();



        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Bilinear Interpolation",
                        "Nearest Neighbour"
                );
        ComboBox comboBox = new ComboBox(options);
        comboBox.setValue("Select Resizing Algorithm");

        Button resetBtn = new Button("Reset");
        Button nearestNeighbour = new Button("Nearest Neighbour");
        Button mipTopbutton = new Button("MIP Top");
        Button mipFrontbutton = new Button("MIP Front");
        Button mipSidebutton = new Button("MIP Side"); //an example button to switch to MIP mode
        //sliders to step through the slices (z and y directions) (remember 113 slices in z direction 0-112)
        Slider topSlider = new Slider(0, 112, 0);
        Slider sideSlider = new Slider(0, 255, 0);
        Slider frontSlider = new Slider(0, 255, 0);
        Slider widthSlider = new Slider(1, 500, displayImg.getImage().getWidth());
        Slider heightSlider = new Slider(1, 500, displayImg.getImage().getHeight());

        if (front != 0 || side != 0) {
            widthSlider.setValue(displayImg.getImage().getWidth());
            heightSlider.setValue(displayImg.getImage().getHeight() / 2);
        } else {
            widthSlider.setValue(displayImg.getImage().getWidth());
            heightSlider.setValue(displayImg.getImage().getHeight());
        }

        Label widthLbl = new Label("Width");
        Label heightLbl = new Label("Height");
        Label xLabel = new Label("Front: ");
        Label yLabel = new Label("Side: ");
        Label zLabel = new Label("Top: ");

        frontSlider.setOnMouseDragged(e -> {
            if (top != 0 || side != 0) { // set z and y back to 0
                clearImage(medical_image);
            }
            sideSlider.adjustValue(0);
            topSlider.adjustValue(0);
            displayImg.setImage(frontAxis(medical_image));
        });

        sideSlider.setOnMouseDragged(e -> {
            if (front != 0 || top != 0) {
                clearImage(medical_image);
            }

            frontSlider.adjustValue(0);
            topSlider.adjustValue(0);
            displayImg.setImage(sideAxis(medical_image));
        });


        topSlider.setOnMouseDragged(e -> {
            if (front != 0 || side != 0) {
                clearImage(medical_image);
            }

            frontSlider.adjustValue(0);
            sideSlider.adjustValue(0);
            displayImg.setImage(topAxis(medical_image));
        });

        widthSlider.setOnMouseDragged(e -> {

            if(comboBox.getValue().equals("Bilinear Interpolation")){
                displayImg.setImage(bilinearInterpolation(medical_image, (int) widthSlider.getValue(), (int) heightSlider.getValue()));
                System.out.println("Bilinear");
            }else{
                displayImg.setImage(NearestNeighbour(medical_image, (int) widthSlider.getValue(), (int) heightSlider.getValue()));
                System.out.println("Nearest");
            }


        });

        heightSlider.setOnMouseDragged(e -> {


            if(comboBox.getValue().equals("Bilinear Interpolation")){
                displayImg.setImage(bilinearInterpolation(medical_image, (int) widthSlider.getValue(), (int) heightSlider.getValue()));
                System.out.println("Bilinear");
            }else{
                displayImg.setImage(NearestNeighbour(medical_image, (int) widthSlider.getValue(), (int) heightSlider.getValue()));
                System.out.println("Nearest");
            }

        });

        mipSidebutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearImage(medical_image);
                displayImg.setImage(MIPSide(medical_image));
            }
        });

        mipFrontbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearImage(medical_image);
                displayImg.setImage(MIPFront(medical_image));
            }
        });

        mipTopbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearImage(medical_image);
                displayImg.setImage(MIPTop(medical_image));
            }
        });

        resetBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                displayImg.setImage((medical_image));
            }
        });

        nearestNeighbour.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                bilinearInterpolation(medical_image, 200, 200);

            }
        });

        frontSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number>
                                                observable, Number oldValue, Number newValue) {
                        front = newValue.intValue();
                        System.out.println(newValue.intValue());
                        //System.out.println("Min is: " + min + " Max is: " + max);
                    }
                });

        sideSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number>
                                                observable, Number oldValue, Number newValue) {
                        side = newValue.intValue();
                        System.out.println(newValue.intValue());
                        //System.out.println("Min is: " + min + " Max is: " + max);
                    }
                });

        topSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number>
                                                observable, Number oldValue, Number newValue) {
                        top = newValue.intValue();
                        System.out.println(newValue.intValue());
                        //System.out.println("Min is: " + min + " Max is: " + max);
                    }
                });


//https://examples.javacodegeeks.com/desktop-java/javafx/scene/image-scene/javafx-image-example/

        root.add(comboBox,2,5,1,1);
        root.add(widthLbl, 0, 5, 1, 1);
        root.add(heightLbl, 0, 6, 1, 1);
        root.add(displayImg, 1, 0, 1, 1);
        root.add(xLabel, 1, 1, 1, 1);
        root.add(yLabel, 2, 1, 1, 1);
        root.add(zLabel, 0, 1, 1, 1);
        root.add(widthSlider, 1, 5, 1, 1);
        root.add(heightSlider, 1, 6, 1, 1);
        root.add(resetBtn,2,6,1,1);
        root.add(frontSlider, 1, 2, 1, 1);
        root.add(sideSlider, 2, 2, 1, 1);
        root.add(topSlider, 0, 2, 1, 1);
        root.add(mipTopbutton, 0, 3, 1, 1);
        root.add(mipSidebutton, 2, 3, 1, 1);
        //root.add(nearestNeighbour, 2, 4, 1, 1);
        root.add(mipFrontbutton, 1, 3, 1, 1);
        root.setVgap(10);
        root.setHgap(10);


        Scene scene = new Scene(root, 800, 480);
        // scene.getStylesheets().add("styleSheet.css");
        stage.setScene(scene);
        stage.show();
    }


    private void clearImage(WritableImage image) { // clears the image from the screen
        int w = (int) image.getWidth(), h = (int) image.getHeight();
        image.getPixelWriter().setPixels(0, 0, w, h, PixelFormat.getIntArgbInstance(),
                new int[w * h], 0, w);
    }

    //Function to read in the cthead data set
    public void ReadData() throws IOException {
        //File name is hardcoded here - much nicer to have a dialog to select it and capture the size from the user
        File file = new File("src/CThead");
        //Read the data quickly via a buffer (in C++ you can just do a single fread - I couldn't find if there is an equivalent in Java)
        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

        int i, j, k; //loop through the 3D data set

        min = Short.MAX_VALUE;
        max = Short.MIN_VALUE; //set to extreme values
        short read; //value read in
        int b1, b2; //data is wrong Endian (check wikipedia) for Java so we need to swap the bytes around

        cthead = new short[113][256][256]; //allocate the memory - note this is fixed for this data set
        //loop through the data reading it in
        for (k = 0; k < 113; k++) {
            for (j = 0; j < 256; j++) {
                for (i = 0; i < 256; i++) {
                    //because the Endianess is wrong, it needs to be read byte at a time and swapped
                    b1 = ((int) in.readByte()) & 0xff; //the 0xff is because Java does not have unsigned types
                    b2 = ((int) in.readByte()) & 0xff; //the 0xff is because Java does not have unsigned types
                    read = (short) ((b2 << 8) | b1); //and swizzle the bytes around
                    if (read < min) min = read; //update the minimum
                    if (read > max) max = read; //update the maximum
                    cthead[k][j][i] = read; //put the short into memory (in C++ you can replace all this code with one fread)
                }
            }
        }
        System.out.println(min + " " + max); //diagnostic - for CThead this should be -1117, 2248
        //(i.e. there are 3366 levels of grey (we are trying to display on 256 levels of grey)
        //therefore histogram equalization would be a good thing
    }


    /*
	   This function shows how to carry out an operation on an image.
	   It obtains the dimensions of the image, and then loops through
	   the image carrying out the copying of a slice of data into the
	   image.
   */
    public WritableImage topAxis(WritableImage image) {
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

    public WritableImage sideAxis(WritableImage image) {
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

    public WritableImage frontAxis(WritableImage image) {
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

    public WritableImage MIPSide(WritableImage image) {
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

    public WritableImage MIPTop(WritableImage image) {
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

    public WritableImage MIPFront(WritableImage image) {
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

    public WritableImage NearestNeighbour(WritableImage image, int newWidth, int newHeight) {
        WritableImage newImage = new WritableImage(newWidth, newHeight);
        PixelWriter image_writer = newImage.getPixelWriter();
        PixelReader image_reader = image.getPixelReader();

        int j, i, c;
        float y, x;
        int xA, yA;

        if (front != 0 || side != 0) {
            xA = (int) image.getWidth();
            yA = (int) image.getHeight() / 2;
        } else {
            xA = (int) image.getWidth();
            yA = (int) image.getHeight();
        }

        float xB = (float) newImage.getWidth();
        float yB = (float) newImage.getHeight();


        for (j = 0; j < yB - 1; j++) {
            for (i = 0; i < xB - 1; i++) {
                y = j * ((yA) / yB);
                x = i * (xA / xB);
                float col = (float) image_reader.getColor((int) x, (int) y).getRed();
                for (c = 0; c < 3; c++) {
                    image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
                }

            }
        }
        return newImage;

    }

    public WritableImage bilinearInterpolation(WritableImage image, int newWidth, int newHeight) {
        WritableImage newImage = new WritableImage(newWidth, newHeight);
        PixelReader image_reader = image.getPixelReader();
        PixelWriter image_writer = newImage.getPixelWriter();


        float xA, yA;
        if (front != 0 || side != 0) {
            xA = (float) image.getWidth();
            yA = (float) image.getHeight() / 2;
        } else {
            xA = (float) image.getWidth();
            yA = (float) image.getHeight();
        }
        float xB = (float) newImage.getWidth();
        float yB = (float) newImage.getHeight();

        float y, x;
        float ratioX = xB / xA, ratioY = yB / yA;
        float col;

        for (int j = 0; j < yB-1; j++) {
            for (int i = 0; i < xB-1; i++) {
                y = j * ((yA) / yB);//y on original image
                x = i * (xA / xB); //x on original image

                //System.out.println(x);
                float pointA = (float) image_reader.getColor((int)x, (int)y).getRed();
                float pointB = (float) image_reader.getColor((int) x + 1, (int) y).getRed();
                float pointC = (float) image_reader.getColor((int) x, (int) y + 1).getRed();
                float pointD = (float) image_reader.getColor((int) x + 1, (int) y + 1).getRed();





                float nearX = (i / ratioX);
                float nearY = (j / ratioY);
                float differenceX = ((nearX * ratioX) - i);
                float differenceY = ((nearY * ratioY) - j);


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

    public WritableImage biCubicInterpolation(WritableImage image, int newWidth, int newHeight) {
        WritableImage newImage = new WritableImage(newWidth, newHeight);
        PixelReader image_reader = image.getPixelReader();
        PixelWriter image_writer = newImage.getPixelWriter();


        float xA, yA;
        if (front != 0 || side != 0) {
            xA = (float) image.getWidth();
            yA = (float) image.getHeight() / 2;
        } else {
            xA = (float) image.getWidth();
            yA = (float) image.getHeight();
        }
        float xB = (float) newImage.getWidth();
        float yB = (float) newImage.getHeight();

        float y, x;
        float ratioX = xB / xA, ratioY = yB / yA;
        float col;

        for (int j = 0; j < yB-1; j++) {
            for (int i = 0; i < xB-1; i++) {
                y = j * ((yA) / yB);//y on original image
                x = i * (xA / xB); //x on original image

                //System.out.println(x);
                float pointA = (float) image_reader.getColor((int)x, (int)y).getRed();
                float pointB = (float) image_reader.getColor((int) x + 1, (int) y).getRed();
                float pointC = (float) image_reader.getColor((int) x, (int) y + 1).getRed();
                float pointD = (float) image_reader.getColor((int) x + 1, (int) y + 1).getRed();
                float pointE, pointF, pointG, pointH;




                float nearX = (i / ratioX);
                float nearY = (j / ratioY);
                float differenceX = ((nearX * ratioX) - i);
                float differenceY = ((nearY * ratioY) - j);


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