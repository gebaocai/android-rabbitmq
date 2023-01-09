package cf.baocai.androidrabbitmq.adapter;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cf.baocai.androidrabbitmq.R;
import cf.baocai.androidrabbitmq.db.VoiceMessage;

public class VMAdapter extends RecyclerView.Adapter<VMAdapter.ViewHolder> {
    private List<VoiceMessage> voiceMessageList = new ArrayList<>();
    private int mMinItemWidth;
    private int mMaxItemWidth;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        WindowManager wm= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            wm = (WindowManager)parent.getContext().getSystemService(parent.getContext().WINDOW_SERVICE);
            DisplayMetrics outMetrics=new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);

            mMaxItemWidth=(int)(outMetrics.widthPixels*0.7f);
            mMinItemWidth=(int)(outMetrics.widthPixels*0.15f);
        }


        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_row_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VoiceMessage voiceMessage = voiceMessageList.get(position);
        holder.tv_distance.setText("距离我 "+voiceMessage.distance+" 米");
        holder.tv_duration.setText(voiceMessage.duration+"s");
        holder.tv_user_name.setText(voiceMessage.senderName+":");

        ViewGroup.LayoutParams tv_duration = holder.tv_duration.getLayoutParams();
        tv_duration.width = (int)(mMinItemWidth+(mMaxItemWidth/60f*voiceMessage.duration));
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
