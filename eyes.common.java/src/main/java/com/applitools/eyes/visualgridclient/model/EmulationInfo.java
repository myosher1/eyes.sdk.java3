package com.applitools.eyes.visualgridclient.model;

import com.fasterxml.jackson.annotation.JsonValue;

public class EmulationInfo extends EmulationBaseInfo {

    private DeviceName deviceName;

    public EmulationInfo(DeviceName deviceName, ScreenOrientation screenOrientation) {
        super(screenOrientation);
        this.deviceName = deviceName;
    }

    public String getDeviceName() {
        return deviceName.getName();
    }

    public void setDeviceName(DeviceName deviceName) {
        this.deviceName = deviceName;
    }

    public ScreenOrientation getScreenOrientation() {
        return screenOrientation;
    }

    public void setScreenOrientation(ScreenOrientation screenOrientation) {
        this.screenOrientation = screenOrientation;
    }

    public enum DeviceName {
        IPHONE4("iPhone 4"),
        iPhone5SE("iPhone 5/SE"),
        iPhone6_7_8("iPhone 6/7/8"),
        iPhone6_7_8_Plus("iPhone 6/7/8 Plus"),
        iPhone_X("iPhone X"),
        BlackBerry_Z30("BlackBerry Z30"),
        Nexus_4("Nexus 4"),
        Nexus_5("Nexus 5"),
        Nexus_5X("Nexus 5X"),
        Nexus_6("Nexus 6"),
        Nexus_6P("Nexus 6P"),
        Pixel_2("Pixel 2"),
        Pixel_2_XL("Pixel 2 XL"),
        LG_Optimus_L70("LG Optimus L70"),
        Nokia_N9("Nokia N9"),
        Nokia_Lumia_520("Nokia Lumia 520"),
        Microsoft_Lumia_550("Microsoft Lumia 550"),
        Microsoft_Lumia_950("Microsoft Lumia 950"),
        Galaxy_S_III("Galaxy S III"),
        Galaxy_S5("Galaxy S5"),
        Kindle_Fire_HDX("Kindle Fire HDX"),
        iPad_Mini("iPad Mini"),
        iPad("iPad"),
        iPad_Pro("iPad Pro"),
        Blackberry_PlayBook("Blackberry PlayBook"),
        Nexus_10("Nexus 10"),
        Nexus_7("Nexus 7"),
        Galaxy_Note_3("Galaxy Note 3"),
        Galaxy_Note_II("Galaxy Note II"),
        Laptop_with_touch("Laptop with touch"),
        Laptop_with_HiDPI_screen("Laptop with HiDPI screen"),
        Laptop_with_MDPI_screen("Laptop with MDPI screen");

        private String name;

        DeviceName(String name) {
            this.name = name;
        }

        @JsonValue
        public String getName() {
            return name;
        }
    }


    @Override
    public String toString() {
        return "EmulationInfo{" +
                "deviceName=" + deviceName +
                ", screenOrientation=" + screenOrientation +
                '}';
    }
}
