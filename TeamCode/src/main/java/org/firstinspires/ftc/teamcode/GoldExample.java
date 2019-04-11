package org.firstinspires.ftc.teamcode;

import com.disnodeteam.dogecv.CameraViewDisplay; //For phone camera input
import com.disnodeteam.dogecv.DogeCV; //For DogeCV Computer vision
import com.disnodeteam.dogecv.detectors.roverrukus.GoldDetector; //For using GoldDetector
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous; //For running Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

//For locating Gold Mineral
import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.util.Locale;

@Autonomous(name="GoldAuto")
@Disabled
public class GoldExample extends OpMode
{
    private GoldDetector detector; //Creation of Detector object
    private int positionX0; //Stores the X position of the cube on the screen
    private int positionX1; //Stores the X position of the cube on the screen
    private int positionX2; //Stores the X position of the cube on the screen
    private int positionX3; //Stores the X position of the cube on the screen
    private int positionX4; //Stores the X position of the cube on the screen
    private int averagePosition;

    //BNO055IMU imu;

    // State used for updating telemetry
    Orientation angles;
    Acceleration gravity;

    /*
     * Used to store the cube's starting position.
     * The use of the String data type allow the use of a switch statement
     */
    private String cubePosition;


    /*
     * Code to run when the drivers hits INIT
     */
    @Override
    public void init()
    {
        //Set up for detector
        detector = new GoldDetector(); //Creates a dogeCV "Gold Detector", this detector finds the location of a visible gold mineral.
        detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance()); //This activates the phone's camera
        detector.cropBRCorner = new Point(350 ,478);
        //detector.cropTLCorner = new Point(300, 1); //Crops top left corner of screen. This removes excess cubes from the visible input
        detector.useDefaults(); //Set detector to use default settings
        detector.downscale = 0.4; //Down scale for input frames. This speeds up computation
        detector.areaScoringMethod = DogeCV.AreaScoringMethod.MAX_AREA; //Inputs max camera resolution
        detector.maxAreaScorer.weight = 0.005; //Used to determine between multiple cubes
        detector.ratioScorer.weight = 5; //Used to determine between multiple cubes
        detector.ratioScorer.perfectRatio = 1.0; //Ratio adjustment
        detector.enable(); // Start the detector!

        // Set up the parameters with which we will use our IMU. Note that integration
        // algorithm here just reports accelerations to the logcat log; it doesn't actually
        // provide positional information.
//        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
//        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
//        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
//        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
//        parameters.loggingEnabled      = true;
//        parameters.loggingTag          = "IMU";
//        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        //imu = hardwareMap.get(BNO055IMU.class, "imu");
        //imu.initialize(parameters);

        // Set up our telemetry dashboard
        //composeTelemetry();

        // Start the logging of measured acceleration
        //imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);
    }

    /*
     * Code to run REPEATEDLY when the driver hits INIT
     */
    @Override
    public void init_loop()
    {

    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start()
    {
        Rect rect0 = detector.getFoundRect(); //Draws a virtual rectangle around chosen cube
        Rect rect1 = detector.getFoundRect(); //Draws a virtual rectangle around chosen cube
        Rect rect2 = detector.getFoundRect(); //Draws a virtual rectangle around chosen cube
        Rect rect3 = detector.getFoundRect(); //Draws a virtual rectangle around chosen cube
        Rect rect4 = detector.getFoundRect(); //Draws a virtual rectangle around chosen cube
        /*
         * Finds X position of the virtual rectangle
         * Because the phone is mounted horizontally, the effective X value is technically the y value.
         */
        positionX0 = (int) (rect0.y + 0.5 * rect0.height);
        positionX1 = (int) (rect1.y + 0.5 * rect1.height);
        positionX2 = (int) (rect2.y + 0.5 * rect2.height);
        positionX3 = (int) (rect3.y + 0.5 * rect3.height);
        positionX4 = (int) (rect4.y + 0.5 * rect4.height);

        averagePosition = (positionX0 + positionX1 + positionX2 + positionX3 + positionX4) / 5;


        /*
         * After downscaling the camera input, the horizontal resolution is 480 Pixels.
         * The following code divides this section into 3 parts. [0, 160) U [160,320] U (320, 480]
         * Section (320, 480] corresponds to "LEFT"
         * Section [160,320] corresponds to "CENTER"
         * Section [0, 160) corresponds to "RIGHT"
         * Using these three sections, we can determine the position of the cube
         */
        if(averagePosition > 320)
            cubePosition = "RIGHT";
        else if(160 <= averagePosition)
            cubePosition = "CENTER";
        else
            cubePosition = "LEFT";




        /*
         * Runs autonomous code depending on the cube position
         */
        switch(cubePosition)
        {
            case "LEFT": //Runs code pertaining to a cube in position "LEFT"
                //drop();
                //cubeLeft();
                break;
            case "CENTER": //Runs code pertaining to a cube in position "CENTER"
                //drop();
                //cubeCenter();
                break;
            case "RIGHT": //Runs code pertaining to a cube in position "RIGHT"
                //drop();
                //cubeRight();
                break;
            default:  //If any vision errors occur, It will default to run as if the cube was in position "CENTER"
                //drop();
                //cubeCenter();
                break;
        }

    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY
     */
    @Override
    public void loop()
    {
        //These let the drivers know what auto will do before it happens
        telemetry.addData("IsFound: ", detector.isFound());
        telemetry.addData("Cube Position: ", cubePosition);
        telemetry.update();
    }

//    void composeTelemetry() {
//
//        // At the beginning of each telemetry update, grab a bunch of data
//        // from the IMU that we will then display in separate lines.
//        telemetry.addAction(new Runnable() { @Override public void run()
//        {
//            // Acquiring the angles is relatively expensive; we don't want
//            // to do that in each of the three items that need that info, as that's
//            // three times the necessary expense.
//            angles   = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
//            gravity  = imu.getGravity();
//        }
//        });
//
//        telemetry.addLine()
//                .addData("YAW: ", new Func<String>() {
//                    @Override public String value() {
//                        return formatAngle(angles.angleUnit, angles.firstAngle);
//                    }
//                })
//                .addData("ROLL: ", new Func<String>() {
//                    @Override public String value() {
//                        return formatAngle(angles.angleUnit, angles.secondAngle);
//                    }
//                })
//                .addData("PITCH: ", new Func<String>() {
//                    @Override public String value() {
//                        return formatAngle(angles.angleUnit, angles.thirdAngle);
//                    }
//                });
//    }
//
//    //----------------------------------------------------------------------------------------------
//    // Formatting
//    //----------------------------------------------------------------------------------------------
//
//    String formatAngle(AngleUnit angleUnit, double angle) {
//        return formatDegrees(AngleUnit.DEGREES.fromUnit(angleUnit, angle));
//    }
//
//    String formatDegrees(double degrees){
//        return String.format(Locale.getDefault(), "%.1f", AngleUnit.DEGREES.normalize(degrees));
//    }
//


    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        if(detector != null) detector.disable(); //Disable the detector if
    }

}