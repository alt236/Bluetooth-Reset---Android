package uk.co.alt236.bluetoothreset.util;

import android.bluetooth.BluetoothAdapter;

/**
 * Created by alex on 10/06/15.
 */
public class BluetoothCheck {
    public enum BT_STATUS{
        BT_NOT_PRESENT,
        ENABLED,
        DISABLED
    }

    public BT_STATUS getBluetoothStatus(){
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return BT_STATUS.BT_NOT_PRESENT;
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                return BT_STATUS.ENABLED;
            } else {
                return BT_STATUS.DISABLED;
            }
        }
    }
}
