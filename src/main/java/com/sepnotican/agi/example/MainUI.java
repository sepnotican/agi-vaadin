package com.sepnotican.agi.example;

import com.sepnotican.agi.core.SessionUIHolder;
import com.sepnotican.agi.core.form.IUIHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@SpringUI
public class MainUI extends UI {

    @Autowired
    private ApplicationContext context;

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        IUIHandler uiHandler = null;
        String sessionId = vaadinRequest.getWrappedSession().getId();
        uiHandler = SessionUIHolder.sessionsMap.get(sessionId);
        if (uiHandler == null) {
            uiHandler = context.getBean(IUIHandler.class);
            SessionUIHolder.sessionsMap.put(vaadinRequest.getWrappedSession().getId(), uiHandler);
        }
        setContent(uiHandler.getMainLayout());
        //todo kill sess
        setSizeFull();

    }
}
