package com.asus.zenbogotolocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.robotframework.API.results.RoomInfo;
import com.robot.asus.robotactivity.RobotActivity;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends RobotActivity {

    // request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    // robotAPI flags
    private static boolean isRobotApiInitialed = false;

    // 1st roomInfo string
    private String sFirstRoom;
    private String Room2;

    // buttons
    private Button mButtonGrantPermission;
    private Button mButtonGetRoomInfo;
    private Button mButtonGetRoomInfo2;
    private Button mButtonGoTo;
    private Button mButtonGoTo2;

    // textViews
    private TextView mTextViewPermissionStatus;
    private TextView mTextViewFirstRoomKeyword;
    private TextView mTextViewSecondRoomKeyword;
    private TextView mTextViewDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // textViews
        mTextViewPermissionStatus = (TextView) findViewById(R.id.textView_permission_status);
        mTextViewFirstRoomKeyword = (TextView) findViewById(R.id.button_goTo);
        mTextViewSecondRoomKeyword = (TextView) findViewById(R.id.button_goTo2);
        mTextViewDestination = (TextView) findViewById(R.id.Destination);

        // buttons
        mButtonGrantPermission = (Button) findViewById(R.id.button_requestPermission);
        mButtonGrantPermission.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                // request READ_CONTACTS permission
                requestPermission();

            }
        });

        mButtonGetRoomInfo = (Button) findViewById(R.id.button_getRoomInfo);
        mButtonGetRoomInfo.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    //3. use robotAPI to get all room info:
                    ArrayList<RoomInfo> arrayListRooms = robotAPI.contacts.room.getAllRoomInfo();

                    sFirstRoom = arrayListRooms.get(0).keyword;
                    Log.d("ZenboGoToLocation", "arrayListRooms = " + arrayListRooms);
                    Log.d("ZenboGoToLocation", "arrayListRooms(0) = " + sFirstRoom);
                    mTextViewFirstRoomKeyword.setText("Go to "+ sFirstRoom);
                    mButtonGoTo.setEnabled(true);
                    Room2 = arrayListRooms.get(1).keyword;
                    mTextViewSecondRoomKeyword.setText( "Go to " +Room2);
                    mButtonGoTo2.setEnabled(true);
                }
                catch (Exception e){
                    Log.d("ZenboGoToLocation", "get room info result exception = "+ e);
                }
            }
        });
        /*
        mButtonGetRoomInfo2 = (Button) findViewById(R.id.button_getRoomInfo2);
        mButtonGetRoomInfo2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    //3. use robotAPI to get all room info:
                    ArrayList<RoomInfo> arrayListRooms = robotAPI.contacts.room.getAllRoomInfo();

                    Room2 = arrayListRooms.get(1).keyword;
                    Log.d("ZenboGoToLocation", "arrayListRooms = " + arrayListRooms);
                    Log.d("ZenboGoToLocation", "arrayListRooms(0) = " + Room2);
                    mTextViewSecondRoomKeyword.setText(Room2);
                    mButtonGoTo2.setEnabled(true);

                }
                catch (Exception e){
                    Log.d("ZenboGoToLocation", "get room info result exception = "+ e);
                }

            }
        });
        */

        mButtonGoTo = (Button) findViewById(R.id.button_goTo);
        mButtonGoTo.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!sFirstRoom.equals("")) {

                    if(isRobotApiInitialed) {
                        // use robotAPI to go to the position "keyword":
                        robotAPI.motion.goTo(sFirstRoom);
                        mTextViewDestination.setText(sFirstRoom);
                    }

                }
            }
        });
        mButtonGoTo2 = (Button) findViewById(R.id.button_goTo2);
        mButtonGoTo2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!Room2.equals("")) {

                    if(isRobotApiInitialed) {
                        // use robotAPI to go to the position "keyword":
                        robotAPI.motion.goTo(Room2);
                        mTextViewDestination.setText(Room2);
                    }

                }
            }
        });

    }



    @Override
    protected void onResume() {
        super.onResume();

        // check permission READ_CONTACTS is granted or not
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted by user yet
            Log.d("ZenboGoToLocation", "READ_CONTACTS permission is not granted by user yet");
            mTextViewPermissionStatus.setText(getString(R.string.permission_not_granted));
            mButtonGrantPermission.setEnabled(true);
            mButtonGetRoomInfo.setEnabled(false);
        }
        else{
            // permission is granted by user
            Log.d("ZenboGoToLocation", "READ_CONTACTS permission is granted");
            mTextViewPermissionStatus.setText(getString(R.string.permission_granted));
            mButtonGrantPermission.setEnabled(false);
            mButtonGetRoomInfo.setEnabled(true);
        }

        // initial params
        mTextViewFirstRoomKeyword.setText(getString(R.string.first_room_info));
        mButtonGoTo.setEnabled(false);
        sFirstRoom="";
        mTextViewSecondRoomKeyword.setText(getString(R.string.room2_info));
        mButtonGoTo2.setEnabled(false);
        Room2="";
    }



    public static RobotCallback robotCallback = new RobotCallback() {
        @Override
        public void initComplete() {
            super.initComplete();

            Log.d("ZenboGoToLocation", "initComplete()");
            isRobotApiInitialed = true;
        }

        @Override
        public void onResult(int cmd, int serial, RobotErrorCode err_code, Bundle result) {
            super.onResult(cmd, serial, err_code, result);
        }

        @Override
        public void onStateChange(int cmd, int serial, RobotErrorCode err_code, RobotCmdState state) {
            super.onStateChange(cmd, serial, err_code, state);
        }
    };


    public static RobotCallback.Listen robotListenCallback = new RobotCallback.Listen() {
        @Override
        public void onFinishRegister() {

        }

        @Override
        public void onVoiceDetect(JSONObject jsonObject) {

        }

        @Override
        public void onSpeakComplete(String s, String s1) {

        }

        @Override
        public void onEventUserUtterance(JSONObject jsonObject) {

        }

        @Override
        public void onResult(JSONObject jsonObject) {

        }

        @Override
        public void onRetry(JSONObject jsonObject) {

        }
    };


    public MainActivity() {
        super(robotCallback, robotListenCallback);
    }


    private void requestPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                this.checkSelfPermission(Manifest.permission.READ_CONTACTS) ==
                        PackageManager.PERMISSION_GRANTED) {
            // Android version is lesser than 6.0 or the permission is already granted.
            Log.d("ZenboGoToLocation", "permission is already granted");
            return;
        }

        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            //showMessageOKCancel("You need to allow access to Contacts",
            //        new DialogInterface.OnClickListener() {
            //            @Override
            //            public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                                    PERMISSIONS_REQUEST_READ_CONTACTS);
            //            }
            //        });
        }
    }

    /*
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    */
}