package com.jgh.androidssh.sshutils;

import android.os.Handler;
import android.util.Log;
import android.widget.EditText;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by jon on 5/17/14.
 */
public class ShellController {


    private final SessionController mSessionController;
    private BufferedReader mBufferedReader;
    private DataOutputStream mDataOutputStream;
    private Channel mChannel;

    private String mCommand;
    public ShellController(SessionController sessionController){
        mSessionController = sessionController;
    }


    public DataOutputStream getDataOutputStream(){
        return mDataOutputStream;
    }

    public void setCommand(String command){
        mCommand = command;
    }

    public void writeToOutput(String command){
        if(mDataOutputStream != null){
            try {
                mDataOutputStream.writeBytes(command+"\r\n");
                mDataOutputStream.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    public void openShell(Handler handler, EditText editText) throws JSchException, IOException {
        final Handler myHandler = handler;
        final EditText myEditText = editText;
        mChannel = mSessionController.getSession().openChannel("shell");
        mChannel.connect();
        mBufferedReader = new BufferedReader(new InputStreamReader(mChannel.getInputStream()));
        mDataOutputStream = new DataOutputStream(mChannel.getOutputStream());
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String line;
                    while(true){
                        while ((line = mBufferedReader.readLine()) != null) {
                            final String result = line;
                            myHandler.post(new Runnable() {
                                public void run() {
                                    myEditText.setText(myEditText.getText().toString() + "\r\n" + result+"\r\n");
                                }
                            });
                        }



                    }

                } catch (Exception e) {
                    Log.v("EXECPTION", " EX " + e.getMessage()+"."+e.getCause()+","+e.getClass().toString());
                }

            }
        }).start();
    }

}

