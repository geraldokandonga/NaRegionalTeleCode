package com.holoog.demo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.holoog.naregionaltelecode.RegionCodePicker;


/**
 * A simple {@link Fragment} subclass.
 */
public class GetRegionFragment extends Fragment {

    TextView textViewCountryName,textViewCountryCode,textViewCountryNameCode;
    Button buttonReadCountry;
    RegionCodePicker nrtc;
    Button buttonNext;
    public GetRegionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_get_region, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews();
        setClickListener();
    }

    private void setClickListener() {
        buttonReadCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewCountryName.setText(nrtc.getSelectedRegionName());
                textViewCountryCode.setText(nrtc.getSelectedRegionCode());
                textViewCountryNameCode.setText(nrtc.getSelectedRegionNameCode());
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).viewPager.setCurrentItem(((MainActivity) getActivity()).viewPager.getCurrentItem() + 1);
            }
        });
    }

    private void assignViews() {
        nrtc=(RegionCodePicker)getView().findViewById(R.id.nrtc);
        textViewCountryCode=(TextView)getView().findViewById(R.id.textView_regionCode);
        textViewCountryName=(TextView)getView().findViewById(R.id.textView_regionName);
        textViewCountryNameCode=(TextView)getView().findViewById(R.id.textView_regionNameCode);
        buttonReadCountry=(Button)getView().findViewById(R.id.button_readRegion);
        buttonNext=(Button)getView().findViewById(R.id.button_next);
    }
}
