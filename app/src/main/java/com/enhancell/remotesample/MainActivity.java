package com.enhancell.remotesample;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.enhancell.remote.Connection;
import com.enhancell.remote.Device;
import com.enhancell.remote.GpsLocation;
import com.enhancell.remote.Manager;
import com.verveba.ts3.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

// Sample application for Enhancell Remote SDK. The API used here is described in more detail in
// JavaDoc under doc folder
public class MainActivity extends AppCompatActivity implements Manager.Listener, Connection.Listener, Device.Listener, Runnable {
    private static final CharSequence COMMAND_DISCONNECT_DEVICE = "Disconnect";
    private static final CharSequence COMMAND_DIAL = "Dial";
    private static final CharSequence COMMAND_HANGUP = "Hangup";
    private static final CharSequence COMMAND_CONNECT_DATA = "Connect data";
    private static final CharSequence COMMAND_DISCONNECT_DATA = "Disconnect data";
    private static final CharSequence COMMAND_CONNECT_WIFI = "Connect Wi-Fi";
    private static final CharSequence COMMAND_DISCONNECT_WIFI = "Disconnect Wi-Fi";
    private static final CharSequence COMMAND_SEND_SMS = "Send SMS";
    private static final CharSequence COMMAND_START_MEASUREMENT = "Start measurement";
    private static final CharSequence COMMAND_STOP_MEASUREMENT = "Stop measurement";
    private static final CharSequence COMMAND_PAUSE_MEASUREMENT = "Pause measurement";
    private static final CharSequence COMMAND_RESUME_MEASUREMENT = "Resume measurement";
    private static final CharSequence COMMAND_READ_SUPPORTED_BANDS = "Read supported bands";
    private static final CharSequence COMMAND_READ_LOCK_STATUS = "Read lock status";
    private static final CharSequence COMMAND_CHECK_IF_LICENSED = "Check if licensed";
    private static final CharSequence COMMAND_REQUEST_LICENSE = "Request license";
    private static final CharSequence COMMAND_RELEASE_LICENSE = "Release license";
    private static final CharSequence COMMAND_UPLOAD_HTTP_SCRIPT = "Upload web page script";
    private static final CharSequence COMMAND_UPLOAD_MOBILE_CONNECTION_SCRIPT = "Upload mobile connection script";
    private static final CharSequence COMMAND_UPLOAD_SPEEDTEST_ROLL_SCRIPT = "Upload speedtest with roll script";
    private static final CharSequence COMMAND_START_SCRIPT = "Start script";
    private static final CharSequence COMMAND_STOP_SCRIPT = "Stop script";
    private static final CharSequence COMMAND_REQUEST_POLQA_LICENSE = "Request POLQA license";
    private static final CharSequence COMMAND_LOCK_BANDS = "Lock bands";
    private static final CharSequence COMMAND_LOCK_GSM = "Lock GSM";
    private static final CharSequence COMMAND_LOCK_UMTS = "Lock UMTS";
    private static final CharSequence COMMAND_LOCK_LTE = "Lock LTE";
    private static final CharSequence COMMAND_LOCK_UMTS_CHANNEL = "Lock UMTS Channel";
    private static final CharSequence COMMAND_LOCK_LTE_CHANNEL = "Lock LTE Channel";
    private static final CharSequence COMMAND_LOCK_LTE_PCI = "Lock LTE PCI";
    private static final CharSequence COMMAND_RELEASE_LOCKS = "Release locks";

    private class DeviceAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return _devices.size();
        }

        @Override
        public Object getItem(int position) {
            return _devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            class DeviceView extends ViewGroup {
                private final View _layout;
                private Device _device;
                private final String _thruput;

                @SuppressLint({"ViewHolder", "InflateParams"})
                public DeviceView(Context context) {
                    super(context);

                    _thruput = "";

                    _layout = LayoutInflater.from(context).inflate(R.layout.view_device, null);
                    _layout.setId(R.id.view_device);
                    addView(_layout);
                }

                public void setDevice(Device device) {
                    _device = device;

                    // Step 5. Here we start listening for parameter and state updates etc.
                    setBackgroundColor(adjustSaturation(_device.getColor(), 1.6f));

                    updateValues();
                }

                @Override
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    _layout.measure(widthMeasureSpec, heightMeasureSpec);

                    setMeasuredDimension(_layout.getMeasuredWidth(), _layout.getMeasuredHeight());
                }

                @Override
                protected void onLayout(boolean changed, int l, int t, int r, int b) {
                    _layout.layout(0, 0, r - l, b - t);
                }

                private void updateValues() {
                    TextView nameText = findViewById(R.id.text_device_name);
                    TextView valueText = findViewById(R.id.text_device_value);
                    TextView detailText = findViewById(R.id.text_device_details);

                    nameText.setText(_device.getTitle());
                    valueText.setText(_thruput);

                    String status = "";
                    status = appendStatus(status, Device.DeviceStates.VoiceCall, "Call");
                    status = appendStatus(status, Device.DeviceStates.Data, "Data");
                    status = appendStatus(status, Device.DeviceStates.WiFi, "Wi-Fi");
                    if (status.isEmpty())
                        status = "Idle";
                    detailText.setText(status);
                }

                private String appendStatus(String str, int state, String text) {
                    if ((_device.getState() & state) == 0)
                        return str;
                    if (!str.isEmpty())
                        str += ", ";
                    return str + text;
                }
            }
            DeviceView view;
            if (convertView != null) {
                view = (DeviceView) convertView;
            } else {
                view = new DeviceView(parent.getContext());
            }
            view.setDevice(_devices.get(position));
            return view;
        }
    }

    private class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case "com.enhancell.intent.action.SERVICE_STARTED":
                    if (intent.getBooleanExtra("ok", false)) {
                        Log.d("RemoteSDK", "Service started ok");
                    } else {
                        Log.d("RemoteSDK", "Service error: " + intent.getStringExtra("error"));
                    }
                    break;
                case "com.enhancell.intent.action.SERVICE_STOPPED":
                    Log.d("RemoteSDK", "Service stopped");
                    Toast.makeText(MainActivity.this, "Service stopped", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private Manager _manager;
    private final List<Connection> _connections = new ArrayList<>();
    private final List<Device> _devices = new ArrayList<>();
    private final ServiceReceiver _receiver = new ServiceReceiver();
    private ServiceConnection _service;
    private DeviceAdapter _adapter;
    private Handler _handler;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        _handler = new Handler();
        _service = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("RemoteSDK", "Service connected");

                Thread t = new Thread() {
                    public void run() {
                        try {
                            for (int i = 0; i < 40; ++i) {
                                if (_manager.connectLocal()) {
                                    break;
                                }
                                Thread.sleep(500);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("RemoteSDK", "Service disconnected");
                Toast.makeText(MainActivity.this, "Service disconnected", Toast.LENGTH_LONG).show();
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.enhancell.intent.action.SERVICE_STARTED");
        filter.addAction("com.enhancell.intent.action.SERVICE_STOPPED");
        registerReceiver(_receiver, filter);

        // Step 1. Create instance of the Manager object. Pass in Manager.Listener interface
        // as second parameter to listen for connection updates
        _manager = new Manager(this, this, "RemoteSample");

        _adapter = new DeviceAdapter();
        ListView deviceList = findViewById(R.id.list_devices);
        assert deviceList != null;
        deviceList.setAdapter(_adapter);

        deviceList.setOnItemClickListener((adapter, view, pos, id) -> {
            Device device = _devices.get(pos);
            int state = device.getState();

            List<CharSequence> names = new ArrayList<>();
            names.add(COMMAND_START_MEASUREMENT);
            names.add(COMMAND_STOP_MEASUREMENT);
            names.add(COMMAND_PAUSE_MEASUREMENT);
            names.add(COMMAND_RESUME_MEASUREMENT);
            if ((state & Device.DeviceStates.VoiceCall) == 0) {
                names.add(COMMAND_DIAL);
            } else {
                names.add(COMMAND_HANGUP);
            }
            if ((state & Device.DeviceStates.Data) == 0) {
                names.add(COMMAND_CONNECT_DATA);
            } else {
                names.add(COMMAND_DISCONNECT_DATA);
            }
            if ((state & Device.DeviceStates.WiFi) == 0) {
                names.add(COMMAND_CONNECT_WIFI);
            } else {
                names.add(COMMAND_DISCONNECT_WIFI);
            }
            names.add(COMMAND_SEND_SMS);
            names.add(COMMAND_READ_SUPPORTED_BANDS);
            names.add(COMMAND_READ_LOCK_STATUS);
            names.add(COMMAND_LOCK_BANDS);
            names.add(COMMAND_LOCK_GSM);
            names.add(COMMAND_LOCK_UMTS);
            names.add(COMMAND_LOCK_LTE);
            names.add(COMMAND_LOCK_UMTS_CHANNEL);
            names.add(COMMAND_LOCK_LTE_CHANNEL);
            names.add(COMMAND_LOCK_LTE_PCI);
            names.add(COMMAND_RELEASE_LOCKS);
            names.add(COMMAND_UPLOAD_HTTP_SCRIPT);
            names.add(COMMAND_UPLOAD_MOBILE_CONNECTION_SCRIPT);
            names.add(COMMAND_UPLOAD_SPEEDTEST_ROLL_SCRIPT);
            names.add(COMMAND_START_SCRIPT);
            names.add(COMMAND_STOP_SCRIPT);
            names.add(COMMAND_CHECK_IF_LICENSED);
            names.add(COMMAND_REQUEST_LICENSE);
            names.add(COMMAND_RELEASE_LICENSE);
            names.add(COMMAND_REQUEST_POLQA_LICENSE);
            names.add(COMMAND_DISCONNECT_DEVICE);

            new AlertDialog.Builder(MainActivity.this)
                    .setItems(names.toArray(new CharSequence[names.size()]), (dialog, item) -> {
                        try {
                            CharSequence name = names.get(item);
                            if (name.equals(COMMAND_DIAL)) {
                                device.dial(readNumber(), "", "");
                            } else if (name.equals(COMMAND_HANGUP)) {
                                device.hangup();
                            } else if (name.equals(COMMAND_CONNECT_DATA)) {
                                device.connectData();
                            } else if (name.equals(COMMAND_DISCONNECT_DATA)) {
                                device.disconnectData();
                            } else if (name.equals(COMMAND_CONNECT_WIFI)) {
                                device.connectWiFi();
                            } else if (name.equals(COMMAND_DISCONNECT_WIFI)) {
                                device.disconnectWiFi();
                            } else if (name.equals(COMMAND_SEND_SMS)) {
                                device.sendSms(readNumber(), "Hello!");
                            } else if (name.equals(COMMAND_DISCONNECT_DEVICE)) {
                                device.getConnection().close();
                            } else if (name.equals(COMMAND_CHECK_IF_LICENSED)) {
                                boolean licensed = device.getConnection().isLicensed();
                                Toast.makeText(this, licensed ? "Device licensed" : "Device not licensed", Toast.LENGTH_SHORT).show();
                            } else if (name.equals(COMMAND_REQUEST_LICENSE)) {
                                LayoutInflater inflater = LayoutInflater.from(this);
                                View layout = inflater.inflate(getResources().getLayout(R.layout.dialog_license), null);
                                final EditText usernameEdit = layout.findViewById(R.id.edit_username);
                                final EditText passwordEdit = layout.findViewById(R.id.edit_password);
                                new AlertDialog.Builder(this)
                                        .setView(layout)
                                        .setTitle("Request license")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", (dialog1, which) -> device.getConnection().requestLicense(usernameEdit.getText().toString(), passwordEdit.getText().toString()))
                                        .setNegativeButton("Cancel", null)
                                        .create().show();
                            } else if (name.equals(COMMAND_RELEASE_LICENSE)) {
                                device.getConnection().releaseLicense();
                            } else if (name.equals(COMMAND_READ_SUPPORTED_BANDS)) {
                                showLockStatus(device, true);
                            } else if (name.equals(COMMAND_READ_LOCK_STATUS)) {
                                showLockStatus(device, false);
                            } else if (name.equals(COMMAND_START_MEASUREMENT)) {
                                device.startMeasurement(String.format("remote-%s", new SimpleDateFormat("yyyyMMdd-hhmmss", Locale.US).format(new Date())), UUID.randomUUID().toString(), Settings.Secure.getString(getContentResolver(),
                                        Settings.Secure.ANDROID_ID), 0);
                            } else if (name.equals(COMMAND_STOP_MEASUREMENT)) {
                                device.stopMeasurement("");
                            } else if (name.equals(COMMAND_PAUSE_MEASUREMENT)) {
                                device.pauseMeasurement();
                            } else if (name.equals(COMMAND_RESUME_MEASUREMENT)) {
                                device.resumeMeasurement();
                            } else if (name.equals(COMMAND_UPLOAD_HTTP_SCRIPT)) {
                                String script =
                                        "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                                        "<script>" +
                                        "   <loop repeat=\"10\">" +
                                        "       <command str=\"http -url:https://www.google.com/ -type:page\"/>" +
                                        "       <command str=\"wait -duration:5\"/>" +
                                        "   </loop>" +
                                        "</script>";
                                device.uploadScript("HTTP_PAGE", script.getBytes());
                            } else if (name.equals(COMMAND_UPLOAD_MOBILE_CONNECTION_SCRIPT)) {
                                String script =
                                        "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                                        "<script>" +
                                        "   <loop repeat=\"10\">" +
                                        "       <command str=\"disconnect -technology:cellular\"/>" +
                                        "       <command str=\"wait -duration:10\"/>" +
                                        "       <command str=\"connect -technology:cellular\"/>" +
                                        "       <command str=\"wait -duration:10\"/>" +
                                        "       <command str=\"http -url:https://www.google.com/ -type:page\"/>" +
                                        "       <command str=\"wait -duration:5\"/>" +
                                        "   </loop>" +
                                        "</script>";
                                device.uploadScript("MOBILE_CONNECTION", script.getBytes());
                            } else if (name.equals(COMMAND_UPLOAD_SPEEDTEST_ROLL_SCRIPT)) {
                                String script =
                                        "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                                        "<script>" +
                                        "   <loop repeat=\"100000000\">" +
                                        "       <command str=\"http -url:http://speedtest.tele2.net/10MB.zip?filesize=10 -type:file\"/>" +
                                        "       <command str=\"wait -duration:5\"/>" +
                                        "       <command str=\"speedtest.net -type:ping -duration:10\"/>" +
                                        "       <command str=\"wait -duration:5\"/>" +
                                        "       <command str=\"roll_measurement -activation_time:5\"/>" +
                                        "   </loop>" +
                                        "</script>";
                                device.uploadScript("SPEEDTEST_ROLL", script.getBytes());
                            } else if (name.equals(COMMAND_START_SCRIPT)) {
                                device.startScript();
                            } else if (name.equals(COMMAND_STOP_SCRIPT)) {
                                device.stopScript();
                            } else if (name.equals(COMMAND_REQUEST_POLQA_LICENSE)) {
                                device.requestPolqa();
                            }else if (name.equals(COMMAND_LOCK_BANDS)) {
                                openBandLockDialog(device);
                            } else if (name.equals(COMMAND_LOCK_GSM)) {
                                device.sendCommand("lock -system:1");
                            } else if (name.equals(COMMAND_LOCK_UMTS)) {
                                device.sendCommand("lock -system:2");
                            } else if (name.equals(COMMAND_LOCK_UMTS_CHANNEL)) {
                                openChannelLockDialog(device, Device.Systems.UMTS, false);
                            } else if (name.equals(COMMAND_LOCK_LTE)) {
                                device.sendCommand("lock -system:128");
                            }else if (name.equals(COMMAND_LOCK_LTE_CHANNEL))  {
                                openChannelLockDialog(device, Device.Systems.LTE, false);
                            } else if (name.equals(COMMAND_LOCK_LTE_PCI))  {
                                openChannelLockDialog(device, Device.Systems.LTE, true);
                            } else if (name.equals(COMMAND_RELEASE_LOCKS)) {
                                device.releaseLocks();
                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).create().show();
        });

        startService();
    }

    private void showLockStatus(Device device, boolean supported) {
        Device.LockStatus bands = device.readLockStatus();
        if (bands == null) {
            Toast.makeText(MainActivity.this, "Failed to read lock status", Toast.LENGTH_SHORT).show();
        } else {
            StringBuilder text = new StringBuilder();
            for (int b : supported ? bands.supportedBands : bands.requestedBands) {
                if (text.length() > 0)
                    text.append(",");
                text.append(device.bandToString(b));
            }
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Bands")
                    .setMessage(text.toString())
                    .create().show();
        }
    }

    private void openBandLockDialog(Device device) {
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(10, 0, 10, 0);

        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);

        EditText bandEdit = new EditText(this);
        bandEdit.setHint("Band ID(s) e.g.: 301,302,303");
        bandEdit.setInputType(InputType.TYPE_CLASS_TEXT);
        bandEdit.setTextSize(18);
        bandEdit.setLayoutParams(llParams);
        ll.addView(bandEdit);

        new AlertDialog.Builder(this)
        .setTitle("Band Lock")
        .setView(ll)
        .setPositiveButton("Lock", (dialog, which) -> {
            try {
                var bands = bandEdit.getText().toString().split(",");
                if (bands.length == 0) {
                    Toast.makeText(this, "Must set band ID(s)", Toast.LENGTH_SHORT).show();
                    return;
                }
                int[] ids = new int[bands.length];
                for (int i = 0; i < ids.length; ++i) {
                    ids[i] = Integer.parseInt(bands[i]);
                }
                device.applyLock(ids);
            } catch (Exception e) {
                e.printStackTrace();
            }
        })
        .setNegativeButton("Cancel", null)
        .create()
        .show();
    }

    private void openChannelLockDialog(Device device, int system, boolean pciLock) {
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(10, 0, 10, 0);

        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);

        EditText channelEdit = new EditText(this);
        channelEdit.setHint("Channel");
        channelEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        channelEdit.setTextSize(18);
        channelEdit.setLayoutParams(llParams);

        EditText pciEdit = new EditText(this);
        pciEdit.setHint("PCI");
        pciEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        pciEdit.setTextSize(18);
        pciEdit.setLayoutParams(llParams);

        EditText bandEdit = new EditText(this);
        bandEdit.setHint("Band");
        bandEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        bandEdit.setTextSize(18);
        bandEdit.setLayoutParams(llParams);

        if (system == 2) {
            ll.addView(bandEdit);
        }
        ll.addView(channelEdit);
        if (pciLock) {
            ll.addView(pciEdit);
        }

        new AlertDialog.Builder(this)
        .setTitle(pciLock ? "PCI Lock" : "Channel Lock")
        .setView(ll)
        .setPositiveButton("Lock", (dialog, which) -> {
            var channel = channelEdit.getText().toString();
            var pci = pciEdit.getText().toString();
            var band = bandEdit.getText().toString();
            if (pciLock) {
                if (channel.isEmpty() || pci.isEmpty()) {
                    Toast.makeText(this, "Must set channel and pci", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                if (system == 2) {
                    if (channel.isEmpty() || band.isEmpty()) {
                        Toast.makeText(this, "Must set channel and band", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    if (channel.isEmpty()) {
                        Toast.makeText(this, "Must set channel", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
            String cmd = "lock -system:" + system + " -channel:" + channel;
            if (pciLock) {
                cmd += " -pci:" + pci;
            } else if (system == Device.Systems.UMTS) {
                cmd += " -band:" + band;
            }
            device.sendCommand(cmd);
        })
        .setNegativeButton("Cancel", null)
        .create()
        .show();
    }

    private String readNumber() throws Exception {
        EditText edit = findViewById(R.id.edit_phone_number);
        if (edit == null)
            return "";
        String number = edit.getText().toString();
        if (number.isEmpty())
            throw new Exception("Empty phone number");
        return number;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Step 2. Call Manager's findBluetoothDevices or connectLocal to initiate connect process
        if (item.getItemId() == R.id.action_find_devices) {
            _manager.findBluetoothDevices();
            return true;
        } else if (item.getItemId() == R.id.action_connect_local) {
            _manager.connectLocal();
            return true;
        } else if (item.getItemId() == R.id.action_start_service) {
            startService();
            return true;
        } else if (item.getItemId() == R.id.action_exit) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(_receiver);

        try {
            for (Connection c : _connections) {
                c.removeListener(this);
                c.close();
            }
            _connections.clear();

            _manager.close();

            unbindService(_service);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    @SuppressWarnings("SameParameterValue")
    private static int adjustSaturation(int color, float v) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= v;
        return Color.HSVToColor(hsv);
    }

    @Override
    public void newConnection(final Connection connection) {
        // Step 3. When new connection is opened the Listener gets notified, at this point you can
        // start listening for updates on device updates, by calling addListener on the Connection
        // and passing in Connection.Listener interface
        runOnUiThread(() -> {
            connection.addListener(MainActivity.this);

            _connections.add(connection);

            Toast.makeText(MainActivity.this, "New connection: " + connection.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void deviceConnected(final Device device) {
        device.addListener(this);

        // Step 4. When new devices are detected on the connection this method gets called, at
        // this point we can start listening for updates on device parameters and controlling the
        // devices, see next Step...
        runOnUiThread(() -> {
            _devices.add(device);

            _adapter.notifyDataSetChanged();

            Toast.makeText(MainActivity.this, "Device connected: " + device.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void locationUpdate(final Connection connection, final GpsLocation location) {
        // Step 12. This method gets called when connection updates it's location
    }

    @Override
    public void deviceDisconnected(final Device device) {
        device.removeListener(this);
        // Step 13. When device is disconnect this method is called. A good place to do some cleanup
        runOnUiThread(() -> {
            _devices.remove(device);
            _adapter.notifyDataSetChanged();
            Toast.makeText(MainActivity.this, "Device disconnected: " + device.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void connectionEnded(final Connection connection) {
        // Step 14. This is the final method that gets called when connection ends.
        runOnUiThread(() -> {
            connection.removeListener(MainActivity.this);

            _connections.remove(connection);

            Toast.makeText(MainActivity.this, "Connection ended: " + connection.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void parameterUpdated(Device device, Device.Value value) {
        // Step 6. When parameter updates this method gets called. Here we check
        // if the parameter in question is DL throughput and if so we print its value.
        // All parameters have unique IDs, refer to parameters.config XML file in the SDK folder
        // for list of IDs
        switch (value.id) {
            case 7:
                //_thruput = value.value != null ? String.format(Locale.US, "%d Bps", (int)(Integer) value.value) : "";
                break;
            case 70004:
                if (value.value != null) {
                    Log.d("RemoteSDK", "LTE channel: " + value.value);
                }
                break;
            case 2:
                if (value.value != null) {
                    Log.d("RemoteSDK", "Call state: " + value.value);
                }
                break;
        }
        refresh();
    }

    @Override
    public void stateUpdated(Device device, int id, int state) {
        // Step 7. Here we get updates on device state changes, such as call and
        // data states...
        refresh();
    }

    @Override
    public void parameterBlockUpdated(Device device, int id, List<Device.Value> values) {
        // Step 8. Here we get updates on table style parameters, which are a set of
        // parameters that form a logical entity, such as UMTS monitored set...
    }

    @Override
    public void scriptStatus(Device device, int state, int progress, String text) {
        // Step 9. Here we get updates on script status changes when test scripts
        // are running...
    }

    @Override
    public void eventUpdate(Device device, int id, String description, Map<String, String> args) {
        // Step 10. Here we get updates on events that occur on the device...
    }

    @Override
    public void testResult(Device device, String title, String description, boolean uplink, Map<String,Object> statistics) {
        // Step 11. Here we get updates of test results
    }

    @Override
    public void errorUpdate(long errors) {
        // Step 12. Here we get device error state information on intervals of 1 second...

        if ((errors & Device.Errors.ERROR_CONNECTION_LOST) != 0) {
            // Connection error
        }
        if ((errors & Device.Errors.ERROR_NOT_RECORDING) != 0) {
            // Not recording
        }
        if ((errors & Device.Errors.ERROR_TRACES_DIED) != 0) {
            // Network parameters not updating
            Log.d("RemoteSDK", "Traces not updating");
        }
        if ((errors & Device.Errors.ERROR_BATTERY_CRITICAL) != 0) {
            // Battery level critical
        }
        if ((errors & Device.Errors.ERROR_DISK_SPACE_CRITICAL) != 0) {
            // Disk critically full
        }
        if ((errors & Device.Errors.ERROR_BATTERY_LOW) != 0) {
            // Battery level low
        }
        if ((errors & Device.Errors.ERROR_DISK_SPACE_LOW) != 0) {
            // Disk space running out
        }
        if ((errors & Device.Errors.ERROR_HIGH_TEMPERATURE) != 0) {
            // Device heating
        }
    }

    @Override
    public void licenseAcquired() {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, "License acquired", Toast.LENGTH_LONG).show());
    }

    @Override
    public void licenseReleased() {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, "License released", Toast.LENGTH_LONG).show());
    }

    private void refresh() {
        _handler.post(this);
    }

    @Override
    public void run() {
    }

    private void startService() {
        //Intent intent = new Intent("com.enhancell.intent.action.START");
        //intent.setPackage("com.enhancell.lib.engine");
        //intent.setComponent(new ComponentName("com.enhancell.lib.engine", "com.enhancell.lib.engine.TraceService"));
        //getApplicationContext().startService(intent);

        Intent intent = new Intent();
        intent.setAction("com.enhancell.intent.action.START");
        ResolveInfo info = getPackageManager().resolveService(intent, 0);
        if (info == null)
            return;
        intent.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));
        bindService(intent, _service, Context.BIND_AUTO_CREATE);
    }
}
