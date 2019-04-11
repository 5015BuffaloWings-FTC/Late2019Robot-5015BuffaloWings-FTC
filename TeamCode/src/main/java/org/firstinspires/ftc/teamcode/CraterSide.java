package org.firstinspires.ftc.teamcode;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.DogeCV;
import com.disnodeteam.dogecv.detectors.roverrukus.GoldDetector;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.opencv.core.Point;
import org.opencv.core.Rect;

@Autonomous(name="CraterSide")
public class CraterSide extends LinearOpMode
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
        detector.cropBRCorner = new Point(330 ,478);//Crops Bottom Right corner of screen. This removes excess cubes from the visible input. To crop more of the screen, raise the value of x.-
        detector.useDefaults(); //Set detector to use default settings
        detector.downscale = 0.4; //Down scale for input frames. This speeds up computation
        detector.areaScoringMethod = DogeCV.AreaScoringMethod.MAX_AREA; //Inputs max camera resolution
        detector.maxAreaScorer.weight = 0.005; //Used to determine between multiple cubes
        detector.ratioScorer.weight = 5; //Used to determine between multiple cubes
        detector.ratioScorer.perfectRatio = 1.0; //Ratio adjustment
        detector.enable(); // Start the detector!

        /**
         * This Code resets the lead screw to its starting position
         */
        robot.leadScrewMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);//Sets the leadscrew to run without the use of encoders
        while (robot.leadScrewLimitBot.getState() && !isStopRequested()) {
            robot.leadScrewMotor.setPower(-0.75);
        }
        robot.leadScrewMotor.setPower(0);

        robot.resetEncoders();//Resets all motor encoders and sets them to run to position
        robot.driveWithEncoders();//Allows the robot to drive robot.power() while still reading encoders
        sleep(5000);//sleeps to give time for camera to turn on
        telemetry.addData("STATE: ", "READY TO ROCK AND ROLL"); //Tells the drivers that the robot is ready to run
        telemetry.update();//updates the telemetry
        waitForStart();//waits for driver to hit start

        drop();//Makes the Robot drop from the lander

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
                cubeLeft();
                robot.setPower(0);
                break;
            case "CENTER": //Runs code pertaining to a cube in position "CENTER"
                cubeCenter();
                robot.setPower(0);
                break;
            case "RIGHT": //Runs code pertaining to a cube in position "RIGHT"
                cubeRight();
                robot.setPower(0);
                break;
            default:  //If any vision errors occur, It will default to run as if the cube was in position "CENTER"
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
        robot.setStrafeLeft(); //come off wall
        while(robot.leftBackMotor.getCurrentPosition() < 1500   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setDriveForward(); //line up to cube
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 800   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setStrafeLeft(); //hit cube
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 1300   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setStrafeRight(); //back up from cube
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 300   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setDriveBackward(); //drive to wall
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 4600   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setRotateRight(); //Turn to face wall
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 1700   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setStrafeRight(); //Line up to wall
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 1250   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setStrafeLeft(); //back off wall
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 250   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setDriveForward(); //drive to depot
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 2500   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.rightCollectionServo.setPower(-1);
        robot.leftCollectionServo.setPower(1);
        sleep(1200);
        robot.rightCollectionServo.setPower(0);
        robot.leftCollectionServo.setPower(0);

        robot.inTakeServo.setPower(-1);
        sleep(500);
        robot.inTakeServo.setPower(0);


        robot.setDriveBackward(); //drive to crater
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 5000   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        while(robot.leadScrewMotor.getCurrentPosition() > 13250)
        {
            robot.leadScrewMotor.setPower(-1);
        }
        robot.leadScrewMotor.setPower(0);
    }

    public void cubeCenter()
    {
        robot.setStrafeLeft(); //off lander + hit cube
        while(robot.leftBackMotor.getCurrentPosition() < 2400  && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setStrafeRight();// back up from cube
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 650   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setDriveBackward(); //drive to wall
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 3000   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setRotateRight(); //Turn to face wall
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 1700   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setStrafeRight(); //Line up to wall
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 1250   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setStrafeLeft(); //back off wall
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 250   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setDriveForward(); //drive to depot
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 3200   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setDriveBackward(); //drive to depot
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 5000   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

    }

    public void cubeLeft()
    {
        robot.setStrafeLeft(); //get away form lander
        while(robot.leftBackMotor.getCurrentPosition() < 1500   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setDriveBackward(); //line up to cube
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 750   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setStrafeLeft(); //hit cube
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 700   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setStrafeRight(); //back up from cube
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 700   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setDriveBackward(); //drive to wall
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 2000   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setRotateRight(); //Turn to face wall
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 1700   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setStrafeRight(); //Line up to wall
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 1250   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setStrafeLeft(); //back off wall
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 250   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setDriveForward(); //drive to depot
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 3200   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);

        robot.setDriveBackward(); //drive to depot
        robot.resetEncoders();
        robot.driveWithEncoders();
        while(robot.leftBackMotor.getCurrentPosition() < 5000   && !isStopRequested())
        {
            robot.setPower(1);
        }
        robot.setPower(0);
    }
}

