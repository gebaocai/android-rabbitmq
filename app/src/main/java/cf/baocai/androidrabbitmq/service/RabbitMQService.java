package cf.baocai.androidrabbitmq.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import cf.baocai.androidrabbitmq.ActionEnum;

public class RabbitMQService extends IntentService {
    private final static String TAG = RabbitMQService.class.getSimpleName();
    private final static String EXCHANGE_NAME = "exchange.chat";

    private Connection connection;
    private Channel channel;

    public RabbitMQService() {
        super("RabbitMQService");
    }

    /**
     * Connect rabbitmq server.
     */
    @Override
    public void onCreate()  {
        super.onCreate();
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
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        } catch (IOException ioException) {
            Toast.makeText(this, ioException.getMessage(), Toast.LENGTH_LONG).show();
        } catch (TimeoutException timeoutException) {
            Toast.makeText(this, timeoutException.getMessage(), Toast.LENGTH_LONG).show();
            stopSelf();
        }
    }

    private void sendMsg() {

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

            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
        } catch (IOException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        } catch (TimeoutException timeoutException) {
            Toast.makeText(this, timeoutException.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent");
        Bundle bundle = intent.getExtras();
        if (null != bundle) {
            String action = bundle.getString("action", ActionEnum.EMPTY.getAction());
            if (ActionEnum.SEND == ActionEnum.fromString(action)) {
                Log.d(TAG, "send msg");
                sendMsg();
            } else if (ActionEnum.CONSUME == ActionEnum.fromString(action)) {
                Log.d(TAG, "START_SERVER");
                consumeMsg();
            }
        }
    }

}
