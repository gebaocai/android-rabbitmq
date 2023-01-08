package cf.baocai.androidrabbitmq.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VMessageDao {
    @Insert
    void insertAll(VoiceMessage... voiceMessages);

    @Update
    public void updateUsers(VoiceMessage... voiceMessages);

    @Delete
    void delete(VoiceMessage voiceMessage);

//    @Query("Select * from voiceMessages")
//    List<VoiceMessage> getAll();

    @Query("Select * from voiceMessages")
    LiveData<List<VoiceMessage>> getAll();
}
