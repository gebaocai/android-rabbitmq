package cf.baocai.androidrabbitmq.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import org.apache.commons.lang3.SerializationUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import cf.baocai.androidrabbitmq.db.VMessageRepository;
import cf.baocai.androidrabbitmq.db.VoiceMessage;
import cf.baocai.androidrabbitmq.enums.ActionEnum;
import cf.baocai.androidrabbitmq.packet.VoiceMsgPacket;
import cf.baocai.androidrabbitmq.util.FileUtil;

public class RabbitMQService extends Service {
    private final static String TAG = RabbitMQService.class.getSimpleName();
    private final static String EXCHANGE_NAME = "exchange.chat";

    private boolean hasConsumer;
    private volatile Looper mServiceLooper;
    private volatile ServiceHandler mServiceHandler;

//    public RabbitMQService() {
//        super("RabbitMQService");
//        setIntentRedelivery(true);
//    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Intent intent = (Intent)msg.obj;

            Bundle bundle = intent.getExtras();
            if (null != bundle) {
                String action = bundle.getString("action", ActionEnum.EMPTY.getAction());
                if (ActionEnum.SEND == ActionEnum.fromString(action)) {
                    Log.d(TAG, "send msg");

                    sendMsg(bundle);
                } else if (ActionEnum.CONSUME == ActionEnum.fromString(action)) {
                    Log.d(TAG, "START_SERVER");
                    if (!hasConsumer) {
                    consumeMsg();
                    }
                }
            }

//            stopSelf(msg.arg1);
        }
    }

    /**
     * Connect rabbitmq server.
     */
    @Override
    public void onCreate()  {
        super.onCreate();
        HandlerThread thread = new HandlerThread("RabbitMQService");
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        Intent intent = new Intent(this, RabbitMQService.class);
        intent.putExtra("action", ActionEnum.CONSUME.getAction());
        startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Bundle bundle = intent.getExtras();
//        if (null != bundle) {
//            String action = bundle.getString("action", ActionEnum.EMPTY.getAction());
//            if (ActionEnum.SEND == ActionEnum.fromString(action)) {
//                Log.d(TAG, "send msg");
//
////                sendMsg(bundle);
//            } else if (ActionEnum.CONSUME == ActionEnum.fromString(action)) {
//                Log.d(TAG, "START_SERVER");
//                if (!hasConsumer) {
////                    consumeMsg();
//                }
//            }
//        }

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void consumeMsg() {
        try {
            //创建连接工程
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("kr.baocai.cf");
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.setVirtualHost("/");
            //创建连接
            Connection connection = factory.newConnection();
            //创建消息通道
            Channel channel = connection.createChannel();

            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, "");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                try {
                    byte[] data = delivery.getBody();
                    VoiceMsgPacket voiceMsgPacket = SerializationUtils.deserialize(data);

                    String mDir= FileUtil.dirPath(getApplicationContext());
                    File dir = new File(mDir);//创建文件夹
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    String fileName = UUID.randomUUID().toString()+".amr";//随机生成文件名
                    File file = new File(dir, fileName);//创建文件
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(voiceMsgPacket.voice);
                    fos.close();
                    String filePath = file.getAbsolutePath();

                    VoiceMessage voiceMessage = new VoiceMessage();
                    voiceMessage.filePath = filePath;
                    voiceMessage.duration = voiceMsgPacket.duration;

                    VMessageRepository vMessageRepository = new VMessageRepository(getApplicationContext());
                    vMessageRepository.insert(voiceMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
            hasConsumer = true;
        } catch (IOException ioException) {
            Toast.makeText(this, ioException.getMessage(), Toast.LENGTH_LONG).show();
        } catch (TimeoutException timeoutException) {
            Toast.makeText(this, timeoutException.getMessage(), Toast.LENGTH_LONG).show();
            stopSelf();
        }
    }

    private void sendMsg(Bundle bundle) {

        try {
            //创建连接工程
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("kr.baocai.cf");
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.setVirtualHost("/");


            //创建连接
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            String message = "info: Hello World!";

            float duration = bundle.getFloat("duration");
            String filePath = bundle.getString("filePath");

            VoiceMsgPacket voiceMsgPacket = new VoiceMsgPacket();
            voiceMsgPacket.messageId = UUID.randomUUID().toString();
            voiceMsgPacket.duration = duration;
            //这里是Voice的数据
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                byte[] body = Files.readAllBytes(Paths.get(filePath));
//                voiceMsgPacket.voice = body;
//            } else {
//
//            }

//            FileUtils

            byte[] body = cn.hutool.core.io.FileUtil.readBytes(filePath);
            voiceMsgPacket.voice = body;
            byte[] data = SerializationUtils.serialize(voiceMsgPacket);

            channel.basicPublish(EXCHANGE_NAME, "", null, data);
        } catch (IOException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        } catch (TimeoutException timeoutException) {
            Toast.makeText(this, timeoutException.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

//    @Override
//    protected void onHandleIntent(@Nullable Intent intent) {
//        Log.d(TAG, "onHandleIntent");
////        Bundle bundle = intent.getExtras();
////        if (null != bundle) {
////            String action = bundle.getString("action", ActionEnum.EMPTY.getAction());
////            if (ActionEnum.SEND == ActionEnum.fromString(action)) {
////                Log.d(TAG, "send msg");
////
////                sendMsg(bundle);
////            } else if (ActionEnum.CONSUME == ActionEnum.fromString(action)) {
////                Log.d(TAG, "START_SERVER");
////                consumeMsg();
////            }
////        }
//    }

}
