package com.vmh.manhhung.parkinglotsone;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllUsersFragment extends Fragment {

   private RecyclerView mUsersListView;
   private List<Customer> usersList;
   private CustomerAdapter customerAdapter;

   private DatabaseReference mData;

    public AllUsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_all_users, container, false);
        mData = FirebaseDatabase.getInstance().getReference();
        mUsersListView=(RecyclerView)view.findViewById(R.id.mUsersListView);
        usersList=new ArrayList<>();

        customerAdapter=new CustomerAdapter(container.getContext(),usersList);
        mUsersListView.setHasFixedSize(true);


        //sua neu loi

        mUsersListView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mUsersListView.setAdapter(customerAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mData.child("ThongTin").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String user_id=dataSnapshot.getKey();
                Customer customer=dataSnapshot.getValue(Customer.class).withId(user_id);
                usersList.add(customer);
                customerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
