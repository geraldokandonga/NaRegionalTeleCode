package com.holoog.naregionaltelecode;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
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

public class RegionCodePicker extends RelativeLayout{

    //this name should match value of enum <attr name="language" format="enum"> from attrs
    final static int LANGUAGE_ENGLISH = 1;
    static String TAG = "NRTC";
    static String BUNDLE_SELECTED_CODE = "selectedCode";
    static int LIB_DEFAULT_REGION_CODE = 91;
    int defaultRegionCode;
    String defaultRegionNameCode;
    Context context;
    View holderView;
    LayoutInflater mInflater;
    TextView textView_selectedRegion;
    EditText editText_registeredCarrierNumber;
    RelativeLayout holder;
    ImageView imageViewArrow;
    ImageView imageViewFlag;
    LinearLayout linearFlagHolder;
    Region selectedRegion;
    Region defaultRegion;
    RelativeLayout relativeClickConsumer;
    RegionCodePicker codePicker;
    boolean hideNameCode = false;
    boolean showFlag = true;
    boolean showFullName = false;
    boolean useFullName = false;
    boolean selectionDialogShowSearch = true;
    int contentColor;
    List<Region> preferredRegions;
    //this will be "KH,KW,KE,ZZ"
    String regionPreference;
    List<Region> customMasterRegionsList;
    //this will be "KH,KW,KE,ZZ"
    String customMasterRegions;
    Language customLanguage = Language.ENGLISH;
    boolean keyboardAutoPopOnSearch = true;
    boolean nrtcClickable = true;
    View.OnClickListener regionCodeHolderClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isNRTCClickable()) {
               RegionCodeDialog.openRegionCodeDialog(codePicker);
            }
        }
    };

    private OnRegionChangeListener onRegionChangeListener;

    public RegionCodePicker(Context context) {
        super(context);
        this.context = context;
        init(null);
    }

    public RegionCodePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public RegionCodePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
//        Log.d(TAG, "Initialization of NRTC");
        mInflater = LayoutInflater.from(context);
        holderView = mInflater.inflate(R.layout.layout_code_picker, this, true);
        textView_selectedRegion = (TextView) holderView.findViewById(R.id.textView_selectedRegion);
        holder = (RelativeLayout) holderView.findViewById(R.id.regionCodeHolder);
        linearFlagHolder = (LinearLayout) holderView.findViewById(R.id.linear_flag_holder);
        relativeClickConsumer = (RelativeLayout) holderView.findViewById(R.id.rlClickConsumer);
        codePicker = this;
        applyCustomProperty(attrs);
        relativeClickConsumer.setOnClickListener(regionCodeHolderClickListener);
    }

    private void applyCustomProperty(AttributeSet attrs) {
//        Log.d(TAG, "Applying custom property");
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RegionCodePicker,
                0, 0);
        //default country code
        try {
            //hide nameCode. If someone wants only phone code to avoid name collision for same region phone code.
            hideNameCode = a.getBoolean(R.styleable.RegionCodePicker_hideNameCode, false);

            //show full name
            showFullName = a.getBoolean(R.styleable.RegionCodePicker_showFullName, false);

            //auto pop keyboard
            setKeyboardAutoPopOnSearch(a.getBoolean(R.styleable.RegionCodePicker_keyboardAutoPopOnSearch, true));

            //if custom language is specified, then set it as custom
            int attrLanguage = LANGUAGE_ENGLISH;
            if (a.hasValue(R.styleable.RegionCodePicker_nrtcLanguage)) {
                attrLanguage = a.getInt(R.styleable.RegionCodePicker_nrtcLanguage, 1);
            }
            customLanguage = getLanguageEnum(attrLanguage);

            //custom master list
            customMasterRegions = a.getString(R.styleable.RegionCodePicker_customMasterRegions);
            refreshCustomMasterList();

            //preference
            regionPreference = a.getString(R.styleable.RegionCodePicker_regionPreference);
            refreshPreferredRegions();

            //default region
            defaultRegionNameCode = a.getString(R.styleable.RegionCodePicker_defaultNameCode);
            boolean setUsingNameCode = false;
            if (defaultRegionNameCode != null && defaultRegionNameCode.length() != 0) {
                if (Region.getRegionForNameCodeFromLibraryMasterList(customLanguage, defaultRegionNameCode) != null) {
                    setUsingNameCode = true;
                    setDefaultRegion(Region.getRegionForNameCodeFromLibraryMasterList(customLanguage, defaultRegionNameCode));
                    setSelectedRegion(defaultRegion);
                }
            }


            //if default region is not set using name code.
            if (!setUsingNameCode) {
                int defaultRegionCode = a.getInteger(R.styleable.RegionCodePicker_defaultCode, -1);

                //if invalid region is set using xml, it will be replaced with LIB_DEFAULT_REGION_CODE
                if (Region.getRegionForCode(customLanguage, preferredRegions, defaultRegionCode) == null) {
                    defaultRegionCode = LIB_DEFAULT_REGION_CODE;
                }
                setDefaultRegionUsingPhoneCode(defaultRegionCode);
                setSelectedRegion(defaultRegion);
            }

            //show flag
            showFlag(a.getBoolean(R.styleable.RegionCodePicker_showFlag, true));

            //content color
            int contentColor;
            if (isInEditMode()) {
                contentColor = a.getColor(R.styleable.RegionCodePicker_contentColor, 0);
            } else {
                contentColor = a.getColor(R.styleable.RegionCodePicker_contentColor, context.getResources().getColor(R.color.defaultContentColor));
            }
            if (contentColor != 0) {
                setContentColor(contentColor);
            }

            //text size
            int textSize = a.getDimensionPixelSize(R.styleable.RegionCodePicker_textSize, 0);
            if (textSize > 0) {
                textView_selectedRegion.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                setFlagSize(textSize);
                setArrowSize(textSize);
            } else { //no text size specified
                DisplayMetrics dm = context.getResources().getDisplayMetrics();
                int defaultSize = Math.round(18 * (dm.xdpi / DisplayMetrics.DENSITY_DEFAULT));
                setTextSize(defaultSize);
            }

            //if arrow arrow size is explicitly defined
            int arrowSize = a.getDimensionPixelSize(R.styleable.RegionCodePicker_arrowSize, 0);
            if (arrowSize > 0) {
                setArrowSize(arrowSize);
            }

            selectionDialogShowSearch = a.getBoolean(R.styleable.RegionCodePicker_selectionDialogShowSearch, true);
            setNrtcClickable(a.getBoolean(R.styleable.RegionCodePicker_nrtcClickable, true));

        } catch (Exception e) {
            textView_selectedRegion.setText(e.getMessage());
        } finally {
            a.recycle();
        }

    }


    private Region getDefaultRegion() {
        return defaultRegion;
    }

    private void setDefaultRegion(Region defaultRegion) {
        this.defaultRegion = defaultRegion;
//        Log.d(TAG, "Setting default region:" + defaultRegion.logString());
    }

    private TextView getTextView_selectedRegion() {
        return textView_selectedRegion;
    }

    private void setTextView_selectedRegion(TextView textView_selectedRegion) {
        this.textView_selectedRegion = textView_selectedRegion;
    }

    private Region getSelectedRegion() {
        return selectedRegion;
    }

    void setSelectedRegion(Region selectedRegion) {
        this.selectedRegion = selectedRegion;
        //as soon as region is selected, textView should be updated
        if (selectedRegion == null) {
            selectedRegion = Region.getRegionForCode(customLanguage, preferredRegions, defaultRegionCode);
        }

        if (!hideNameCode) {
            if (showFullName) {
                textView_selectedRegion.setText(selectedRegion.getName().toUpperCase() + "  0" + selectedRegion.getPhoneCode());
            } else {
                textView_selectedRegion.setText("(" + selectedRegion.getNameCode().toUpperCase() + ")  0" + selectedRegion.getPhoneCode());
            }
        } else {
            textView_selectedRegion.setText("0" + selectedRegion.getPhoneCode());
        }

        if (onRegionChangeListener != null) {
            onRegionChangeListener.onRegionSelected();
        }
    }


    Language getCustomLanguage() {
        return customLanguage;
    }

    private void setCustomLanguage(Language customLanguage) {
        this.customLanguage = customLanguage;
    }

    private View getHolderView() {
        return holderView;
    }

    private void setHolderView(View holderView) {
        this.holderView = holderView;
    }

    private RelativeLayout getHolder() {
        return holder;
    }

    private void setHolder(RelativeLayout holder) {
        this.holder = holder;
    }

    boolean isKeyboardAutoPopOnSearch() {
        return keyboardAutoPopOnSearch;
    }

    /**
     * By default, keyboard is poped every time nrtc is clicked and selection dialog is opened.
     *
     * @param keyboardAutoPopOnSearch true: to open keyboard automatically when selection dialog is opened
     *                                false: to avoid auto pop of keyboard
     */
    public void setKeyboardAutoPopOnSearch(boolean keyboardAutoPopOnSearch) {
        this.keyboardAutoPopOnSearch = keyboardAutoPopOnSearch;
    }

    EditText getEditText_registeredCarrierNumber() {
        return editText_registeredCarrierNumber;
    }

    void setEditText_registeredCarrierNumber(EditText editText_registeredCarrierNumber) {
        this.editText_registeredCarrierNumber = editText_registeredCarrierNumber;
    }

    private LayoutInflater getmInflater() {
        return mInflater;
    }

    private OnClickListener getRegionCodeHolderClickListener() {
        return regionCodeHolderClickListener;
    }

    /**
     * this will load preferredRegion based on regionPreference
     */
    void refreshPreferredRegions() {
        if (regionPreference == null || regionPreference.length() == 0) {
            preferredRegions = null;
        } else {
            List<Region> localRegionList = new ArrayList<>();
            for (String nameCode : regionPreference.split(",")) {
                Region region = Region.getRegionForNameCodeFromCustomMasterList(customMasterRegionsList, customLanguage, nameCode);
                if (region != null) {
                    if (!isAlreadyInList(region, localRegionList)) { //to avoid duplicate entry of region
                        localRegionList.add(region);
                    }
                }
            }

            if (localRegionList.size() == 0) {
                preferredRegions = null;
            } else {
                preferredRegions = localRegionList;
            }
        }
        if (preferredRegions != null) {
//            Log.d("preference list", preferredRegion.size() + " countries");
            for (Region region : preferredRegions) {
                region.log();
            }
        } else {
//            Log.d("preference list", " has no region");
        }
    }

    /**
     * this will load preferredRegions based on regionsPreference
     */
    void refreshCustomMasterList() {
        if (customMasterRegions == null || customMasterRegions.length() == 0) {
            customMasterRegionsList = null;
        } else {
            List<Region> localRegionList = new ArrayList<>();
            for (String nameCode : customMasterRegions.split(",")) {
                Region region = Region.getRegionForNameCodeFromLibraryMasterList(customLanguage, nameCode);
                if (region != null) {
                    if (!isAlreadyInList(region, localRegionList)) { //to avoid duplicate entry of country
                        localRegionList.add(region);
                    }
                }
            }

            if (localRegionList.size() == 0) {
                customMasterRegionsList = null;
            } else {
                customMasterRegionsList = localRegionList;
            }
        }
        if (customMasterRegionsList != null) {
//            Log.d("custom master list:", customMasterRegionList.size() + " regions");
            for (Region region : customMasterRegionsList) {
                region.log();
            }
        } else {
//            Log.d("custom master list", " has no region");
        }
    }

    List<Region> getCustomMasterRegionList() {
        return customMasterRegionsList;
    }

    void setCustomMasterRegionsList(List<Region> customMasterRegionsList) {
        this.customMasterRegionsList = customMasterRegionsList;
    }

    String getCustomMasterRegions() {
        return customMasterRegions;
    }

    /**
     * To provide definite set of regions when selection dialog is opened.
     * Only custom master regions, if defined, will be there is selection dialog to select from.
     * To set any regions in preference, it must be included in custom master regions, if defined
     * When not defined or null or blank is set, it will use library's default master list
     * Custom master list will only limit the visibility of irrelevant country from selection dialog. But all other functions like setRegionForCodeName() or setFullNumber() will consider all the regions.
     *
     * @param customMasterRegions is region name codes separated by comma. e.g. "kh,zz,ke"
     *                              if null or "" , will remove custom regions and library default will be used.
     */
    public void setCustomMasterRegions(String customMasterRegions) {
        this.customMasterRegions = customMasterRegions;
    }

    /**
     * This will match name code of all regions of list against the region's name code.
     *
     * @param region
     * @param regionList list of regions against which regions will be checked.
     * @return if region name code is found in list, returns true else return false
     */
    private boolean isAlreadyInList(Region region, List<Region> regionList) {
        if (region != null && regionList != null) {
            for (Region iterationRegion : regionList) {
                if (iterationRegion.getNameCode().equalsIgnoreCase(region.getNameCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This function removes possible region code from fullNumber and set rest of the number as carrier number.
     *
     * @param fullNumber combination of region code and carrier number.
     * @param region    selected region in NRTC to detect region code part.
     */
    private String detectCarrierNumber(String fullNumber, Region region) {
        String carrierNumber;
        if (region == null || fullNumber == null) {
            carrierNumber = fullNumber;
        } else {
            int indexOfCode = fullNumber.indexOf(region.getPhoneCode());
            if (indexOfCode == -1) {
                carrierNumber = fullNumber;
            } else {
                carrierNumber = fullNumber.substring(indexOfCode + region.getPhoneCode().length());
            }
        }
        return carrierNumber;
    }

    //add entry here
    private Language getLanguageEnum(int language) {
        switch (language) {
            default:
                return Language.ENGLISH;
        }
    }

    String getDialogTitle() {
        switch (customLanguage) {
            default:
                return "Select region";
        }
    }

    String getSearchHintText() {
        switch (customLanguage) {
            default:
                return "Search...";
        }
    }

    /**
     * Related to default region
     */

    String getNoResultFoundText() {
        switch (customLanguage) {
            default:
                return "No result found";
        }
    }

    /**
     * Publicly available functions from library
     */


    /**
     * This method is not encouraged because this might set some other region which have same region code as of yours. e.g 1 is common for US and canada.
     * If you are trying to set KH ( and regionPreference is not set) and you pass 1 as @param defaultRegionCode, it will set KE (prior in list due to alphabetical order)
     * Rather use setDefaultRegionUsingNameCode("kh"); or setDefaultRegionUsingNameCode("KH");
     * <p>
     * Default region code defines your default region.
     * Whenever invalid / improper number is found in setCountryForPhoneCode() /  setFullNumber(), it NRTC will set to default Region.
     * This function will not set default region as selected in NRTC. To set default region in NRTC call resetToDefaultRegion() right after this call.
     * If invalid defaultRegionCode is applied, it won't be changed.
     *
     * @param defaultRegionCode code of your default region
     *                           if you want to set KE 066(Kavango East) as default region, defaultRegionCode =  66
     *                           if you want to set KW 066(Kavango West) as default region, defaultRegionCode =  66
     */
    @Deprecated
    public void setDefaultRegionUsingPhoneCode(int defaultRegionCode) {
        Region defaultRegion = Region.getRegionForCode(customLanguage, preferredRegions, defaultRegionCode); //xml stores data in string format, but want to allow only numeric value to region code to user.
        if (defaultRegion == null) { //if no correct country is found
//            Log.d(TAG, "No region for code " + defaultRegionCode + " is found");
        } else { //if correct region is found, set the region
            this.defaultRegionCode = defaultRegionCode;
            setDefaultRegion(defaultRegion);
        }
    }

    /**
     * Default region name code defines your default region.
     * Whenever invalid / improper name code is found in setRegionForNameCode(), NRTC will set to default region.
     * This function will not set default region as selected in NRTC. To set default region in CCP call resetToDefaultRegion() right after this call.
     * If invalid defaultRegionCode is applied, it won't be changed.
     *
     * @param defaultRegionNameCode code of your default region
     *                               if you want to set OO 062(Oshikoto) as default region, defaultRegionCode =  "OO" or "oo"
     *                               if you want to set OM 065(Omusati) as default region, defaultRegionCode =  "OM" or "om"
     */
    public void setDefaultRegionUsingNameCode(String defaultRegionNameCode) {
        Region defaultRegion = Region.getRegionForNameCodeFromLibraryMasterList(customLanguage, defaultRegionNameCode); //xml stores data in string format, but want to allow only numeric value to region code to user.
        if (defaultRegion == null) { //if no correct region is found
//            Log.d(TAG, "No region for nameCode " + defaultRegionNameCode + " is found");
        } else { //if correct region is found, set the region
            this.defaultRegionNameCode = defaultRegion.getNameCode();
            setDefaultRegion(defaultRegion);
        }
    }

    /**
     * @return: Region Code of default region
     * i.e if default region is KH 061(Khomas)  returns: "61"
     * if default region is KW 066(Kavango West) returns: "66"
     */
    public String getDefaultRegionCode() {
        return defaultRegion.phoneCode;
    }

    /**
     * * To get code of default region as Integer.
     *
     * @return integer value of default region code in naregionaltelecode
     */
    public int getDefaultregionCodeAsInt() {
        int code = 0;
        try {
            code = Integer.parseInt(getDefaultRegionCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * To get code of default region with prefix "+".
     *
     * @return String value of default region code in naregionaltelecode with prefix "+"
     */
    public String getDefaultRegionCodeWithPlus() {
        return "0" + getDefaultRegionCode();
    }

    /**
     * To get name of default region.
     *
     * @return String value of region name, default in naregionaltelecode
     */
    public String getDefaultRegionName() {
        return getDefaultRegion().name;
    }

    /**
     * To get name code of default region.
     *
     * @return String value of country name, default in naregionaltelecode
     */
    public String getDefaultRegionNameCode() {
        return getDefaultRegion().nameCode.toUpperCase();
    }

    /**
     * Related to selected country
     */

    /**
     * reset the default region as selected region.
     */
    public void resetToDefaultRegion() {
        setSelectedRegion(defaultRegion);
    }

    /**
     * To get code of selected region.
     *
     * @return String value of selected region code in naregionaltelecode
     */
    public String getSelectedRegionCode() {
        return getSelectedRegion().phoneCode;
    }

    /**
     * To get code of selected region with prefix "0".
     *
     * @return String value of selected region code in naregionaltelecode with prefix "0"
     */
    public String getSelectedRegionCodeWithPlus() {
        return "0" + getSelectedRegionCode();
    }

    /**
     * * To get code of selected region as Integer.
     *
     * @return integer value of selected region code in naregionaltelecode
     */
    public int getSelectedRegionCodeAsInt() {
        int code = 0;
        try {
            code = Integer.parseInt(getSelectedRegionCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * To get name of selected region.
     *
     * @return String value of region name, selected in naregionaltelecode
     */
    public String getSelectedRegionName() {
        return getSelectedRegion().name;
    }

    /**
     * To get name code of selected region.
     *
     * @return String value of region name, selected in naregionaltelecode
     */
    public String getSelectedRegionNameCode() {
        return getSelectedRegion().nameCode.toUpperCase();
    }

    /**
     * This will set region with @param regionCode as region code, in CCP
     *
     * @param regionCode a valid region code.
     *                    If you want to set WHK +61(Windhoek), regionCode= 61
     *
     */
    public void setRegionForPhoneCode(int regionCode) {
        Region region = Region.getRegionForCode(customLanguage, preferredRegions, regionCode); //xml stores data in string format, but want to allow only numeric value to region code to user.
        if (region == null) {
            if (defaultRegion == null) {
                defaultRegion = Region.getRegionForCode(customLanguage, preferredRegions, defaultRegionCode);
            }
            setSelectedRegion(defaultRegion);
        } else {
            setSelectedRegion(region);
        }
    }

    /**
     * This will set region with @param regionNameCode as region name code, in NRTC
     *
     * @param regionNameCode a valid region name code.
     *                        If you want to set Kh 061(Khomas), regionCode= KH
     *
     */
    public void setRegionForNameCode(String regionNameCode) {
        Region region = Region.getRegionForNameCodeFromLibraryMasterList(customLanguage, regionNameCode); //xml stores data in string format, but want to allow only numeric value to region code to user.
        if (region == null) {
            if (defaultRegion == null) {
                defaultRegion = Region.getRegionForCode(customLanguage, preferredRegions, defaultRegionCode);
            }
            setSelectedRegion(defaultRegion);
        } else {
            setSelectedRegion(region);
        }
    }

    /**
     * All functions that work with fullNumber need an editText to write and read carrier number of full number.
     * An editText for carrier number must be registered in order to use functions like setFullNumber() and getFullNumber().
     *
     * @param editTextCarrierNumber - an editText where user types carrier number ( the part of full number other than country code).
     */
    public void registerCarrierNumberEditText(EditText editTextCarrierNumber) {
        setEditText_registeredCarrierNumber(editTextCarrierNumber);
    }

    /**
     * This function combines selected region code from naregionaltelecode and carrier number from @param editTextCarrierNumber
     *
     * @return Full number is regionCode 0 carrierNumber i.e regionCode= 61 and carrier number= 333 333, this will return "613333333"
     */
    public String getFullNumber() {
        String fullNumber;
        if (editText_registeredCarrierNumber != null) {
            fullNumber = getSelectedRegion().getPhoneCode() + editText_registeredCarrierNumber.getText().toString();
        } else {
            fullNumber = getSelectedRegion().getPhoneCode();
            Log.w(TAG, "EditText for carrier number is not registered. Register it using registerCarrierNumberEditText() before getFullNumber() or setFullNumber().");
        }
        return fullNumber;
    }

    /**
     * Separate out region code and carrier number from fullNumber.
     * Sets region of separated region code in naregionaltelecode and carrier number as text of editTextCarrierNumber
     * If no valid region code is found from full number, naregionaltelecode will be set to default region code and full number will be set as carrier number to editTextCarrierNumber.
     *
     * @param fullNumber is combination of region code and carrier number, (region_code+carrier_number) for example if region is Khomas (+61) and carrier/mobile number is 222 555 then full number will be 61222555 or 06122555. "+" in starting of number is optional.
     */
    public void setFullNumber(String fullNumber) {
        Region region = Region.getRegionForNumber(customLanguage, preferredRegions, fullNumber);
        setSelectedRegion(region);
        String carrierNumber = detectCarrierNumber(fullNumber, region);
        if (getEditText_registeredCarrierNumber() != null) {
            getEditText_registeredCarrierNumber().setText(carrierNumber);
        } else {
            Log.w(TAG, "EditText for carrier number is not registered. Register it using registerCarrierNumberEditText() before getFullNumber() or setFullNumber().");
        }
    }

    /**
     * This function combines selected region code from NRTC and carrier number from @param editTextCarrierNumber and prefix "+"
     *
     * @return Full number is regionCode + carrierNumber i.e regionCode= 61 and carrier number= 22222 222, this will return "+666667722"
     */
    public String getFullNumberWithPlus() {
        String fullNumber = "0" + getFullNumber();
        return fullNumber;
    }

    /**
     * @return content color of naregionaltelecode's text and small downward arrow.
     */
    public int getContentColor() {
        return contentColor;
    }

    /**
     * Sets text and small down arrow color of naregionaltelecode.
     *
     * @param contentColor color to apply to text and down arrow
     */
    public void setContentColor(int contentColor) {
        this.contentColor = contentColor;
        textView_selectedRegion.setTextColor(this.contentColor);
        imageViewArrow.setColorFilter(this.contentColor, PorterDuff.Mode.SRC_IN);
    }

    /**
     * Modifies size of text in side naregionaltelecode view.
     *
     * @param textSize size of text in pixels
     */
    public void setTextSize(int textSize) {
        if (textSize > 0) {
            textView_selectedRegion.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            setArrowSize(textSize);
            setFlagSize(textSize);
        }
    }

    /**
     * Modifies size of downArrow in CCP view
     *
     * @param arrowSize size in pixels
     */
    private void setArrowSize(int arrowSize) {
        if (arrowSize > 0) {
            LayoutParams params = (LayoutParams) imageViewArrow.getLayoutParams();
            params.width = arrowSize;
            params.height = arrowSize;
            imageViewArrow.setLayoutParams(params);
        }
    }

    /**
     * If nameCode of region in naregionaltelecode view is not required use this to show/hide region name code of ccp view.
     *
     * @param hideNameCode true will remove region name code from naregionaltelecode view, it will result  " 061 "
     *                     false will show region name code in naregionaltelecode view, it will result " (KH) 061 "
     */
    public void hideNameCode(boolean hideNameCode) {
        this.hideNameCode = hideNameCode;
        setSelectedRegion(selectedRegion);
    }

    /**
     * This will set preferred regions using their name code. Prior preferred countries will be replaced by these regions.
     * Preferred regions will be at top of region selection box.
     * If more than one regions have same region code, region in preferred list will have higher priory than others. e.g. Omaheke and Otjizondjupa have +62 as their region code. If "ot" is set as preferred region then Omaheke will be selected whenever setRegionForPhoneCode(1); or setFullNumber("+62xxxxxxxxx"); is called.
     *
     * @param regionPreference is region name codes separated by comma. e.g. "kh,ke,zz"
     */
    public void setRegionPreference(String regionPreference) {
        this.regionPreference = regionPreference;
    }

    /**
     * Language will be applied to region select dialog
     *
     * @param language
     */
    public void changeLanguage(Language language) {
        setCustomLanguage(language);
    }

    /**
     * To change font of nrtc views
     *
     * @param typeFace
     */
    public void setTypeFace(Typeface typeFace) {
        try {
            textView_selectedRegion.setTypeface(typeFace);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * To change font of ccp views along with style.
     *
     * @param typeFace
     * @param style
     */
    public void setTypeFace(Typeface typeFace, int style) {
        try {
            textView_selectedRegion.setTypeface(typeFace, style);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * To get call back on region selection a onRegionChangeListener must be registered.
     *
     * @param onRegionChangeListener
     */
    public void setOnRegionChangeListener(OnRegionChangeListener onRegionChangeListener) {
        this.onRegionChangeListener = onRegionChangeListener;
    }

    /**
     * Modifies size of flag in NRTC view
     *
     * @param flagSize size in pixels
     */
    public void setFlagSize(int flagSize) {
        imageViewFlag.getLayoutParams().height = flagSize;
        imageViewFlag.requestLayout();
    }

    public void showFlag(boolean showFlag) {
        this.showFlag = showFlag;
        if (showFlag) {
            linearFlagHolder.setVisibility(VISIBLE);
        } else {
            linearFlagHolder.setVisibility(GONE);
        }
    }

    public void showFullName(boolean showFullName) {
        this.showFullName = showFullName;
        setSelectedRegion(selectedRegion);
    }

    /**
     * SelectionDialogSearch is the facility to search through the list of region while selecting.
     *
     * @return true if search is set allowed
     */
    public boolean isSelectionDialogShowSearch() {
        return selectionDialogShowSearch;
    }

    /**
     * SelectionDialogSearch is the facility to search through the list of country while selecting.
     *
     * @param selectionDialogShowSearch true will allow search and false will hide search box
     */
    public void setSelectionDialogShowSearch(boolean selectionDialogShowSearch) {
        this.selectionDialogShowSearch = selectionDialogShowSearch;
    }

    public boolean isNRTCClickable() {
        return nrtcClickable;
    }

    /**
     * Allow click and open dialog
     *
     * @param nrtcClickable
     */
    public void setNrtcClickable(boolean nrtcClickable) {
        this.nrtcClickable = nrtcClickable;
        if (!nrtcClickable) {
            relativeClickConsumer.setOnClickListener(null);
            relativeClickConsumer.setClickable(false);
            relativeClickConsumer.setEnabled(false);
        } else {
            relativeClickConsumer.setOnClickListener(regionCodeHolderClickListener);
            relativeClickConsumer.setClickable(true);
            relativeClickConsumer.setEnabled(true);
        }
    }

    /**
     * Update every time new language is supported #languageSupport
     */
    //add an entry for your language in attrs.xml's <attr name="language" format="enum"> enum.
    //add getMasterListForLanguage() to Country.java

    //add here so that language can be set programmatically
    public enum Language {
        ENGLISH
    }

    /*
    interface to set change listener
     */
    public interface OnRegionChangeListener {
        void onRegionSelected();
    }
}
