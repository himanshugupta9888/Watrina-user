package com.gox.basemodule.utils

import android.content.DialogInterface

interface ViewCallBack{

    interface Alert{
        fun onPositiveButtonClick(dialog:DialogInterface)
        fun onNegativeButtonClick(dialog:DialogInterface)
    }
}