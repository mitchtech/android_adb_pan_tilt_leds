package net.mitchtech.adb;

import java.io.IOException;

import net.mitchtech.adb.pantiltleds.R;

import org.microbridge.server.Server;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;

public class PanTiltLedsActivity extends Activity {
	static final String TAG = "LED ACtivity";

	private final byte PIN_OFF = 0x0;
	private final byte PIN_ON = 0x1;

	private ToggleButton ledToggle1;
	private ToggleButton ledToggle2;

	private SeekBar servoBar1;
	private SeekBar servoBar2;

	private OnCheckedChangeListener stateChangeListener = new StateChangeListener();
	private OnSeekBarChangeListener seekBarChangeListener = new SeekBarChangeListener();

	Server server = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		ledToggle1 = (ToggleButton) findViewById(R.id.ToggleButton1);
		ledToggle1.setTag((byte) 0x3);
		ledToggle1.setOnCheckedChangeListener(stateChangeListener);

		ledToggle2 = (ToggleButton) findViewById(R.id.ToggleButton2);
		ledToggle2.setTag((byte) 0x4);
		ledToggle2.setOnCheckedChangeListener(stateChangeListener);

		servoBar1 = (SeekBar) findViewById(R.id.SeekBarServo1);
		servoBar1.setTag((byte) 0x5);
		servoBar1.setOnSeekBarChangeListener(seekBarChangeListener);

		servoBar2 = (SeekBar) findViewById(R.id.SeekBarServo2);
		servoBar2.setTag((byte) 0x6);
		servoBar2.setOnSeekBarChangeListener(seekBarChangeListener);

		// Create new TCP Server
		try {
			server = new Server(4567);
			server.start();
		} catch (IOException e) {
			Log.e("microbridge", "Unable to start TCP server", e);
			System.exit(-1);
		}
	}

	private class StateChangeListener implements OnCheckedChangeListener {

		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			byte portByte = (Byte) buttonView.getTag();
			if (isChecked) {
				try {
					server.send(new byte[] { portByte, PIN_ON });
				} catch (IOException e) {
					Log.e("microbridge", "problem sending TCP message", e);
				}
			} else {
				try {
					server.send(new byte[] { portByte, PIN_OFF });
				} catch (IOException e) {
					Log.e("microbridge", "problem sending TCP message", e);
				}
			}
		}
	}

	private class SeekBarChangeListener implements OnSeekBarChangeListener {

		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			byte portByte = (Byte) seekBar.getTag();
			try {
				server.send(new byte[] { portByte, (byte) progress });
			} catch (IOException e) {
				Log.e("microbridge", "problem sending TCP message", e);
			}
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		public void onStopTrackingTouch(SeekBar seekBar) {
		}
	}

}