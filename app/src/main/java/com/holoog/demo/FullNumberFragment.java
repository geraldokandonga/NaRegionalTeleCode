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

import com.holoog.naregionaltelecode.RegionCodePicker;


/**
 * A simple {@link Fragment} subclass.
 */
public class FullNumberFragment extends Fragment {


    EditText editTextLoadFullNumber,editTextLoadCarrierNumber,editTextGetFullNumber,editTextGetCarrierNumber;
    RegionCodePicker nrtcLoadNumber,nrtcGetNumber;
    Button buttonLoadNumber,buttonGetNumber,buttonGetNumberWithPlus;
    Button buttonNext;
    public FullNumberFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_full_number, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignView();
        registerCarrierEditText();
        setClickListener();
        addTextWatcher();
    }

    private void addTextWatcher() {
        editTextLoadFullNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonLoadNumber.setText("Load "+s+" to NRTC.");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setClickListener() {
        buttonLoadNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nrtcLoadNumber.setFullNumber(editTextLoadFullNumber.getText().toString());
            }
        });

        buttonGetNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextGetFullNumber.setText(nrtcGetNumber.getFullNumber());
            }
        });

        buttonGetNumberWithPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextGetFullNumber.setText(nrtcGetNumber.getFullNumberWithZero());
            }
        });

    }

    private void registerCarrierEditText() {
        nrtcLoadNumber.registerCarrierNumberEditText(editTextLoadCarrierNumber);
        nrtcGetNumber.registerCarrierNumberEditText(editTextGetCarrierNumber);
    }

    private void assignView() {
        //load number
        editTextLoadFullNumber=(EditText)getView().findViewById(R.id.editText_loadFullNumber);
        editTextLoadCarrierNumber=(EditText)getView().findViewById(R.id.editText_loadCarrierNumber);
        nrtcLoadNumber=(RegionCodePicker)getView().findViewById(R.id.nrtc_loadFullNumber);
        buttonLoadNumber=(Button)getView().findViewById(R.id.button_loadFullNumber);

        //get number
        editTextGetCarrierNumber=(EditText)getView().findViewById(R.id.editText_getCarrierNumber);
        editTextGetFullNumber=(EditText)getView().findViewById(R.id.editText_getFullNumber);
        buttonGetNumber=(Button)getView().findViewById(R.id.button_getFullNumber);
        buttonGetNumberWithPlus=(Button)getView().findViewById(R.id.button_getFullNumberWithPlus);
        nrtcGetNumber=(RegionCodePicker)getView().findViewById(R.id.nrtc_getFullNumber);

    }
}
