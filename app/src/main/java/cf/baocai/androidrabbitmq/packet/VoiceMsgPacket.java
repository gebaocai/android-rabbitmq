package cf.baocai.androidrabbitmq.packet;

import java.io.Serializable;

public class VoiceMsgPacket implements Serializable {
    public String messageId;
    public String senderName;

    public float distance;
    public float duration;
    public byte[] voice;
}
