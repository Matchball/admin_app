package com.lenovo.adminmatchpoint;

/**
 * Created by atharva on 18-06-2017.
 * class to store different categories allowed by tournaments
 */

public class CategoryTypes {
    public boolean BU11;
    public boolean GU11;
    public boolean BU13;
    public boolean GU13;
    public boolean BU15;
    public boolean GU15;
    public boolean BU17;
    public boolean GU17;
    public boolean BU19;
    public boolean GU19;

    public CategoryTypes() {
        this.BU11 = false;
        this.BU13 = false;
        this.BU15 = false;
        this.BU17 = false;
        this.BU19 = false;
        this.GU11 = false;
        this.GU13 = false;
        this.GU15 = false;
        this.GU17 = false;
        this.GU19 = false;
    }

    public CategoryTypes(boolean b11, boolean g11, boolean b13, boolean g13, boolean b15, boolean g15, boolean b17, boolean g17, boolean b19, boolean g19) {
        this.BU11 = b11;
        this.GU11 = g11;
        this.BU13 = b13;
        this.GU13 = g13;
        this.BU15 = b15;
        this.GU15 = g15;
        this.BU17 = b17;
        this.GU17 = g17;
        this.BU19 = b19;
        this.GU19 = g19;
    }
}
