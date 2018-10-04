package com.cola.myrecyclerview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

/**
 * RecyclerView的封装Adapter.
 */

public class BaseAdapter extends RecyclerView.Adapter
    implements View.OnClickListener,View.OnLongClickListener {

    private List<String> mStrings;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private boolean openAnimationEnable = true;
    private int lastPosition = -1;
    private int duration = 500;
    private final int TYPE_FOOTER = 1;
    private final int TYPE_NORMAL = 2;
    private int load_more_status = 0;
    public static final int SHOW_FOOTER = 0;
    public static final int LOADING_MORE = 1;

    public BaseAdapter(Context context,List<String> list){
        this.mContext = context;
        this.mStrings = list;
    }

    public void removeData(int position){
        mStrings.remove(position);
        notifyItemRemoved(position);
    }

    public void setOpenItemAnimationEnable(boolean b){
        this.openAnimationEnable = b;
    }

    public void setItemAnimationDuration(int duration){
        this.duration = duration;
    }

    public void refreshDatas(List<String> newDatas){
        newDatas.addAll(mStrings);
        mStrings.clear();
        mStrings.addAll(newDatas);
        notifyDataSetChanged();
    }

    public void addMoreItems(List<String> loadMoreDatas){
        mStrings.addAll(loadMoreDatas);
        notifyDataSetChanged();
    }

    public void changeLoadMoreStatus(int status){
        load_more_status = status;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()){
            return TYPE_FOOTER;
        }else{
            return TYPE_NORMAL;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==TYPE_NORMAL){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item,parent,false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new MyViewHolder(view);
        }else {
            return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_recycler,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder){
            bindListView((MyViewHolder)holder,position);
        }else if (holder instanceof FooterViewHolder){
            bindFooter((FooterViewHolder)holder,position);
        }
    }

    private void bindFooter(FooterViewHolder holder, int position) {
        switch (load_more_status){
            case SHOW_FOOTER:
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.textView.setText("上拉加载更多");
                break;
            case LOADING_MORE:
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.textView.setText("loading...");
                break;
        }
    }

    private void bindListView(MyViewHolder holder, int position) {
        holder.mTextView.setText(mStrings.get(position));
        holder.itemView.setTag(position);
        if (openAnimationEnable){
            if (holder.getLayoutPosition() > lastPosition){
                Animator animator = ObjectAnimator.ofFloat(holder.itemView,"translationX",
                        holder.itemView.getRootView().getWidth(),0);
                animator.setInterpolator(new LinearInterpolator());
                animator.setDuration(duration);
                animator.start();
                lastPosition = holder.getLayoutPosition();
            }
        }
    }


    @Override
    public int getItemCount() {
        return mStrings.size();
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null){
            mOnItemClickListener.onItemClick(view, (int) view.getTag());
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (mOnItemClickListener != null){
            mOnItemClickListener.onItemLongClick(view, (int) view.getTag());
        }
        return true;
    }

    public interface OnItemClickListener{

        void onItemClick(View view,int position);

        void onItemLongClick(View view,int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView mTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.item_tv);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ProgressBar progressBar;

        public FooterViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_footer);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
