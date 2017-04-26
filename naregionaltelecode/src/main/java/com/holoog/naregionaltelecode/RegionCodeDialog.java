package com.holoog.naregionaltelecode;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by O.G on 4/25/2017.
 */

class RegionCodeDialog {

    public static void openRegionCodeDialog(RegionCodePicker codePicker) {
        Context context=codePicker.getContext();
        final Dialog dialog = new Dialog(context);
        codePicker.refreshCustomMasterList();
        codePicker.refreshPreferredRegions();
        List<Region> masterRegions = Region.getCustomMasterRegionList(codePicker);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setContentView(R.layout.layout_picker_dialog);
        RecyclerView recyclerView_regionDialog = (RecyclerView) dialog.findViewById(R.id.recycler_regionDialog);
        final TextView textViewTitle=(TextView) dialog.findViewById(R.id.textView_title);
        textViewTitle.setText(codePicker.getDialogTitle());
        final EditText editText_search = (EditText) dialog.findViewById(R.id.editText_search);
        editText_search.setHint(codePicker.getSearchHintText());
        TextView textView_noResult = (TextView) dialog.findViewById(R.id.textView_noresult);
        textView_noResult.setText(codePicker.getNoResultFoundText());
        final RegionCodeAdapter cca = new RegionCodeAdapter(context, masterRegions, codePicker, editText_search, textView_noResult, dialog);
        if (!codePicker.isSelectionDialogShowSearch()) {
            Toast.makeText(context, "Found not to show search", Toast.LENGTH_SHORT).show();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) recyclerView_regionDialog.getLayoutParams();
            params.height = RecyclerView.LayoutParams.WRAP_CONTENT;
            recyclerView_regionDialog.setLayoutParams(params);
        }
        recyclerView_regionDialog.setLayoutManager(new LinearLayoutManager(context));
        recyclerView_regionDialog.setAdapter(cca);
        dialog.show();
    }
}
