package cf.baocai.androidrabbitmq.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "voiceMessages")
public class VoiceMessage {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String messageId;
    public String senderName;

    public float distance;
    public Integer duration;
    public String filePath;
}
