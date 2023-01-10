package cf.baocai.androidrabbitmq.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(VoiceMessage... voiceMessages);

    @Update
    public void update(VoiceMessage... voiceMessages);

    @Query("DELETE FROM voiceMessages")
    void delete();

//    @Query("Select * from voiceMessages")
//    List<VoiceMessage> getAll();

    @Query("Select * from voiceMessages")
    LiveData<List<VoiceMessage>> getAll();
}
