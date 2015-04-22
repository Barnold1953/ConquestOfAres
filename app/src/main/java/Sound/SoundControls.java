package Sound;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by Nathan on 4/20/2015.
 */
public class SoundControls {
    public SoundControls(Context context){
        SoundManager.init(context);
    }

    public static void march() {
        SoundManager.march();
    }

    public void scream(){
        SoundManager.getScream();
    }

    public void laser(){
        SoundManager.getLaser();
    }
}
