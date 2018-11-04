package com.vmh.manhhung.parkinglotsone;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.time.Instant;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {

    private List<Customer> usersList;
    private Context context;

    public CustomerAdapter(Context context,List<Customer> usersList)
    {
        this.usersList=usersList;
        this.context=context;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.line_layout_listview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.user_name_view.setText(usersList.get(position).getFullname());
        holder.user_email_view.setText(usersList.get(position).getEmail());
        holder.user_phone_view.setText(usersList.get(position).getPhoneNumber());
        holder.user_birthday_view.setText(usersList.get(position).getBirthDay());
        Picasso.get().load(usersList.get(position).getUri()).into(holder.user_image_view);

        final String user_id=usersList.get(position).userId;

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ManageUsersActivity.class);
                intent.putExtra("user_id",user_id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private CircleImageView user_image_view;
        private TextView user_name_view;
        private TextView user_email_view;
        private TextView user_phone_view;
        private TextView user_birthday_view;
        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;

            user_image_view     =(CircleImageView)mView.findViewById(R.id.imgHinh);
            user_email_view     =(TextView)mView.findViewById(R.id.txtEmail);
            user_name_view      =(TextView)mView.findViewById(R.id.txtFullName);
            user_phone_view     =(TextView)mView.findViewById(R.id.txtPhone);
            user_birthday_view  =(TextView)mView.findViewById(R.id.txtBirhday);
        }
    }

}
