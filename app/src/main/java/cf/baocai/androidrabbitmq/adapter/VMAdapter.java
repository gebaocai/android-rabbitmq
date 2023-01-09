package cf.baocai.androidrabbitmq.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
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
import cf.baocai.androidrabbitmq.util.MediaManager;

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
                .inflate(R.layout.item_recorder, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VoiceMessage voiceMessage = voiceMessageList.get(position);
        holder.seconds.setText(Math.round(voiceMessage.duration)+"\"");

        ViewGroup.LayoutParams lp = holder.length.getLayoutParams();
        lp.width = (int)(mMinItemWidth+(mMaxItemWidth/60f*voiceMessage.duration));



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            private View mAnimView;
            @Override
            public void onClick(View v) {
                if(mAnimView!=null)
                {
                    mAnimView.setBackgroundResource(R.drawable.adj);
                    mAnimView=null;
                }
                //play video
                mAnimView=holder.itemView.findViewById(R.id.id_recorder_anim);
                mAnimView.setBackgroundResource(R.drawable.play_anim);
                AnimationDrawable anim=(AnimationDrawable)holder.mAnimView.getBackground();
                anim.start();
                //play audio
                MediaManager.playSound(voiceMessage.filePath,new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        holder.mAnimView.setBackgroundResource(R.drawable.adj);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return voiceMessageList.size();
    }

    public void setVoiceMessageList(List<VoiceMessage> voiceMessageList) {
        this.voiceMessageList = voiceMessageList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView seconds;
        private View length;
        private View mAnimView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            seconds = itemView.findViewById(R.id.id_recorder_time);
            length = itemView.findViewById(R.id.id_recorder_length);
            mAnimView = itemView.findViewById(R.id.id_recorder_anim);
        }
    }
}
