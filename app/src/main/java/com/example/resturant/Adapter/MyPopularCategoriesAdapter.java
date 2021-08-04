package com.example.resturant.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.resturant.Model.PopularCategoryModel;
import com.example.resturant.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyPopularCategoriesAdapter extends RecyclerView.Adapter<MyPopularCategoriesAdapter.MyViewHolder> {

    Context context;
    List<PopularCategoryModel> popularCategoryModelsList;

    public MyPopularCategoriesAdapter(Context context, List<PopularCategoryModel> popularCategoryModelsList) {
        this.context = context;
        this.popularCategoryModelsList = popularCategoryModelsList;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
        .inflate(R.layout.layout_popular_categories_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyPopularCategoriesAdapter.MyViewHolder holder, int position) {
        Glide.with(context).load(popularCategoryModelsList.get(position).getImage())
                .into(holder.category_image);
        holder.txt_category_name.setText(popularCategoryModelsList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return popularCategoryModelsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        Unbinder unbinder;

        @BindView(R.id.txt_category_name)
        TextView txt_category_name;
        @BindView(R.id.category_image)
        CircleImageView category_image;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }
    }
}
