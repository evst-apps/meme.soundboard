package com.evstapps.memesoundboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.Window;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    public ViewPager2 viewPager2;
    public AdManager adManager;
    public EVSTRingtoneManager evstRingtoneManager;
    public EVSTMediaPlayer evstMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_main);

        adManager = new AdManager(this);
        evstRingtoneManager = new EVSTRingtoneManager(this);
        evstMediaPlayer = new EVSTMediaPlayer(this);

        viewPager2 = findViewById(R.id.pager);
        ViewPager2Adapter viewPager2Adapter = new ViewPager2Adapter(this);
        viewPager2.setAdapter(viewPager2Adapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setCustomView(App.assetFolders.get(position).tabView)).attach();
        tabLayout.selectTab(tabLayout.getTabAt(1));
    }

}