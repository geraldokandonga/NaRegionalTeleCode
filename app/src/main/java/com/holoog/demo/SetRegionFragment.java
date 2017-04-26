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
public class SetRegionFragment extends Fragment {

    EditText editTextCode,editTextNameCode;
    Button buttonSetCode,buttonSetNameCode;
    RegionCodePicker nrtc;
    Button buttonNext;
    public SetRegionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_region, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews();
        editTextWatcher();
        addClickListeners();
    }

    private void addClickListeners() {
        buttonSetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int code=-1;
                try{
                    code=Integer.parseInt(editTextCode.getText().toString());
                    nrtc.setRegionForPhoneCode(code);
                }catch (Exception ex){
                    Toast.makeText(getActivity(),"Invalid number format",Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonSetNameCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String code=editTextNameCode.getText().toString();
                    nrtc.setRegionForNameCode(code);
                }catch (Exception ex){
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).viewPager.setCurrentItem(((MainActivity) getActivity()).viewPager.getCurrentItem()+1);
            }
        });
    }

    private void editTextWatcher() {
        editTextCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonSetCode.setText("Set region with code "+s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextNameCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonSetNameCode.setText("Set region with name code '"+s+"'");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        nrtc.setOnRegionChangeListener(new RegionCodePicker.OnRegionChangeListener() {
            @Override
            public void onRegionSelected() {
                Toast.makeText(getContext(), "This is from OnRegionChangeListener. \n Region updated to " + nrtc.getSelectedRegionName() + "(" + nrtc.getSelectedRegionCodeWithPlus() + ")", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignViews() {
        editTextNameCode=(EditText)getView().findViewById(R.id.editText_regionNameCode);
        editTextCode=(EditText)getView().findViewById(R.id.editText_regionCode);
        nrtc=(RegionCodePicker)getView().findViewById(R.id.nrtc);
        buttonSetCode=(Button) getView().findViewById(R.id.button_setRegion);
        buttonSetNameCode=(Button) getView().findViewById(R.id.button_setRegionNameCode);
        buttonNext=(Button)getView().findViewById(R.id.button_next);
    }

}
