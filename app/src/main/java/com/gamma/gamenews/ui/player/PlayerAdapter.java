package com.gamma.gamenews.ui.player;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gamma.gamenews.R;
import com.gamma.gamenews.data.database.Player;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private Context context;
    private List<Player> playerArray;

    public PlayerAdapter(Context context, List<Player> playerArray) {
        this.context = context;
        this.playerArray = playerArray;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_news,parent,false);
        return (new PlayerViewHolder(v));
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        final Player player = playerArray.get(position);
        if(player.getName() != null)
            holder.txtName.setText(player.getName());
        else
            holder.txtName.setText("-----");

        if(player.getAvatar() != null){
            Picasso.get().load(player.getAvatar())
                    .error(R.drawable.ic_player)
                    .placeholder(R.drawable.ic_player)
                    .into(holder.imgPlayer);
        } else
            holder.imgPlayer.setImageResource(R.drawable.no_image);
    }

    @Override
    public int getItemCount() {
        return playerArray != null?playerArray.size():0;
    }

    class PlayerViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout item;
        TextView txtName;
        ImageView imgPlayer;

        public PlayerViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item_cardview);
            txtName = itemView.findViewById(R.id.txt_name);
            imgPlayer = itemView.findViewById(R.id.img_picture);
        }
    }
}
