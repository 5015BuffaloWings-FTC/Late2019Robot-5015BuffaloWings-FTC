package org.firstinspires.ftc.teamcode;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.DogeCV;
import com.disnodeteam.dogecv.detectors.roverrukus.GoldDetector;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.opencv.core.Point;
import org.opencv.core.Rect;

@Autonomous(name="Depot Side")
@Disabled
public class DepotSide extends LinearOpMode
{
    Definitions robot = new Definitions();
    private GoldDetector detector; //Creation of Detector object
    /*
     * Used to store the cube's starting position.
     * The use of the String data type allow the use of a switch statement
     */
    private String cubePosition;

    private int positionX0; //Stores the X position of the cube on the screen
    private int positionX1; //Stores the X position of the cube on the screen
    private int positionX2; //Stores the X position of the cube on the screen
    private int positionX3; //Stores the X position of the cube on the screen
    private int positionX4; //Stores the X position of the cube on the screen
    private int averagePosition;

    @Override
    public void runOpMode() {
        telemetry.addData("STATE: ", "DO NOT RUN");
        telemetry.update();
        robot.robotHardwareMapInit(hardwareMap);


        detector = new GoldDetector(); //Creates a dogeCV "Gold Detector", this detector finds the location of a visible gold mineral.
        detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance()); //This activates the phone's camera
        detector.cropBRCorner = new Point(330 ,478);//make x less to crop more
        //detector.cropTLCorner = new Point(300, 1); //Crops top left corner of screen. This removes excess cubes from the visible input
        detector.useDefaults(); //Set detector to use default settings
        detector.downscale = 0.4; //Down scale for input frames. This speeds up computation
        detector.areaScoringMethod = DogeCV.AreaScoringMethod.MAX_AREA; //Inputs max camera resolution
        detector.maxAreaScorer.weight = 0.005; //Used to determine between multiple cubes
        detector.ratioScorer.weight = 5; //Used to determine between multiple cubes
        detector.ratioScorer.perfectRatio = 1.0; //Ratio adjustment
        detector.enable(); // Start the detector!

        robot.leadScrewMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        while (robot.leadScrewLimitBot.getState() && !isStopRequested()) {
            robot.leadScrewMotor.setPower(-0.75);
        }
        robot.leadScrewMotor.setPower(0);

        robot.resetEncoders();
        robot.driveWithEncoders();
        sleep(5000);
        telemetry.addData("STATE: ", "READY TO ROCK AND ROLL");
        telemetry.update();
        waitForStart();

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
         * Section (320, 480] corresponds to "RIGHT"
         * Section [160,320] corresponds to "CENTER"
         * Section [0, 160) corresponds to "LEFT"
         * Using these three sections, we can determine the position of the cube
         */
        if(averagePosition > 320)
            cubePosition = "RIGHT";
        else if(160 <= averagePosition)
            cubePosition = "CENTER";
        else
            cubePosition = "LEFT";

        telemetry.addData("Position", cubePosition);
        telemetry.update();

        /*
         * Runs autonomous code depending on the cube position
         */
        switch(cubePosition)
        {
            case "LEFT": //Runs code pertaining to a cube in position "LEFT"
                drop();
                cubeLeft();
                robot.setPower(0);
                break;
            case "CENTER": //Runs code pertaining to a cube in position "CENTER"
                drop();
                cubeCenter();
                robot.setPower(0);
                break;
            case "RIGHT": //Runs code pertaining to a cube in position "RIGHT"
                drop();
                cubeRight();
                robot.setPower(0);
                break;
            default:  //If any vision errors occur, It will default to run as if the cube was in position "CENTER"
                drop();
                cubeCenter();
                robot.setPower(0);
                break;
        }

        robot.setPower(0);
        detector.disable();
    }

    public void drop()
    {
        while(robot.leadScrewMotor.getCurrentPosition() < 24000 && !isStopRequested())
        {
            robot.leadScrewMotor.setTargetPosition(24000);
            robot.leadScrewMotor.setPower(1);
        }
        robot.leadScrewMotor.setPower(0);
    }

    public void cubeRight()
    {

    }

    public void cubeCenter()
    {

    }

    public void cubeLeft()
    {

    }
}

