package com.applitools;

public interface ICheckRGSettings extends ICheckSettings {
    ICheckRGSettings sendDom(boolean sendDom);
    ICheckRGSettings fully(boolean fully);
    ICheckRGSettings fully();
    ICheckRGSettings withName(String name);
    ICheckRGSettings webHook(String hook);
}
