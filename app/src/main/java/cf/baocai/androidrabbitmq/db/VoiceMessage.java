package cf.baocai.androidrabbitmq.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity(tableName = "voiceMessages")
public class VoiceMessage implements Serializable {
//    @PrimaryKey(autoGenerate = true)
//    public int id;

    @NonNull
    @PrimaryKey
    public String messageId;
    public String senderName;
    public boolean received;

    public float distance;
    public float duration;
    public String filePath;
}
