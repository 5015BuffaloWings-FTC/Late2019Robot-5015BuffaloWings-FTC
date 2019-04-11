package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp; //For running TeleOP
import com.qualcomm.robotcore.hardware.DcMotor; //For redefining motors.
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range; //Allows digital inputs to be clipped to a set range

@TeleOp(name="TeleOp")
public class TeleOpBuffaloWings extends OpMode
{
    Definitions robot = new Definitions();
    double slowMovement = 0.5; //Slow movement is used as a multiplier to change movement speed
    double armLowerLimit; //Used to set the lower limit of the scoring arm's range of motion
    double armHigherLimit; //Used to set the upper limit of the scoring arm's range of motion

    public void init()
    {
        robot.robotHardwareMapInit(hardwareMap);
        robot.resetEncoders();
        robot.runWithOutEncoders();
    }

    public void loop() {
        /**
         * DRIVING SECTION
         */
        //Using Range.clip to limit joystick values from -1 to 1 (clipping the outputs)
        //If gamepad 1 presses the right bumper the speed will be multiplied by the slowMovement constant
        if (gamepad1.right_bumper) {
            robot.rightFrontMotor.setPower(Range.clip((-gamepad1.left_stick_y - (gamepad1.left_stick_x) - gamepad1.right_stick_x) * slowMovement, -1, 1));
            robot.leftFrontMotor.setPower(Range.clip((gamepad1.left_stick_y - (gamepad1.left_stick_x) - gamepad1.right_stick_x) * slowMovement, -1, 1));
            robot.rightBackMotor.setPower(Range.clip((-gamepad1.left_stick_y + (gamepad1.left_stick_x) - gamepad1.right_stick_x) * slowMovement, -1, 1));
            robot.leftBackMotor.setPower(Range.clip((gamepad1.left_stick_y + (gamepad1.left_stick_x) - gamepad1.right_stick_x) * slowMovement, -1, 1));
        } else {
            robot.rightFrontMotor.setPower(Range.clip((-gamepad1.left_stick_y - (gamepad1.left_stick_x) - gamepad1.right_stick_x), -1, 1));
            robot.leftFrontMotor.setPower(Range.clip((gamepad1.left_stick_y - (gamepad1.left_stick_x) - gamepad1.right_stick_x), -1, 1));
            robot.rightBackMotor.setPower(Range.clip((-gamepad1.left_stick_y + (gamepad1.left_stick_x) - gamepad1.right_stick_x), -1, 1));
            robot.leftBackMotor.setPower(Range.clip((gamepad1.left_stick_y + (gamepad1.left_stick_x) - gamepad1.right_stick_x), -1, 1));
        }


        /**
         * LEADSCREW SECTION
         */

            if (robot.leadScrewLimitBot.getState()) {
                if ((gamepad2.dpad_down || gamepad1.dpad_down)) {
                    robot.leadScrewMotor.setPower(-1);
                } else if (gamepad2.dpad_up || gamepad1.dpad_up) {
                    robot.leadScrewMotor.setPower(1);
                } else
                    robot.leadScrewMotor.setPower(0);
            } else if (!robot.leadScrewLimitBot.getState())
                robot.leadScrewMotor.setPower(0.5);
            else
                robot.leadScrewMotor.setPower(0);

        /**
         * SCORING ARM SECTION
         */
        robot.scoringArmMotor.setPower(gamepad2.left_stick_y);

        /**
         * COLLECTION SECTION
         */
        robot.rightCollectionMotor.setPower(gamepad2.right_stick_y);
        robot.leftCollectionMotor.setPower(-gamepad2.right_stick_y);

        if( gamepad2.x)
            robot.inTakeServo.setPower(1);
        else
            robot.inTakeServo.setPower(0);

        /**
         * SERVO SECTION
         */
         if(gamepad2.y || gamepad2.a)
         {
             if (gamepad2.y) {
                 robot.rightCollectionServo.setPower(-1);
                 robot.leftCollectionServo.setPower(1);
             }
             if (gamepad2.a) {
                 robot.rightCollectionServo.setPower(1);
                 robot.leftCollectionServo.setPower(-1);
             }
         }
         else
         {
             robot.rightCollectionServo.setPower(0);
             robot.leftCollectionServo.setPower(0);
         }

         if(gamepad1.y || gamepad1.a)
         {
             if(gamepad1.y)
                 robot.dumpServo.setPower(0.5);
             if(gamepad1.a)
                 robot.dumpServo.setPower(-0.5);
         }
         else
         {
             robot.dumpServo.setPower(0);
         }





        telemetry.addData("Lead Screw Position:", robot.leadScrewMotor.getCurrentPosition());
        telemetry.addData("Lead Screw Limit switch", robot.leadScrewLimitBot.getState());
        //telemetry.addData("scoring arm motor position", robot.scoringArmMotor.getCurrentPosition());
        //telemetry.addData("left trigger", gamepad2.left_trigger);
        telemetry.addData("Left Back", robot.leftBackMotor.getCurrentPosition());
        //telemetry.addData("Arm Position", robot.scoringArmMotor.getCurrentPosition());24000
        telemetry.update();
    }

    public void stop()
    {
        robot.setPower(0);
        //robot.leadScrewMotor.setPower(0);
        //robot.scoringArmMotor.setPower(0);
    }
}
