package cf.baocai.androidrabbitmq;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cf.baocai.androidrabbitmq.db.VoiceMessage;

public class VMAdapter extends RecyclerView.Adapter<VMAdapter.ViewHolder> {
    private List<VoiceMessage> voiceMessageList = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_row_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VoiceMessage voiceMessage = voiceMessageList.get(position);
        holder.tv_distance.setText("距离我 "+voiceMessage.distance+" 米");
        holder.tv_duration.setText(voiceMessage.duration+"s");
        holder.tv_user_name.setText(voiceMessage.senderName);
    }

    @Override
    public int getItemCount() {
        return voiceMessageList.size();
    }

    public void setVoiceMessageList(List<VoiceMessage> voiceMessageList) {
        this.voiceMessageList = voiceMessageList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_duration, tv_user_name, tv_distance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_duration = itemView.findViewById(R.id.tv_duration);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            tv_distance = itemView.findViewById(R.id.tv_distance);
        }
    }
}
