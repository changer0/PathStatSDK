// CrossProcess.aidl
package com.example.pathstatsdk;

// Declare any non-default types here with import statements

interface PageState {
    void setSessionId(String String);
    String getSessionId();
    void setOrder(int order);
    int getOrder();
    void setActivityNum(int num);
    int getActivityNum();
    void setfragmentNum(int num);
    int getFragmentNum();
}
