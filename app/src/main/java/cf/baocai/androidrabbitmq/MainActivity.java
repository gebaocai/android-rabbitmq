package cf.baocai.androidrabbitmq;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import cf.baocai.androidrabbitmq.adapter.VMAdapter;
import cf.baocai.androidrabbitmq.service.RabbitMQService;
import cf.baocai.androidrabbitmq.viewmodel.MessageViewModel;

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

        // 启动RabbitMQ服务
        Intent intent = new Intent(this, RabbitMQService.class);
        intent.putExtra("action", ActionEnum.CONSUME.getAction());
        startService(intent);

        model = new ViewModelProvider(this).get(MessageViewModel.class);
        model.getListLiveData().observe(this, voiceMessages -> {
            Log.d(TAG, "消息变化了, 当前数量:"+voiceMessages.size());

            adapter.setVoiceMessageList(voiceMessages);
            adapter.notifyDataSetChanged();

            recyclerView.scrollToPosition(voiceMessages.size()-1);
        });

        Button sendBtn = findViewById(R.id.btn_send);
        sendBtn.setOnClickListener(v -> {
//            VoiceMessage voiceMessage = new VoiceMessage();
//            voiceMessage.messageId = UUID.randomUUID().toString();
//            voiceMessage.senderName = RandomUtil.randomString(3);
//            voiceMessage.distance = new Random().nextFloat();
//            voiceMessage.duration = RandomUtil.randomInt(60);
//            model.insert(voiceMessage);
            Intent sendIntent = new Intent(this, RabbitMQService.class);
            sendIntent.putExtra("action", ActionEnum.SEND.getAction());

            startService(sendIntent);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear://清空数据
                model.clear();
            default:
                break;
        }
        return true;
    }
}