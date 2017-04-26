package com.holoog.naregionaltelecode;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by O.G on 4/25/2017.
 */

 class RegionCodeAdapter extends RecyclerView.Adapter<RegionCodeAdapter.RegionCodeViewHolder> {
    List<Region> filteredRegions = null, masterRegions = null;
    TextView textView_noResult;
    RegionCodePicker codePicker;
    LayoutInflater inflater;
    EditText editText_search;
    Dialog dialog;
    Context context;

    RegionCodeAdapter(Context context, List<Region> regions, RegionCodePicker codePicker, final EditText editText_search, TextView textView_noResult, Dialog dialog) {
        this.context = context;
        this.masterRegions = regions;
        this.codePicker = codePicker;
        this.dialog = dialog;
        this.textView_noResult = textView_noResult;
        this.editText_search = editText_search;
        this.inflater = LayoutInflater.from(context);
        this.filteredRegions = getFilteredRegions("");
        setSearchBar();
    }

    private void setSearchBar() {
        if (codePicker.isSelectionDialogShowSearch()) {
            setTextWatcher();
        } else {
            editText_search.setVisibility(View.GONE);
        }
    }

    /**
     * add textChangeListener, to apply new query each time editText get text changed.
     */
    private void setTextWatcher() {
        if (this.editText_search != null) {
            this.editText_search.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    applyQuery(s.toString());
                }
            });

            if(codePicker.isKeyboardAutoPopOnSearch()) {
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            }
        }
    }

    /**
     * Filter region list for given keyWord / query.
     * Lists all regions that contains @param query in region's name, name code or phone code.
     *
     * @param query : text to match against region name, name code or phone code
     */
    private void applyQuery(String query) {


        textView_noResult.setVisibility(View.GONE);
        query = query.toLowerCase();

        //if query started from "+" ignore it
        if (query.length() > 0 && query.charAt(0) == '+') {
            query=query.substring(1);
        }

        filteredRegions= getFilteredRegions(query);

        if (filteredRegions.size() == 0) {
            textView_noResult.setVisibility(View.VISIBLE);
        }
        notifyDataSetChanged();
    }

    private List<Region> getFilteredRegions(String query) {
        List<Region> tempRegionList = new ArrayList<Region>();
        if(codePicker.preferredRegions!=null && codePicker.preferredRegions.size()>0) {
            for (Region region : codePicker.preferredRegions) {
                if (region.isEligibleForQuery(query)) {
                    tempRegionList.add(region);
                }
            }

            if (tempRegionList.size() > 0) { //means at least one preferred country is added.
                Region divider = null;
                tempRegionList.add(divider);
            }
        }

        for (Region region : masterRegions) {
            if (region.isEligibleForQuery(query)) {
                tempRegionList.add(region);
            }
        }
        return tempRegionList;
    }

    @Override
    public RegionCodeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View rootView = inflater.inflate(R.layout.layout_recycler_region_tile, viewGroup, false);
        RegionCodeViewHolder viewHolder = new RegionCodeViewHolder(rootView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RegionCodeViewHolder regionCodeViewHolder, final int i) {
        regionCodeViewHolder.setRegion(filteredRegions.get(i));
        regionCodeViewHolder.getMainView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codePicker.setSelectedRegion(filteredRegions.get(i));
                if (view != null && filteredRegions.get(i)!=null) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    dialog.dismiss();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredRegions.size();
    }

    class RegionCodeViewHolder extends RecyclerView.ViewHolder {
        LinearLayout relativeLayout_main;
        TextView textView_name, textView_code;
        ImageView imageViewFlag;
        LinearLayout linearFlagHolder;
        View divider;
        public RegionCodeViewHolder(View itemView) {
            super(itemView);
            relativeLayout_main = (LinearLayout) itemView;
            textView_name = (TextView) relativeLayout_main.findViewById(R.id.textView_countryName);
            textView_code = (TextView) relativeLayout_main.findViewById(R.id.textView_code);
            linearFlagHolder = (LinearLayout) relativeLayout_main.findViewById(R.id.linear_flag_holder);
            divider = relativeLayout_main.findViewById(R.id.preferenceDivider);
        }

        public void setRegion(Region region) {
            if(region!=null) {
                divider.setVisibility(View.GONE);
                textView_name.setVisibility(View.VISIBLE);
                textView_code.setVisibility(View.VISIBLE);
                linearFlagHolder.setVisibility(View.VISIBLE);
                textView_name.setText(region.getName() + " (" + region.getNameCode().toUpperCase() + ")");
                textView_code.setText("+" + region.getPhoneCode());
            }else{
                divider.setVisibility(View.VISIBLE);
                textView_name.setVisibility(View.GONE);
                textView_code.setVisibility(View.GONE);
                linearFlagHolder.setVisibility(View.GONE);
            }
        }

        public LinearLayout getMainView() {
            return relativeLayout_main;
        }
    }

}
