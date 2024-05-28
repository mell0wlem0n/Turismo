package com.example.turismo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_TEXT = 1;
    private static final int VIEW_TYPE_IMAGE = 2;

    private List<Message> messageList;
    private FragmentActivity activity;

    public MessageAdapter(List<Message> messageList, FragmentActivity activity) {
        this.messageList = messageList;
        this.activity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getImageUrl() != null) {
            return VIEW_TYPE_IMAGE;
        } else {
            return VIEW_TYPE_TEXT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_TEXT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_message, parent, false);
            return new TextMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_message, parent, false);
            return new ImageMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_TEXT) {
            ((TextMessageViewHolder) holder).bind(message);
        } else {
            ((ImageMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class TextMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView senderTextView;
        ShapeableImageView profileImageView;

        TextMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            senderTextView = itemView.findViewById(R.id.senderTextView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
        }

        void bind(Message message) {
            senderTextView.setText(message.getSenderName());
            messageTextView.setText(message.getText());
            if (message.getProfileImageUrl() != null) {
                Glide.with(itemView.getContext()).load(message.getProfileImageUrl()).into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.img); // default image
            }
        }
    }

    class ImageMessageViewHolder extends RecyclerView.ViewHolder {
        TextView senderTextView;
        ShapeableImageView profileImageView;
        ImageView messageImageView;

        ImageMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderTextView = itemView.findViewById(R.id.senderTextView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            messageImageView = itemView.findViewById(R.id.messageImageView);
        }

        void bind(Message message) {
            senderTextView.setText(message.getSenderName());
            if (message.getProfileImageUrl() != null) {
                Glide.with(itemView.getContext()).load(message.getProfileImageUrl()).into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.img); // default image
            }
            Glide.with(itemView.getContext()).load(message.getImageUrl()).into(messageImageView);

            messageImageView.setOnClickListener(v -> {
                ImageViewerFragment.newInstance(message.getImageUrl())
                        .show(activity.getSupportFragmentManager(), "image_viewer");
            });
        }
    }
}
