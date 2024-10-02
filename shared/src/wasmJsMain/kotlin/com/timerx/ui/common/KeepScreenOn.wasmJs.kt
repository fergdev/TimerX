package com.timerx.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect

private fun requestWakeLock() {
    js("""
   if(isScreenLockSupported()){
      let screenLock;
      try {
         screenLock = navigator.wakeLock.request('screen');
      } catch(err) {
         console.log(err.name, err.message);
      }
      return screenLock;
    }
    """
    )
}

private fun releaseWakeLock() {
    js(""" 
    if(typeof screenLock !== "undefined" && screenLock != null) {
       screenLock.release()
       .then(() => {
          console.log("Lock released");
          screenLock = null;
       });
    }
    """
    )
}

@Composable
actual fun KeepScreenOn() {
    // Noop
//    DisposableEffect(Unit) {
//        requestWakeLock()
//        onDispose {
//            releaseWakeLock()
//        }
//    }
}
