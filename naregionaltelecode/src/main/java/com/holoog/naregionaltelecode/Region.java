package com.holoog.naregionaltelecode;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by O.G on 4/25/2017.
 */

class Region {
    static String TAG = "Class Region";
    String nameCode;
    String phoneCode;
    String name;

    public Region(){

    }

    public Region(String nameCode, String phoneCode, String name){
        this.nameCode = nameCode;
        this.phoneCode = phoneCode;
        this.name = name;
    }

    /**
     * This function parses the raw/regions.xml file, and get list of all the regions.
     *
     * @param context: required to access application resources (where regions.xml is).
     * @return List of all the regions available in xml file.
     */
    public static List<Region> readXMLofRegions(Context context) {
        List<Region> regions = new ArrayList<Region>();
        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = xmlFactoryObject.newPullParser();
            InputStream ins = context.getResources().openRawResource(R.raw.regions);
            xmlPullParser.setInput(ins, null);
            int event = xmlPullParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = xmlPullParser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("region")) {
                            Region region = new Region();
                            region.setNameCode(xmlPullParser.getAttributeValue(null, "code").toUpperCase());
                            region.setPhoneCode(xmlPullParser.getAttributeValue(null, "phoneCode"));
                            region.setName(xmlPullParser.getAttributeValue(null, "name"));
                            regions.add(region);
                        }
                        break;
                }
                event = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        return regions;
    }

    /**
     * Search a region which matches @param code.
     *
     * @param preferredRegions is list of preference regions.
     * @param code               phone code. i.e "61" or "61"
     * @return region that has telephone code as @param code.
     * or returns null if no region matches given code.
     * if same code (e.g. 061) available for more than one region ( Zambezi, Kavango Eat) , this function will return preferred region.
     */
    private static Region getRegionForCode(RegionCodePicker.Language language, List<Region> preferredRegions, String code) {

        /**
         * check in preferred regions
         */
        if (preferredRegions != null && !preferredRegions.isEmpty()) {
            for (Region country : preferredRegions) {
                if (country.getPhoneCode().equals(code)) {
                    return country;
                }
            }
        }

        for (Region regions : getLibraryMasterRegionList(language)) {
            if (regions.getPhoneCode().equals(code)) {
                return regions;
            }
        }
        return null;
    }

    public static List<Region> getCustomMasterRegionList(RegionCodePicker codePicker) {
        codePicker.refreshCustomMasterList();
        if (codePicker.customMasterRegionsList != null && codePicker.customMasterRegionsList.size() > 0) {
            return codePicker.getCustomMasterRegionList();
        } else {
            return getLibraryMasterRegionList(codePicker.getCustomLanguage());
        }
    }

    /**
     * Search a region which matches @param nameCode.
     *
     *
     * @param customMasterRegionsList
     * @param nameCode region name code. i.e Kavango East or Zambezi . See regions.xml for all code names.
     * @return region that has phone code as @param code.
     * or returns null if no region matches given code.
     */
    public static Region getRegionForNameCodeFromCustomMasterList(List<Region> customMasterRegionsList, RegionCodePicker.Language language, String nameCode) {
        if (customMasterRegionsList == null || customMasterRegionsList.size() == 0) {
            return getRegionForNameCodeFromLibraryMasterList(language, nameCode);
        } else {
            for (Region region : customMasterRegionsList) {
                if (region.getNameCode().equalsIgnoreCase(nameCode)) {
                    return region;
                }
            }
        }
        return null;
    }

    /**
     * Search a region which matches @param nameCode.
     *
     * @param nameCode region name code. i.e KH or us or Au. See regions.xml for all code names.
     * @return Region that has phone code as @param code.
     * or returns null if no region matches given code.
     */
    public static Region getRegionForNameCodeFromLibraryMasterList(RegionCodePicker.Language language, String nameCode) {
        List<Region> regions = Region.getLibraryMasterRegionList(language);
        for (Region region : regions) {
            if (region.getNameCode().equalsIgnoreCase(nameCode)) {
                return region;
            }
        }
        return null;
    }

    /**
     * Search a region which matches @param nameCode.
     *
     */
    public static Region getRegionForNameCodeTrial(String nameCode, Context context) {
        List<Region> regions = Region.readXMLofRegions(context);
        for (Region region : regions) {
            if (region.getNameCode().equalsIgnoreCase(nameCode)) {
                return region;
            }
        }
        return null;
    }

    /**
     * Search a region which matches @param code.
     *
     * @param preferredRegions list of region with priority,
     * @param code               phone code. i.e 91 or 1
     * @return Region that has phone code as @param code.
     * or returns null if no region matches given code.
     */
    static Region getRegionForCode(RegionCodePicker.Language language, List<Region> preferredRegions, int code) {
        return getRegionForCode(language, preferredRegions, code + "");
    }

    /**
     *
     */
    static Region getRegionForNumber(RegionCodePicker.Language language, List<Region> preferredRegions, String fullNumber) {
        int firstDigit;
        if (fullNumber.length() != 0) {
            if (fullNumber.charAt(0) == '+') {
                firstDigit = 1;
            } else {
                firstDigit = 0;
            }
            Region region = null;
            for (int i = firstDigit; i < firstDigit + 4; i++) {
                String code = fullNumber.substring(firstDigit, i);
                region = Region.getRegionForCode(language, preferredRegions, code);
                if (region != null) {
                    return region;
                }
            }
        }
        return null;
    }

    /**
     * This will return all the regions. No preference is manages.
     * Anytime new region need to be added, add it
     *
     * @return
     */
    public static List<Region> getLibraryMasterRegionList(RegionCodePicker.Language language) {{
            return getLibraryMasterRegionEnglish();
        }
    }

    public static List<Region> getLibraryMasterRegionEnglish() {
        List<Region> regions = new ArrayList<>();
        regions.add(new Region("eo", "64", "Erongo"));
        regions.add(new Region("ku", "65", "Kunene"));
        regions.add(new Region("om", "65", "Omusati"));
        regions.add(new Region("oa", "65", "Oshana"));
        regions.add(new Region("oh", "65", "Ohanguena"));
        regions.add(new Region("oo", "67", "Oshikoto"));
        regions.add(new Region("kw", "66", "Kavango West"));
        regions.add(new Region("ke", "66", "Kavango East"));
        regions.add(new Region("ot", "67", "Otjizondjupa"));
        regions.add(new Region("oe", "62", "Omaheke"));
        regions.add(new Region("kh", "61", "Khomas"));
        regions.add(new Region("hp", "63", "Hardap"));
        regions.add(new Region("kr", "63", "||Karas"));
        regions.add(new Region("zi", "66", "Zambezi"));
        return regions;
    }

    public String getNameCode() {
        return nameCode;
    }

    public void setNameCode(String nameCode) {
        this.nameCode = nameCode;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void log() {
        try {
            Log.d(TAG, "Region->" + nameCode + ":" + phoneCode + ":" + name);
        } catch (NullPointerException ex) {
            Log.d(TAG, "Null");
        }
    }

    public String logString() {
        return nameCode.toUpperCase() + " 0" + phoneCode + "(" + name + ")";
    }

    /**
     * If region have query word in name or name code or phone code, this will return true.
     *
     * @param query
     * @return
     */
    public boolean isEligibleForQuery(String query) {
        query = query.toLowerCase();
        return getName().toLowerCase().contains(query) || getNameCode().toLowerCase().contains(query) || getPhoneCode().toLowerCase().contains(query);
    }
}
