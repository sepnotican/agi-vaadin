package agi.core;

import agi.core.form.IUIHandler;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class SessionUIHolder {
    public static Map<String, WeakReference<IUIHandler>> sessionsMap = new HashMap<>();

    public static void removeSession(String sessionId) {
        SessionUIHolder.sessionsMap.remove(sessionId);
    }

}
