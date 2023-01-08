package cf.baocai.androidrabbitmq;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import cf.baocai.androidrabbitmq.db.VMDatabase;
import cf.baocai.androidrabbitmq.db.VMessageDao;
import cf.baocai.androidrabbitmq.db.VoiceMessage;

public class VMessageRepository {

    private LiveData<List<VoiceMessage>> listLiveData;
    private VMessageDao vMessageDao;

    public VMessageRepository(Context context) {
        VMDatabase vmDatabase = VMDatabase.getDatabase(context.getApplicationContext());
        vMessageDao = vmDatabase.vMessageDao();
        listLiveData = vMessageDao.getAll();
    }

    public void insert(VoiceMessage voiceMessage) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                vMessageDao.insertAll(voiceMessage);
            }
        });

    }

    public LiveData<List<VoiceMessage>> getListLiveData() {
        return listLiveData;
    }
}