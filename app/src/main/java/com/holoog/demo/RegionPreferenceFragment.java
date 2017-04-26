package com.holoog.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.holoog.naregionaltelecode.RegionCodePicker;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class RegionPreferenceFragment extends Fragment {

    EditText editTextRegionPreference;
    Button buttonSetRegionPreference;
    RegionCodePicker nrtc;
    Button buttonNext;

    public RegionPreferenceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_region_preference, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews();
        editTextWatcher();
        addClickListeners();
    }

    private void addClickListeners() {

        buttonSetRegionPreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String countryPreference;
                try {
                    countryPreference = editTextRegionPreference.getText().toString();
                    nrtc.setRegionPreference(countryPreference);
                    Toast.makeText(getActivity(), "Region preference list has been changed, click on NRTC to see them at top of list.", Toast.LENGTH_LONG).show();
                } catch (Exception ex) {

                }
            }
        });


        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).viewPager.setCurrentItem(((MainActivity) getActivity()).viewPager.getCurrentItem() + 1);
            }
        });
    }

    private void editTextWatcher() {
        editTextRegionPreference.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonSetRegionPreference.setText("set '" + s + "' as Region preference.");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void assignViews() {
        editTextRegionPreference = (EditText) getView().findViewById(R.id.editText_regionPreference);
        nrtc = (RegionCodePicker) getView().findViewById(R.id.nrtc);
        buttonSetRegionPreference = (Button) getView().findViewById(R.id.button_setRegionPreference);
        buttonNext = (Button) getView().findViewById(R.id.button_next);
    }
}
