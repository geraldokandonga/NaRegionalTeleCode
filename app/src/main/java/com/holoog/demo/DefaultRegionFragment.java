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
 */
public class DefaultRegionFragment extends Fragment {


    EditText editTextDefaultPhoneCode, editTextDefaultNameCode;
    Button buttonSetNewDefaultPhoneCode, buttonSetNewDefaultNameCode, buttonResetToDefault;
    RegionCodePicker nrtc;
    Button buttonNext;

    public DefaultRegionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_default_region, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews();
        editTextWatcher();
        addClickListeners();
    }

    private void addClickListeners() {
        buttonSetNewDefaultPhoneCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int code = -1;
                try {
                    code = Integer.parseInt(editTextDefaultPhoneCode.getText().toString());
                    nrtc.setDefaultRegionUsingPhoneCode(code);
                    Toast.makeText(getActivity(), "Now default region is " + nrtc.getDefaultRegionName() + " with phone code " + nrtc.getDefaultRegionCode(), Toast.LENGTH_LONG).show();
                } catch (Exception ex) {
                    Toast.makeText(getActivity(), "Invalid number format", Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonSetNewDefaultNameCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameCode;
                try {
                    nameCode = editTextDefaultNameCode.getText().toString();
                    nrtc.setDefaultRegionUsingNameCode(nameCode);
                    Toast.makeText(getActivity(), "Now default region is " + nrtc.getDefaultRegionName() + " with phone code " + nrtc.getDefaultRegionCode(), Toast.LENGTH_LONG).show();
                } catch (Exception ex) {

                }
            }
        });

        buttonResetToDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nrtc.resetToDefaultRegion();
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
        editTextDefaultPhoneCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonSetNewDefaultPhoneCode.setText("set " + s + " as Default Region Code");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextDefaultNameCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonSetNewDefaultNameCode.setText("set '" + s + "' as Default Region Name Code");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void assignViews() {
        editTextDefaultPhoneCode = (EditText) getView().findViewById(R.id.editText_defaultCode);
        nrtc = (RegionCodePicker) getView().findViewById(R.id.nrtc);
        buttonSetNewDefaultPhoneCode = (Button) getView().findViewById(R.id.button_setDefaultCode);
        buttonResetToDefault = (Button) getView().findViewById(R.id.button_resetToDefault);

        editTextDefaultNameCode = (EditText) getView().findViewById(R.id.editText_defaultNameCode);
        buttonSetNewDefaultNameCode = (Button) getView().findViewById(R.id.button_setDefaultNameCode);

        buttonNext = (Button) getView().findViewById(R.id.button_next);
    }
}
