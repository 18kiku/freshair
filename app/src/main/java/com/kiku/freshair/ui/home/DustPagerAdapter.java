package com.kiku.freshair.ui.home;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DustPagerAdapter extends FragmentStateAdapter {
    private ArrayList<Pair<Fragment, String>> fragmentList;

    public DustPagerAdapter(@NonNull @NotNull FragmentActivity fragmentActivity, ArrayList<Pair<Fragment, String>> fragmentList) {
        super(fragmentActivity);
        this.fragmentList = fragmentList;
    }


    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position).first;
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
