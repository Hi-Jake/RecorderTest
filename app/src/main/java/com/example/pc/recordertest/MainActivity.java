package com.example.pc.recordertest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity{
    Button recordeBtn;
    Button recordeStopBtn;
    Button playBtn;
    Button playStopBtn;

    static final String RECORDED_FILE = "/sdcard/download/englishtextbook/recorded.mp4";

    static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 0;
    static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    MediaRecorder recorder;
    MediaPlayer player;
    int playbackPosition = 0;
    boolean isPlaying = false;

    @Override
    protected void onPause() {
        super.onPause();
        if(recorder != null){
            recorder.release();
            recorder = null;
        }
        if (player != null){
            player.release();
            player = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        killMediaPlayer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        setValues();
        setUpEvents();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //권한 설정
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        checkPermission(MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }else{
                        startRec();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "녹음을 하시려면 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        checkPermission(MY_PERMISSIONS_REQUEST_RECORD_AUDIO, Manifest.permission.RECORD_AUDIO);
                    }else{
                        startRec();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "녹음을 하시려면 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
    public void checkAllPermissions(){
        //전체 권한 획득 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission(MY_PERMISSIONS_REQUEST_RECORD_AUDIO, Manifest.permission.RECORD_AUDIO);
        }
        else if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            checkPermission(MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        else{
            startRec();
        }
    }
    public void checkPermission(int request, String permission){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,permission)) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, request);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, request);
        }
    }

    public void startRec(){
        //녹음 시작
        if(recorder != null){
            recorder.stop();
            recorder.release();
            recorder = null;
        }// TODO Auto-generated method stub
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "EnglishTextBook");
        if (!file.mkdirs()) {
            Log.e("FILE", "Directory not created");
        }else{
            Log.e("FILE", "Directory created");
//            Toast.makeText(MainActivity.this, "폴더 생성 SUCCESS", Toast.LENGTH_SHORT).show();
        }
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setMaxDuration(10000);//최대녹음시간 10초 설정
        recorder.setOutputFile(RECORDED_FILE);
        recorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mediaRecorder, int i, int i1) {
                if (i == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    //녹음 최대시간 도달 시 이벤트 작성
                    stopRec();
                }
            }
        });
        try{
            Toast.makeText(getApplicationContext(), "녹음을 시작합니다.", Toast.LENGTH_SHORT).show();
            recorder.prepare();
            recorder.start();
        }catch (Exception ex){
            Log.e("RecorderStart", "Exception : ", ex);
        }
    }

    public void stopRec(){
        //멈추는 것이다.
        if(recorder == null){
            Toast.makeText(getApplicationContext(), "녹음중이 아닙니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        recorder.stop();
        recorder.release();
        recorder = null;

        Toast.makeText(getApplicationContext(), "녹음되었습니다.", Toast.LENGTH_SHORT).show();

    }
    private void playAudio(String url) throws Exception{
        killMediaPlayer();

        player = new MediaPlayer();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                isPlaying = false;
                Toast.makeText(getApplicationContext(), "음악 파일 종료됨.",Toast.LENGTH_SHORT).show();
            }
        });
        player.setDataSource(url);
        player.prepare();
        player.start();
        isPlaying = true;
    }


    private void killMediaPlayer() {
        if(player != null){
            try {
                Log.d("Kill","MediaPlayer");
                player.release();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void bindViews(){
        recordeBtn = (Button)findViewById(R.id.recordeBtn);
        recordeStopBtn = (Button)findViewById(R.id.recordeStopBtn);
        playBtn = (Button)findViewById(R.id.playBtn);
        playStopBtn = (Button)findViewById(R.id.playStopBtn);

    }

    public void setValues(){

    }

    public void setUpEvents(){
        recordeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("clicked","recordeBtn");
                checkAllPermissions();
//                startRec();
            }
        });

        recordeStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("clicked","recordeStopBtn");
                stopRec();
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("clicked","playBtn");
                try{
                    playAudio(RECORDED_FILE);
                    Toast.makeText(getApplicationContext(), "음악파일 재생 시작됨.", Toast.LENGTH_SHORT).show();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        playStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player != null && isPlaying){
                    isPlaying = false;
                    playbackPosition = player.getCurrentPosition();
                    player.pause();
                    Toast.makeText(getApplicationContext(), "음악 파일 재생 중지됨.",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "음악 파일이 재생중이지 않습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
