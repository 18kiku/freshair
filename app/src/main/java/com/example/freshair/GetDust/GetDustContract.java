package com.example.freshair.GetDust;

import com.example.freshair.dust_material.GetDust;

public class GetDustContract {
    public interface View{
        void showGetDustResult(GetDust getDust);
        void showLoadError(String message);
    }
    interface UserActionsListener{
        void loadGetDustData();
    }
}
