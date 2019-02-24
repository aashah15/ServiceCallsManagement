package com.beans.coder.servicecallsmanagement;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {

    private Context context;
    private List<ServiceCall> serviceCalls;
    private OnItemClickListener mListener;
    private static ArrayList<CardView> cardViewArrayList = new ArrayList<>();


    public ResultAdapter(Context context, List<ServiceCall> serviceCalls) {
        this.context = context;
        this.serviceCalls = serviceCalls;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.result_item,viewGroup,false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int i) {

        ServiceCall serviceCall = serviceCalls.get(i);
        holder.textViewCallNo.setText(serviceCall.getCallNo());
        holder.textViewDate.setText(serviceCall.getDate());
        holder.textViewEquipment.setText(serviceCall.getEquipment());
        holder.textViewModelNo.setText(serviceCall.getModelNo());
        holder.textViewIssue.setText(serviceCall.getIssue());
        holder.textViewActionTaken.setText(serviceCall.getActionTaken());
        holder.textViewResult.setText(serviceCall.getResult());
        holder.textViewRemarks.setText(serviceCall.getRemarks());
        Picasso.get().load(serviceCall.getImageUri())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageView);

        addCardView(holder.cardView);
    }

    @Override
    public int getItemCount() {
        return serviceCalls.size();
    }
    private static void addCardView(CardView cardView)
    {
        cardViewArrayList.add(cardView);
    }

    public static ArrayList<CardView> getCardViewList()
    {
        return cardViewArrayList;
    }
    public class ResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
    View.OnCreateContextMenuListener,MenuItem.OnMenuItemClickListener {

        public TextView textViewCallNo;
        public TextView textViewDate;
        public TextView textViewEquipment;
        public TextView textViewModelNo;
        public TextView textViewIssue;
        public TextView textViewActionTaken;
        public TextView textViewResult;
        public TextView textViewRemarks;
        public ImageView imageView;
        public CardView cardView;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewCallNo = itemView.findViewById(R.id.text_view_call_no);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewEquipment = itemView.findViewById(R.id.text_view_equipment);
            textViewModelNo = itemView.findViewById(R.id.text_view_model_no);
            textViewIssue =itemView.findViewById(R.id.text_view_issue);
            textViewActionTaken = itemView.findViewById(R.id.text_view_action_taken);
            textViewResult = itemView.findViewById(R.id.text_view_result);
            textViewRemarks = itemView.findViewById(R.id.text_view_remarks);
            imageView = itemView.findViewById(R.id.image_view);
            cardView = itemView.findViewById(R.id.card_view_root);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem Update = menu.add(Menu.NONE,1,1,"Update");
            MenuItem Delete = menu.add(Menu.NONE,2,2,"Delete");
            MenuItem CreatePDF = menu.add(Menu.NONE,3,3,"CreatePDF");

            Update.setOnMenuItemClickListener(this);
            Delete.setOnMenuItemClickListener(this);
            CreatePDF.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(mListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    switch (item.getItemId()){
                        case 1:
                            mListener.onUpdateClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                        case 3:
                            mListener.onCreatePDFClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }
    public interface OnItemClickListener{
    void onUpdateClick(int position);

    void onDeleteClick(int position);

    void onCreatePDFClick(int position);
}
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
}
