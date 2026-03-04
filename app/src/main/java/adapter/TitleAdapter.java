package adapter;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.diudiunews.NewsDetail;
import com.example.diudiunews.Newscontent;
import com.example.diudiunews.R;

import java.util.ArrayList;
import java.util.List;

public class TitleAdapter extends RecyclerView.Adapter<TitleAdapter.TitleViewHolder> {
    @NonNull
    //把布局对象存到列表
    public List<Newscontent> titles;
    public TitleAdapter(List<Newscontent> titles) {
        this.titles = titles != null ? titles : new ArrayList<>();
    }

    @NonNull
    public TitleAdapter.TitleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_page, parent, false);
        return new TitleViewHolder(view);
    }

    //数据设置到布局上
    @Override
    public void onBindViewHolder(@NonNull TitleViewHolder holder, int position) {
        Newscontent titleinfo = titles.get(position);
        holder.tvTitle.setText(titleinfo.title);
        holder.tvDate.setText(titleinfo.date);
        holder.tvTime.setText(titleinfo.time);
        holder.tvPublisher.setText(titleinfo.publisher);
        if (titleinfo.imageUrl != null && !titleinfo.imageUrl.isEmpty()) {
            String imageurl = titleinfo.imageUrl;
            if (imageurl.startsWith("[") && imageurl.endsWith("]")) {
                imageurl = imageurl.substring(1, imageurl.length() - 1);
            }
            Log.d("zion", "222 imageurl:" + imageurl);
            Glide.with(holder.itemView.getContext()).load(imageurl).into(holder.ivImage);
        } else {
            // 如果没有图片，隐藏 ImageView
            holder.ivImage.setVisibility(View.GONE);
        }
        if (titleinfo.isRead) {
            holder.tvTitle.setTextColor(Color.GRAY);
        } else {
            holder.tvTitle.setTextColor(Color.BLACK); // 或其他默认颜色
        }
        holder.itemView.setOnClickListener(v -> {
            titleinfo.isRead=true;
            notifyItemChanged(position);
            Intent intent = new Intent(v.getContext(), NewsDetail.class);
            intent.putExtra("title", titleinfo.title);
            intent.putExtra("publisher", titleinfo.publisher);
            intent.putExtra("date", titleinfo.date);
            intent.putExtra("time", titleinfo.time);
            intent.putExtra("content", titleinfo.content);
            intent.putExtra("videourl", titleinfo.videoUrl!= null ? titleinfo.videoUrl : "");
            intent.putExtra("imageurl", titleinfo.imageUrl!= null ? titleinfo.imageUrl : "");
            v.getContext().startActivity(intent);
        });
        holder.itemView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                float translationY = scrollY * 0.5f; // 视差系数
                holder.ivImage.setTranslationY(translationY);
            }
        });
    }

    //列表的项数
    @Override
    public int getItemCount() {
        return titles.size();
    }

    public static class TitleViewHolder extends RecyclerView.ViewHolder {
        public TitleViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        TextView tvTitle = itemView.findViewById(R.id.title);
        TextView tvPublisher = itemView.findViewById(R.id.publisher);
        ImageView ivImage = itemView.findViewById(R.id.photo);
        TextView tvDate = itemView.findViewById(R.id.date);
        TextView tvTime = itemView.findViewById(R.id.time);
    }

    public void updateData(List<Newscontent> newTitles) {
        this.titles.clear();
        this.titles.addAll(newTitles);
        notifyDataSetChanged();
    }
}
