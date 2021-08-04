package com.example.resturant.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.bumptech.glide.Glide;
import com.example.resturant.Model.BestDealModel;
import com.example.resturant.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyBestDealAdapter extends LoopingPagerAdapter<BestDealModel> {

    @BindView(R.id.img_best_deal)
    ImageView img_best_deal;
    @BindView(R.id.txt_best_deal)
    TextView txt_best_deal;

    Unbinder unbinder;


    public MyBestDealAdapter(Context context, List<BestDealModel> itemList, boolean isInfinite) {
        super(context, itemList, isInfinite);
    }

    @Override
    protected void bindView(@NotNull View view, int i, int i1) {
        unbinder = ButterKnife.bind(this,view);
        Glide.with(view).load(itemList.get(i).getImage()).into(img_best_deal);
        txt_best_deal.setText(itemList.get(i).getName());

    }

    @Override
    protected @NotNull View inflateView(int i, @NotNull ViewGroup viewGroup, int i1) {
        return LayoutInflater.from(context).inflate(R.layout.layout_best_deal_item,viewGroup,false);
    }
}
