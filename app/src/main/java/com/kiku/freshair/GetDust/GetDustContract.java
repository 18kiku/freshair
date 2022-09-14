package com.kiku.freshair.GetDust;

import com.kiku.freshair.dustMaterial.GetDust;

public class GetDustContract {
    public interface View{
        void showGetDustResult(GetDust getDust);
        void showLoadError(String message);
    }
    interface UserActionsListener{
        void loadGetDustData();
    }
}
