package Sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.Random;

import utkseniordesign.conquestofares.MainActivity;
import utkseniordesign.conquestofares.R;

/**
 * Created by Nathan on 4/20/2015.
 */
public class SoundManager {
    private static SoundPool soundPool;
    private static Context context;
    private static Random random = new Random(System.currentTimeMillis());
    private static int screamTot = 0, laserTot = 0;
    private static int loaded = 0;
    private static int[] screamID, laserID;
    private static int marchID, marchPlay;

    private static int getResId(String resName, Class<?> c){
        try{
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void init(Context c){
        context = c;

        Class raw = R.raw.class;
        Field[] fields = raw.getFields();
        for(Field field : fields){
            if(field.getName().contains("laser")){
                laserTot++;
            }
            if(field.getName().contains("scream")){
                screamTot++;
            }
        }

        soundPool = new SoundPool(laserTot + screamTot + 1, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded++;
            }
        });

        screamID = new int[screamTot];
        laserID = new int[laserTot];
        marchID = soundPool.load(context, R.raw.marching, 1);

        int resID;
        for(Integer i = 0; i < screamTot; i++){
            resID = getResId("scream" + i.toString(), R.raw.class);
            screamID[i] = soundPool.load(context, resID, 1);
        }
        for(Integer i = 0; i < laserTot; i++){
            resID = getResId("laser" + i.toString(), R.raw.class);
            laserID[i] = soundPool.load(context, resID, 1);
        }

        Log.d("SoundManager", "laserTot: " + laserTot + " screamTot: " + screamTot);
    }

    public static void march(){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        float aVol = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float mVol = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float vol = aVol / mVol;

        marchPlay = soundPool.play(marchID, vol, vol, 1, 0, 1f);
    }

    public static void stopMarch(){
        soundPool.stop(marchPlay);
    }

    public static void getScream(){
        Integer screamNum = Math.abs(random.nextInt());

        screamNum %= screamTot;
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        float aVol = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float mVol = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float vol = aVol / mVol;

        soundPool.play(screamID[screamNum], vol, vol, 1, 0, 1f);
    }

    public static void getLaser(){
        Integer laserNum = Math.abs(random.nextInt());

        laserNum %= laserTot;
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        float aVol = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float mVol = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float vol = aVol / mVol / 2.0f;

        soundPool.play(laserID[laserNum], vol, vol, 1, 0, 1f);
    }
}
