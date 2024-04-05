package com.asianitinc.availtrade.interfaces;

public interface OnResponseListener
{
    void onSuccess(boolean flag);

    void onFailure(String error);
}
