/*
 * Copyright (c) 2015, Picker Weng
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of CameraRecorder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Project:
 *     CameraRecorder
 *
 * File:
 *     CameraRecorder.java
 *
 * Author:
 *     Picker Weng (pickerweng@gmail.com)
 */

package com.example.hometaskversion3.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.hometaskversion3.ui.CameraRecorderActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class RecorderService extends Service {
    private static final String TAG = "RecorderService";
    //    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private static Camera mServiceCamera;
    private MediaRecorder mMediaRecorder;


    @Override
    public void onCreate() {
        mServiceCamera = CameraRecorderActivity.mCamera;
//        mSurfaceView = CameraRecorder.mSurfaceView;
        mSurfaceHolder = CameraRecorderActivity.mSurfaceHolder;

        super.onCreate();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        startRecording();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopRecording();
        Log.e(TAG, "onDestroy: from service Got called");
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public boolean startRecording() {
        try {

            Log.e(TAG, "startRecording: Started recording video");
            mServiceCamera = Camera.open();
            mServiceCamera.setDisplayOrientation(90);
            Camera.Parameters params = mServiceCamera.getParameters();
            mServiceCamera.setParameters(params);
            Camera.Parameters p = mServiceCamera.getParameters();
            p.setRecordingHint(true);


            final List<Size> listPreviewSize = p.getSupportedPreviewSizes();
            for (Size size : listPreviewSize) {
                Log.i(TAG, String.format("Supported Preview Size (%d, %d)", size.width, size.height));
            }


            Size previewSize = listPreviewSize.get(0);
            p.setPreviewSize(previewSize.width, previewSize.height);
            mServiceCamera.setParameters(p);

            try {
                mServiceCamera.setPreviewDisplay(mSurfaceHolder);
                mServiceCamera.startPreview();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }

            mMediaRecorder = new MediaRecorder();
            mServiceCamera.unlock();
            mMediaRecorder.setOrientationHint(90);     //This is for the output video;
            mMediaRecorder.setCamera(mServiceCamera);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            mMediaRecorder.setOutputFile(Environment.getExternalStorageDirectory().getPath() + "/video-" + formatter.format(new Date()) + ".mp4");
            mMediaRecorder.setOutputFile(getOutPutMediaFile(MEDIA_TYPE_VIDEO) == null ? "" : getOutPutMediaFile(MEDIA_TYPE_VIDEO).toString());
            mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
            mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
            mMediaRecorder.setMaxDuration(60000);

            mMediaRecorder.setOnInfoListener((mediaRecorder, i, i1) -> {
                if (i == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    Log.e(TAG, "onInfo: Duration Limit Reached great");
                    stopRecording();
                    startRecording();

                }
            });

            mMediaRecorder.prepare();
            mMediaRecorder.start();

            return true;

        } catch (IllegalStateException e) {
            Log.e(TAG, "Exception is thrown", e);
            e.printStackTrace();
            return false;

        } catch (IOException e) {
            Log.e(TAG, "Exception is thrown", e);
            e.printStackTrace();
            return false;
        }
    }

    private File getOutPutMediaFile(int type) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                "Java app"
        );

        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e(TAG, "Failed to create directory");
                return null;
            }
        }

        String formatter_date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        switch (type) {
            case MEDIA_TYPE_IMAGE:
                return new File(file.getAbsoluteFile() + File.separator + "IMG_" + formatter_date + ".jpg");
            case MEDIA_TYPE_VIDEO:
                return new File(file.getAbsoluteFile() + File.separator + "VID" + formatter_date + ".mp4");
            default:
                return null;
        }

    }


    public void stopRecording() {
        Toast.makeText(getBaseContext(), "Recording Stopped", Toast.LENGTH_SHORT).show();
        try {
            mServiceCamera.reconnect();

        } catch (Exception e) {
            Log.e(TAG, "stopRecording: ", e);
            e.printStackTrace();
        }
        try {
            mMediaRecorder.stop();
        } catch (Exception ex) {
            Log.e(TAG, "stopRecording: ", ex);
        }
        try {
            mMediaRecorder.reset();

            mServiceCamera.stopPreview();
            mMediaRecorder.release();

            mServiceCamera.unlock();
            mServiceCamera.release();
        } catch (Exception ex) {
            Log.e(TAG, "stopRecording: ", ex);
        }
    }


}
