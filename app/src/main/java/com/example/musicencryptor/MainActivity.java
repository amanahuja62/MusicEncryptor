package com.example.musicencryptor;

import androidx.appcompat.app.AppCompatActivity;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "VoiceEncryptionActivity";
    private static final String seed = "guess"; // seeds
    private MediaPlayer mPlayer;
    private Button mPlayButton;
    private Button mEncryptionButton;
    private Button mDecryptionButton;
    private File sdCard = Environment.getExternalStorageDirectory();
    private File oldFile = new File(sdCard, "aman.mp3");
    // For the path of the audio file, find the audio file in res\raw\recording_old.3gpp and put it in the root directory of the external storage. For testing
    private FileInputStream fis = null;
    private FileOutputStream fos = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPlayButton = (Button) findViewById(R.id.playButton);
        mPlayButton.setOnClickListener(this);
        mEncryptionButton = (Button) findViewById(R.id.encryptionButton);
        mEncryptionButton.setOnClickListener(this);
        mDecryptionButton = (Button) findViewById(R.id.decryptionButton);
        mDecryptionButton.setOnClickListener(this);

    }
    @SuppressWarnings("static-access")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playButton:
                if (mPlayer != null) {
                    mPlayer.release();
                    mPlayer = null;
                }
                // mPlayer = MediaPlayer.create(this, R.raw.recording_old);
                boolean isSuccess = true;
                try {
                    fis = new FileInputStream(oldFile);
                    mPlayer = new MediaPlayer();
                    mPlayer.setDataSource(fis.getFD());
                    mPlayer.prepare(); // It will be wrong to remove
                    mPlayer.start();
                } catch (FileNotFoundException e) {
                    isSuccess = false;
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    isSuccess = false;
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    isSuccess = false;
                    e.printStackTrace();
                } catch (IOException e) {
                    isSuccess = false;
                    e.printStackTrace();
                } finally {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (!isSuccess)
                    Toast.makeText(this, "Playback failed", Toast.LENGTH_SHORT).show();
                break;

            case R.id.encryptionButton:
                // encrypted storage
                isSuccess = true;
                try {
                    fis = new FileInputStream(oldFile);
                    byte[] oldByte = new byte[(int) oldFile.length()];
                    fis.read(oldByte); // read
                    byte[] newByte = AESUtils.encryptVoice(seed, oldByte);
                    // encryption
                    fos = new FileOutputStream(oldFile);
                    fos.write(newByte);

                } catch (FileNotFoundException e) {
                    isSuccess = false;
                    e.printStackTrace();
                } catch (IOException e) {
                    isSuccess = false;
                    e.printStackTrace();
                } catch (Exception e) {
                    isSuccess = false;
                    e.printStackTrace();
                } finally {
                    try {
                        fis.close();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (isSuccess)
                    Toast.makeText(this, "Encryption succeeded", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Encryption failed", Toast.LENGTH_SHORT).show();

                Log.i(TAG, "Saved successfully");
                break;

            case R.id.decryptionButton:
                // decrypt and save
                isSuccess = true;
                byte[] oldByte = new byte[(int) oldFile.length()];
                try {
                    fis = new FileInputStream(oldFile);
                    fis.read(oldByte);
                    byte[] newByte = AESUtils.decryptVoice(seed, oldByte);
                    // decrypt
                    fos = new FileOutputStream(oldFile);
                    fos.write(newByte);

                } catch (FileNotFoundException e) {
                    isSuccess = false;
                    e.printStackTrace();
                } catch (IOException e) {
                    isSuccess = false;
                    e.printStackTrace();
                } catch (Exception e) {
                    isSuccess = false;
                    e.printStackTrace();
                }
                try {
                    fis.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (isSuccess)
                    Toast.makeText(this, "Decryption succeeded", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Decryption failed", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }

    }

}
