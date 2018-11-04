package com.vmh.manhhung.parkinglotsone;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ManageAccountActivity extends AppCompatActivity {
    private TextView mProfileLabel;
    private TextView mUsersLabel;

    private ViewPager mMainPager;
    private PagerViewAdapter mPagerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);
        AnhXa();
        mPagerViewAdapter =new PagerViewAdapter(getSupportFragmentManager());
        mMainPager.setAdapter(mPagerViewAdapter);

        mProfileLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainPager.setCurrentItem(0);
            }
        });

        mUsersLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainPager.setCurrentItem(1);
            }
        });


        mMainPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onPageSelected(int i) {
                changeTabs(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void changeTabs(int i) {
        if(i==0)
        {
            mProfileLabel.setTextColor(getColor(R.color.textTabBright));
            mProfileLabel.setTextSize(22);

            mUsersLabel.setTextColor(getColor(R.color.textTabLight));
            mUsersLabel.setTextSize(16);


        }
        if(i==1)
        {
            mProfileLabel.setTextColor(getColor(R.color.textTabLight));
            mProfileLabel.setTextSize(16);

            mUsersLabel.setTextColor(getColor(R.color.textTabBright));
            mUsersLabel.setTextSize(22);

        }
    }

    private void AnhXa() {
        mProfileLabel=(TextView)findViewById(R.id.profileLabel);
        mUsersLabel=(TextView)findViewById(R.id.usersLabel);
        mMainPager=(ViewPager)findViewById(R.id.mainPager);
    }
}
