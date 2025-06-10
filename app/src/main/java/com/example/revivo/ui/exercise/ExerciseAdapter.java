package com.example.revivo.ui.exercise;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.revivo.R;
import com.example.revivo.data.networking.response.Exercise;
import com.example.revivo.ui.exercise.detail.ExerciseDetailActivity;
import com.google.android.material.imageview.ShapeableImageView;

public class ExerciseAdapter extends ListAdapter<Exercise, ExerciseAdapter.ExerciseHolder> {
    private static final DiffUtil.ItemCallback<Exercise> DIFF_CALLBACK = new DiffUtil.ItemCallback<Exercise>() {
        @Override
        public boolean areItemsTheSame(@NonNull Exercise a, @NonNull Exercise b) {
            return a.getId().equals(b.getId());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Exercise a, @NonNull Exercise b) {
            return a.equals(b);
        }
    };
    private final String baseUrl = "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/";
    private OnItemClickListener listener;

    public ExerciseAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExerciseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public interface OnItemClickListener {
        void onItemClick(Exercise exercise);
    }

    class ExerciseHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory;
        ShapeableImageView ivWorkoutImage;
        ImageView btnPlay;

        public ExerciseHolder(@NonNull View itemView) {
            super(itemView);
            ivWorkoutImage = itemView.findViewById(R.id.ivWorkoutImage);
            tvTitle = itemView.findViewById(R.id.tvWorkoutTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            btnPlay = itemView.findViewById(R.id.btnPlay);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Exercise exercise = getItem(pos);

                    // Buka ExerciseDetailActivity
                    Context context = itemView.getContext();
                    Intent intent = new Intent(context, ExerciseDetailActivity.class);
                    intent.putExtra(ExerciseDetailActivity.EXTRA_EXERCISE_ID, exercise.getId());
                    intent.putExtra(ExerciseDetailActivity.EXTRA_EXERCISE_NAME, exercise.getName());
                    context.startActivity(intent);

                    // Tetap panggil listener jika ada
                    if (listener != null) {
                        listener.onItemClick(exercise);
                    }
                }
            });
        }

        void bind(Exercise exercise) {
            Context context = itemView.getContext();

            tvTitle.setText(capitalize(exercise.getName()));
            tvCategory.setText("Category: " + capitalize(exercise.getCategory()));

            // Load first image (if available)
            if (exercise.getImages() != null && !exercise.getImages().isEmpty()) {
                String imageUrl = baseUrl + exercise.getImages().get(0);
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(ivWorkoutImage);
            } else {
                ivWorkoutImage.setImageResource(R.drawable.placeholder);
            }

            // Handle play button click - juga buka detail
            btnPlay.setOnClickListener(v -> {
                Context context1 = itemView.getContext();
                Intent intent = new Intent(context1, ExerciseDetailActivity.class);
                intent.putExtra(ExerciseDetailActivity.EXTRA_EXERCISE_ID, exercise.getId());
                intent.putExtra(ExerciseDetailActivity.EXTRA_EXERCISE_NAME, exercise.getName());
                context1.startActivity(intent);

                if (listener != null) {
                    listener.onItemClick(exercise);
                }
            });
        }

        private String capitalize(String str) {
            if (str == null || str.isEmpty()) return "-";
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
    }
}