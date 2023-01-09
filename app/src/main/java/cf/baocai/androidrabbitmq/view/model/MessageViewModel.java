package cf.baocai.androidrabbitmq.view.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import cf.baocai.androidrabbitmq.db.VMessageRepository;
import cf.baocai.androidrabbitmq.db.VoiceMessage;

public class MessageViewModel extends AndroidViewModel {
    private VMessageRepository vMessageRepository;

    public MessageViewModel(@NonNull Application application) {
        super(application);
        vMessageRepository = new VMessageRepository(application);
    }

    public LiveData<List<VoiceMessage>> getListLiveData() {
        return vMessageRepository.getListLiveData();
    }

    public void insert(VoiceMessage voiceMessage) {
        vMessageRepository.insert(voiceMessage);
    }

    public void clear() {
        vMessageRepository.clear();
    }
}
