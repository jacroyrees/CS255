
import java.awt.*;

import java.io.FileInputStream;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;

import static java.lang.Integer.max;


// OK this is not best practice - maybe you'd like to create
// a volume data class?
// I won't give extra marks for that though.

public class Example extends Application {
    public static short cthead[][][]; //store the 3D volume data set (z x y)
    public static short min, max; //min/max value in the 3D volume data set
    public static int top = 0; //Global variables to take in the value of the slice
    public static int side = 0;
    public static int front = 0;
    public static int width = 256;
    public static int height = 256;
    public static boolean dim = false;
    public static boolean versionControl = false;

    public static WritableImage[] thumbNail = new WritableImage[629];

    public static WritableImage medical_image = new WritableImage(width, height);

    public static GridPane root2 = new GridPane();

    ImageView displayImg = new ImageView(medical_image);



    @Override
    public void start(Stage stage) throws IOException {

        Axis axis = new Axis();
        imageResizing imageResize = new imageResizing();
        thumbNails thumb = new thumbNails();

        ReadData();
        thumb.viewThumbnails();


        MIP mip = new MIP();
        Stage thumbNailStage = new Stage();

        GridPane root = new GridPane();

        root2.setGridLinesVisible(true);


// get the mouse's position


        GridPane thumbnailLarge = new GridPane();
        Button backBtn = new Button("Back to ThumbNails");
        Button backBtn2 = new Button("Back to Main");
        thumbnailLarge.add(backBtn,2,2,1,1);
        thumbnailLarge.add(backBtn2,3,2,1,1);
        ScrollPane sc = new ScrollPane(root2);
        stage.setX(0);
        stage.setY(0);

        Scene scene1 = new Scene(root, 1440, 1550);
        Scene scene2 = new Scene(sc, 1440, 1550);
        Scene scene3 = new Scene(thumbnailLarge,1440,1550);
        stage.setResizable(false);
        stage.setTitle("CThead Viewer");





        root2.setOnMouseClicked(e ->{

            //System.out.println(e.getX());
            int cX = (int)Math.floor(e.getX() / 90);
            int cY = (int)Math.floor(e.getY() / 90);
            System.out.println(cY);
            System.out.println(cX);

            int cZ = ((cY *16) + cX);
            //System.out.println(cZ);

            if(e.getY() < root2.getHeight()){
                if(e.getY() > root2.getHeight() - 90 && e.getX() < 360) {
                    if (cZ < 256) {
                        side = cZ;
                        ImageView image = new ImageView(imageResize.bilinearInterpolation(Axis.sideAxis(), 500, 500));
                        thumbnailLarge.add(image, 2, 0, 2, 1);
                    } else if (cZ < 512) {
                        front = cZ - 256;
                        ImageView image = new ImageView(imageResize.bilinearInterpolation(Axis.frontAxis(), 500, 500));
                        thumbnailLarge.add(image, 2, 0, 2, 1);
                    } else if (cZ < 624) {
                        top = cZ - 512;
                        ImageView image = new ImageView(imageResize.bilinearInterpolation(Axis.frontAxis(), 500, 500));
                        thumbnailLarge.add(image, 2, 0, 2, 1);
                    } else if (cZ == 625) {
                        ImageView image1 = new ImageView(imageResize.bilinearInterpolation(mip.MIPSide(), 500, 500));

                        thumbnailLarge.add(image1, 2, 0, 2, 1);

                    } else if (cZ == 626) {
                        ImageView image = new ImageView(imageResize.bilinearInterpolation(mip.MIPFront(), 500, 500));
                        thumbnailLarge.add(image, 2, 0, 2, 1);
                    } else {
                        ImageView image = new ImageView(imageResize.bilinearInterpolation(mip.MIPTop(), 500, 500));
                        thumbnailLarge.add(image, 2, 0, 2, 1);
                    }
                    thumbNailStage.setScene(scene3);

                }

                //System.out.println(cY);

            }
        });




        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Bilinear Interpolation",
                        "Nearest Neighbour"
                );
        Button closeBtn = new Button("Return to Main Screen");
        ComboBox comboBox = new ComboBox(options);
        comboBox.setValue("Select Resizing Algorithm");
        Button viewThumbNails = new Button("View ImageThumbnails");
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

        closeBtn.setMaxWidth(180);
        root2.add(closeBtn,7,66,2,2);


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
            displayImg.setImage(axis.frontAxis());
        });

        sideSlider.setOnMouseDragged(e -> {
            if (front != 0 || top != 0) {
                clearImage(medical_image);
            }

            frontSlider.adjustValue(0);
            topSlider.adjustValue(0);
            displayImg.setImage(axis.sideAxis());
        });


        topSlider.setOnMouseDragged(e -> {
            if (front != 0 || side != 0) {
                clearImage(medical_image);
            }

            frontSlider.adjustValue(0);
            sideSlider.adjustValue(0);
            displayImg.setImage(axis.topAxis());
        });

        widthSlider.setOnMouseDragged(e -> {

            if(comboBox.getValue().equals("Bilinear Interpolation")){
                displayImg.setImage(imageResize.bilinearInterpolation(medical_image, (int) widthSlider.getValue(), (int) heightSlider.getValue()));
                System.out.println("Bilinear");
            }else{
                displayImg.setImage(imageResize.NearestNeighbour(medical_image, (int) widthSlider.getValue(), (int) heightSlider.getValue()));
                System.out.println("Nearest");
            }


        });

        heightSlider.setOnMouseDragged(e -> {


            if(comboBox.getValue().equals("Bilinear Interpolation")){

                displayImg.setImage(imageResize.bilinearInterpolation(medical_image, (int) widthSlider.getValue(), (int) heightSlider.getValue()));
                System.out.println("Bilinear");
            }else{
                displayImg.setImage(imageResize.NearestNeighbour(medical_image, (int) widthSlider.getValue(), (int) heightSlider.getValue()));
                System.out.println("Nearest");
            }

        });

        mipSidebutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearImage(medical_image);
                displayImg.setImage(mip.MIPSide());
            }
        });

        mipFrontbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearImage(medical_image);
                displayImg.setImage(mip.MIPFront());
            }
        });

        mipTopbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearImage(medical_image);
                displayImg.setImage(mip.MIPTop());
            }
        });

        closeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.setScene(scene1);
            }
        });


        resetBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                displayImg.setImage((medical_image));
            }
        });


        viewThumbNails.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {



                int k = 0;
                for (int j = 0; j < 40;j++) {
                    for (int i = 0; i < 16; i++) {
                        ImageView image = new ImageView(thumbNail[k]);
                        root2.add(image, i, j, 1, 1);
                        if(k < 627){
                            k++;
                        }else{
                            break;

                        }
                    }




                }

                thumbNailStage.setScene(scene2);
                thumbNailStage.show();
            }

        });



        nearestNeighbour.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                imageResizing.bilinearInterpolation(medical_image, 200, 200);

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

        root.add(viewThumbNails,2,7,1,1);
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



        // scene.getStylesheets().add("styleSheet.css");
        stage.setScene(scene1);
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








}