package com.lenovo.adminmatchpoint;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by shree on 24/08/2017.
 */

public class CenterSlot {

    public String CenterName;
   /* public Date Availableslot1;*/
    public String Availableslot;
    public String accesstype;
    public long vacancy;
    public CenterSlot(String cname, String as, String accesstype,long vacancy) {
        this.CenterName = cname;

        this.Availableslot = as;

        this.accesstype = accesstype;
        this.vacancy=vacancy;
    }
}
