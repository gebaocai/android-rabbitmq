package cf.baocai.androidrabbitmq.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {VoiceMessage.class}, version = 1, exportSchema = false)
public abstract class VMDatabase extends RoomDatabase {
    public abstract VMessageDao vMessageDao();

    private static VMDatabase INSTANCE;
    public static synchronized VMDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),VMDatabase.class,"voiceMsg.db")
                    .build();
        }
        return INSTANCE;
    }
}
