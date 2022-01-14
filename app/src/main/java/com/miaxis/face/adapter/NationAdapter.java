package com.miaxis.face.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miaxis.face.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NationAdapter<T> extends RecyclerView.Adapter<NationAdapter.MyViewHolder> {

    private List<T> dataList;
    private int selectNationPosition;

    private LayoutInflater layoutInflater;
    private OnItemClickListener mOnItemClickListener;

    public NationAdapter(Context context, List<T> dataList) {
        this.dataList = dataList;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_nation, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final NationAdapter.MyViewHolder holder, int position) {
        String nation = (String) dataList.get(position);
        holder.tvNation.setText(nation);
        if (position == this.selectNationPosition) {
            holder.tvNation.setBackground(layoutInflater.getContext().getResources().getDrawable(R.drawable.green_check_bg));
        } else {
            holder.tvNation.setBackground(null);
        }
        holder.itemView.setOnClickListener(v -> mOnItemClickListener.onItemClick(holder.itemView, holder.getLayoutPosition()));
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public T getData(int position) {
        return dataList.get(position);
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void selectData(int position) {
        int cache = selectNationPosition;
        selectNationPosition = position;
        notifyItemChanged(cache);
        notifyItemChanged(selectNationPosition);
    }

    public T getSelecNation() {
        return dataList.get(selectNationPosition);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_nation)
        TextView tvNation;
        @BindView(R.id.rl_nation)
        RelativeLayout rlNation;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}