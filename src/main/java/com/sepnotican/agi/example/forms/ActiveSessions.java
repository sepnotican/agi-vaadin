package com.sepnotican.agi.example.forms;

import com.sepnotican.agi.core.annotations.AgiForm;
import com.sepnotican.agi.core.annotations.LinkedObject;
import com.sepnotican.agi.core.annotations.Synonym;
import com.sepnotican.agi.example.entity.Customer;
import com.sepnotican.agi.example.entity.TradeDeal;
import lombok.Data;

@AgiForm(caption = ActiveSessions.FORM_NAME, menuPath = "/Service")
@Data
public class ActiveSessions {
    public final static String FORM_NAME = "Active session";

    @LinkedObject
    Customer customer;
    @Synonym("free text")
    String text;
    @LinkedObject
    TradeDeal tradeDeal;
}
