package uk.co.alt236.bluetoothreset;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.alt236.bluetoothreset.util.BluetoothCheck;
import uk.co.alt236.bluetoothreset.util.ExecTerminal;


public class MainActivity extends Activity {
    @InjectView(R.id.restart_bt)
    protected Button btnReset;

    @InjectView(R.id.check_status)
    protected Button btnRefresh;

    @InjectView(R.id.status)
    protected TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        checkState();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBluetooth();
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                tvStatus.setText("");
                checkState();
            }
        });
    }

    private void resetBluetooth(){
        final ExecTerminal execTerminal = new ExecTerminal();
        final String clearCache = "pm clear com.android.bluetooth";
        final String startBt = "service call bluetooth_manager 8";

        final List<String> commands = new ArrayList<>();
        commands.add(clearCache);
        commands.add(startBt);

        final StringBuilder sb = new StringBuilder();
        for (final String command : commands) {
            sb.setLength(0);
            sb.append("\n");
            sb.append("---> Executing: '" + command + "'");

            final ExecTerminal.ExecResult result = execTerminal.execSu(command);
            Log.d("TAG", String.format("Got %s for '%s'", result.getExitCode(), command));
            sb.append("\n");
            sb.append("Exit Code: '" + result.getExitCode() + "'");
            sb.append("\n");
            sb.append("Std Out: '" + result.getStdOut().trim() + "'");
            sb.append("\n");
            sb.append("Std Err: '" + result.getStdErr().trim() + "'");

            tvStatus.append(sb.toString());
            sleep(1000);
        }
        tvStatus.append("\n");
        tvStatus.append("-------------");

        checkState();
    }

    private void sleep(final int millis){
      try{
          Thread.sleep(millis);
      } catch (final InterruptedException e){
          // NOTHING
      }
    }

    private void checkState() {
        final BluetoothCheck bluetoothCheck = new BluetoothCheck();
        final BluetoothCheck.BT_STATUS status = bluetoothCheck.getBluetoothStatus();
        final int targetApi = Build.VERSION_CODES.LOLLIPOP_MR1;

        btnReset.setEnabled(false);
        btnRefresh.setEnabled(false);

        final String text;
        if(Build.VERSION.SDK_INT != targetApi){
            text = "This application will only work on API " + targetApi;
        } else {
            if(status == BluetoothCheck.BT_STATUS.BT_NOT_PRESENT){
                text = "This device does not support Bluetooth.";
            } else if (status == BluetoothCheck.BT_STATUS.ENABLED){
                text = "Bluetooth seems to be enabled";
            } else if(status == BluetoothCheck.BT_STATUS.DISABLED){
                if(isAirplaneModeOn(this)){
                    text = "Bluetooth is off but flight mode is enabled!";
                } else {
                    text = "Bluetooth is off";
                }
                btnReset.setEnabled(true);
            } else {
                text = "Unknown state! No idea what to do...";
            }
        }

        tvStatus.append("\n");
        tvStatus.append(text);
        btnRefresh.setEnabled(true);
    }

    /**
     * Gets the state of Airplane Mode.
     *
     * @param context
     * @return true if enabled.
     */
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isAirplaneModeOn(final Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }
}
