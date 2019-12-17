package com.ourdreamit.blooddonationproject.ui.fragments;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.ui.adapters.NavigationDrawerAdapter;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.Information;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment {
    private NavigationDrawerAdapter adapter;
    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private View containerView;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);



        adapter = new NavigationDrawerAdapter(getActivity(),getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return layout;
    }

    public static List<Information> getData(){
        List<Information> data = new ArrayList<>();
        int[] icons = {R.drawable.ic_home,
                R.drawable.ic_menu_rateus,
        R.drawable.ic_menu_share,R.drawable.ic_menu_more,R.drawable.developer,R.drawable.ic_menu_exit};
        String[] titles = {"Home","Rate Us","Share App","Website","Developer","Exit"};
        for (int i = 0;i<titles.length;i++){
            Information current = new Information();
            current.iconId = icons[i];
            current.title = titles[i];
            data.add(current);
        }
        return data;
    }

    public void setUp(int fragment_id, DrawerLayout drawer, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragment_id);
        DataLoader.mDrawerLayout = drawer;
        mDrawerToggle =
                new ActionBarDrawerToggle(getActivity(),DataLoader.mDrawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);
                        getActivity().invalidateOptionsMenu();
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        super.onDrawerClosed(drawerView);
                        getActivity().invalidateOptionsMenu();
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        if(slideOffset < 0.6){
                            toolbar.setAlpha(1-slideOffset);
                        }
                    }
                };
        DataLoader.mDrawerLayout.addDrawerListener(mDrawerToggle);
        DataLoader.mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }
}
