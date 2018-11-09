package com.input.vatoobhai.ergonomickeybordcontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.widget.TextView;

import static com.input.vatoobhai.ergonomickeybordcontroller.JSTATES.HOME;
import static com.input.vatoobhai.ergonomickeybordcontroller.JSTATES.LEFT;
import static com.input.vatoobhai.ergonomickeybordcontroller.JSTATES.RIGHT;


public class MainActivity extends AppCompatActivity {

    TextView text_main;
    TextView text_status;
    String mMessage = "";
    JSTATES mState = HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_main = findViewById(R.id.main_text_input_string);
        text_status = findViewById(R.id.main_text_status);
        text_status.setText("Start");
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {

        // Check that the event came from a game controller
        if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) ==
                InputDevice.SOURCE_JOYSTICK &&
                event.getAction() == MotionEvent.ACTION_MOVE) {

            // Process all historical movement samples in the batch
            final int historySize = event.getHistorySize();

            // Process the movements starting from the
            // earliest historical position in the batch
            for (int i = 0; i < historySize; i++) {
                // Process the event at historical position i
                processJoystickInput(event, i);
            }

            // Process the current movement sample in the batch (position -1)
            processJoystickInput(event, -1);
            return true;
        }
        return super.onGenericMotionEvent(event);
    }

    private static float getCenteredAxis(MotionEvent event,
                                         InputDevice device, int axis, int historyPos) {
        final InputDevice.MotionRange range =
                device.getMotionRange(axis, event.getSource());

        // A joystick at rest does not always report an absolute position of
        // (0,0). Use the getFlat() method to determine the range of values
        // bounding the joystick axis center.
        if (range != null) {
            final float flat = range.getFlat();
            final float value =
                    historyPos < 0 ? event.getAxisValue(axis):
                            event.getHistoricalAxisValue(axis, historyPos);

            // Ignore axis values that are within the 'flat' region of the
            // joystick axis center.
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0;
    }

    private void processJoystickInput(MotionEvent event,
                                      int historyPos) {

        InputDevice mInputDevice = event.getDevice();

        // Calculate the horizontal distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat axis, or the right control stick.
        int x_axis = -1;
        int y_axis = -1;

        float x = getCenteredAxis(event, mInputDevice,
                MotionEvent.AXIS_X, historyPos);
        x_axis = MotionEvent.AXIS_X;

        if (x == 0) {
            x = getCenteredAxis(event, mInputDevice,
                    MotionEvent.AXIS_HAT_X, historyPos);
            x_axis = MotionEvent.AXIS_HAT_X;
        }

        if (x == 0) {
            x = getCenteredAxis(event, mInputDevice,
                    MotionEvent.AXIS_Z, historyPos);
            x_axis = MotionEvent.AXIS_Z;
        }

        if (x == 0) {
            x_axis = -1;
        }
        // Calculate the vertical distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat switch, or the right control stick.
        float y = getCenteredAxis(event, mInputDevice,
                MotionEvent.AXIS_Y, historyPos);
        y_axis = MotionEvent.AXIS_Y;

        if (y == 0) {
            y = getCenteredAxis(event, mInputDevice,
                    MotionEvent.AXIS_HAT_Y, historyPos);
            y_axis = MotionEvent.AXIS_HAT_Y;

        }
        if (y == 0) {
            y = getCenteredAxis(event, mInputDevice,
                    MotionEvent.AXIS_RZ, historyPos);
            y_axis = MotionEvent.AXIS_RZ;
        }
        if (y == 0) {
            y_axis = -1;
        }

        // Update the ship object based on the new x and y values

        Log.d(Tag, "X = " +x +"Y = " +y);

        if (Math.abs(x) != 1)
            x = 0;

        if (Math.abs(y) != 1)
            y = 0;

        if (Math.abs(x) == 1 || Math.abs(y) == 1) {
            processxy(x_axis, x, y_axis, y);
        }
    }




    //Trial Log in Phone
    private void processxy(int jx, float x, int jy, float y){
//        String msg = "Joystick X = " +jx +"X = " +x +"\nJoystick Y =" +jy +"Y = " +y;
        String msg = MotionEvent.axisToString(jx) + "  X = " +x + "\n" +MotionEvent.axisToString(jy) +"  Y = " +y;
        text_status.setText(msg);

        switch (jx) {
            case (MotionEvent.AXIS_X):
                if (x == 1) {
                    process_joystick(JOYSTICK.J1_RIGHT);
                } else if (x == -1) {
                    process_joystick(JOYSTICK.J1_LEFT);
                }
                break;

            case (MotionEvent.AXIS_Z):
                if (x == 1) {
                    process_joystick(JOYSTICK.J2_RIGHT);
                } else if (x == -1) {
                    process_joystick(JOYSTICK.J2_LEFT);
                }
                break;

            default:
                // ignore
                break;
        }

        switch (jy) {
            case (MotionEvent.AXIS_Y):
                if (y == 1) {
                    process_joystick(JOYSTICK.J1_DOWN);
                } else if (y == -1) {
                    process_joystick(JOYSTICK.J1_UP);
                }
                break;

            case (MotionEvent.AXIS_RZ):
                if (y == 1) {
                    process_joystick(JOYSTICK.J2_DOWN);
                } else if (y == -1) {
                    process_joystick(JOYSTICK.J2_UP);
                }
                break;

            default:
                // ignore
                break;
        }

    }

    private JOYSTICK jp = JOYSTICK.NONE;
    private long timestamp = 0;

    private void process_joystick(JOYSTICK j){

        if (j == jp){
            long t = System.currentTimeMillis();
            if (t - timestamp < 200){
                //ignore multiple case
                return;
            }
        }

        jp = j;
        timestamp = System.currentTimeMillis();

        Log.d(Tag, "j = " +j +"State =" +mState);

        switch (mState) {

            case HOME:
                switch (j) {
                    case J1_UP:
                        mMessage = mMessage + "9";
                        break;
                    case J1_RIGHT:
                        mState = RIGHT;
                        break;
                    case J1_DOWN:
                        mMessage = mMessage + "0";
                        break;
                    case J1_LEFT:
                        mState = LEFT;
                        break;
                    case J2_UP:
                        // do nothing;
                        break;
                    case J2_RIGHT:
                        mMessage = mMessage + " ";
                        break;
                    case J2_DOWN:
                        mMessage = mMessage + "\n";
                        break;
                    case J2_LEFT:
                        if (mMessage.length() > 0) {
                            mMessage = mMessage.substring(0, mMessage.length() - 1);
                        }
                        break;
                    default:
                        // do nothing
                        break;
                }
                break;

            case LEFT:
                switch (j) {
                    case J1_UP:
                        //mMessage = mMessage + "9";
                        break;
                    case J1_RIGHT:
                        // to do
                        break;
                    case J1_DOWN:
                        //mMessage = mMessage + "0";
                        break;
                    case J1_LEFT:
                        // to do
                        break;
                    case J2_UP:
                        mMessage = mMessage + "1";
                        mState = HOME;
                        break;
                    case J2_RIGHT:
                        mMessage = mMessage + "2";
                        mState = HOME;
                        break;
                    case J2_DOWN:
                        mMessage = mMessage + "3";
                        mState = HOME;
                        break;
                    case J2_LEFT:
                        mMessage = mMessage + "4";
                        mState = HOME;
                        break;
                    default:
                        // do nothing
                        break;
                }
                break;

            case RIGHT:
                switch (j) {
                    case J1_UP:
                        //mMessage = mMessage + "9";
                        break;
                    case J1_RIGHT:
                        // to do
                        break;
                    case J1_DOWN:
                        //mMessage = mMessage + "0";
                        break;
                    case J1_LEFT:
                        // to do
                        break;
                    case J2_UP:
                        mMessage = mMessage + "5";
                        mState = HOME;
                        break;
                    case J2_RIGHT:
                        mMessage = mMessage + "6";
                        mState = HOME;
                        break;
                    case J2_DOWN:
                        mMessage = mMessage + "7";
                        mState = HOME;
                        break;
                    case J2_LEFT:
                        mMessage = mMessage + "8";
                        mState = HOME;
                        break;
                    default:
                        // do nothing
                        break;
                }
                break;

            default:
                break;
        }

        Log.d(Tag, "Message = " +mMessage);
        text_main.setText(mMessage);
    }



    private static final String Tag = "Cinput";

}
