package cf.baocai.androidrabbitmq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

import cf.baocai.androidrabbitmq.db.VoiceMessage;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private LinearLayoutManager linearLayoutManager;
    private MessageViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.message_list);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        VMAdapter adapter = new VMAdapter();
        recyclerView.setAdapter(adapter);

        model = new ViewModelProvider(this).get(MessageViewModel.class);
        model.getListLiveData().observe(this, voiceMessages -> {
            Log.d(TAG, "消息变化了, 当前数量:"+voiceMessages.size());

            adapter.setVoiceMessageList(voiceMessages);
            adapter.notifyDataSetChanged();

            recyclerView.scrollToPosition(voiceMessages.size()-1);
        });

        Button sendBtn = findViewById(R.id.btn_send);
        sendBtn.setOnClickListener(v -> {
            VoiceMessage voiceMessage = new VoiceMessage();
            voiceMessage.messageId = UUID.randomUUID().toString();
            voiceMessage.senderName = "我是你大爷";
            voiceMessage.distance = 2.5f;
            voiceMessage.duration = 10;
            model.insert(voiceMessage);
        });

    }
}