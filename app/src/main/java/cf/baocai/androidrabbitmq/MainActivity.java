package cf.baocai.androidrabbitmq;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.UUID;

import cf.baocai.androidrabbitmq.adapter.VMAdapter;
import cf.baocai.androidrabbitmq.db.VoiceMessage;
import cf.baocai.androidrabbitmq.enums.ActionEnum;
import cf.baocai.androidrabbitmq.packet.VoiceMsgPacket;
import cf.baocai.androidrabbitmq.service.RabbitMQService;
import cf.baocai.androidrabbitmq.view.AudioRecorderButton;
import cf.baocai.androidrabbitmq.view.model.MessageViewModel;
import cn.hutool.core.bean.BeanUtil;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private LinearLayoutManager linearLayoutManager;
    private MessageViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        checkPermission();

        RecyclerView recyclerView = findViewById(R.id.message_list);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        VMAdapter adapter = new VMAdapter();
        recyclerView.setAdapter(adapter);

        // 启动RabbitMQ服务
        Intent intent = new Intent(this, RabbitMQService.class);
//        intent.putExtra("action", ActionEnum.CONSUME.getAction());
        startService(intent);

        model = new ViewModelProvider(this).get(MessageViewModel.class);
        model.getListLiveData().observe(this, voiceMessages -> {
            Log.d(TAG, "消息变化了, 当前数量:"+voiceMessages.size());

            adapter.setVoiceMessageList(voiceMessages);
            adapter.notifyDataSetChanged();

            recyclerView.scrollToPosition(voiceMessages.size()-1);
        });

        AudioRecorderButton sendBtn = findViewById(R.id.btn_send);
        sendBtn.setAudioFinishRecorderListener(new AudioRecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                Log.d(TAG, "时长："+seconds);

                VoiceMessage voiceMessage = new VoiceMessage();
                voiceMessage.received = false;
                voiceMessage.filePath = filePath;
                voiceMessage.duration = seconds;
                voiceMessage.messageId = UUID.randomUUID().toString();
                model.insert(voiceMessage);

                Intent sendIntent = new Intent(getApplicationContext(), RabbitMQService.class);
                sendIntent.putExtra("action", ActionEnum.SEND.getAction());
                sendIntent.putExtra("msg", voiceMessage);
                startService(sendIntent);
            }
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

    /**
     * 权限申请
     */
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, 200);
                    return;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 200) {
            checkPermission();
        }
    }
}