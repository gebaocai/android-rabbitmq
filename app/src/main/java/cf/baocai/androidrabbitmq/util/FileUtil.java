package cf.baocai.androidrabbitmq.util;

import android.content.Context;

public class FileUtil {
    public static String dirPath(Context context) {
        String dir=context.getExternalCacheDir()+"/chatapp/recorder/audios";;
        return dir;
    }
}
