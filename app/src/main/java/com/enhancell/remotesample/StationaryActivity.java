package com.enhancell.remotesample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.enhancell.redx.network.Systems;
import com.verveba.ts3.R;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.enhancell.remote.Connection;
import com.enhancell.remote.Device;
import com.enhancell.remote.GpsLocation;
import com.enhancell.remote.Manager;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import au.com.bytecode.opencsv.CSVReader;

public class StationaryActivity extends AppCompatActivity implements LteFragment.OnLteFragmentInteractionListener, Manager.Listener, Connection.Listener, Device.Listener, Runnable {

    private interface VoidCallback {
        void execute();
    }

    private static final String LICENSE_USERNAME = "verveba";
    private static final String LICENSE_PASSWORD = "echo123";

    private static final String TAG = "StationaryActivity";
    public static final int CELLID_COLUMN = 3;
    public static final int SITEID_COLUMN = 2;
    public static final int SECTORID_COLUMN = 5;

    //private Button btnstartTest;
    //private float max_dl,avg_dl;
    //private double pingTime;
    private final Handler handler = new Handler();

    private final String LTE_FRAGMENT_TAG = "LTE_FRAGEMENT";
    LteFragment lteFragment = LteFragment.newInstance("", "");
    private Manager _manager;
    private final List<Connection> _connections = new ArrayList<>();
    private final List<Device> _devices = new ArrayList<>();
    private final ServiceReceiver _receiver = new ServiceReceiver();
    private ServiceConnection _service;
    private Handler _handler;
    private DeviceAdapter _adapter;
    private LteModel lteModel = new LteModel();
    private TextView _siteIdTextView, _sectorIdTextView, _channelTextView, _bandTextView, _systemTextView;
    private List cellinfocontent = null;
    private TestButtonView _testButton;
    private double _maxDl, _maxUl, _avgDl, _avgUl, _avgPing;
    private int _moPass, _mtPass;
    private boolean _isTestsRunning = false;
    private boolean _isLicensedAcquired = false;
    private int _servingSystem = Systems.NONE;
    private int _scriptDuration = 0;
    private int _scriptRepeats = 1;

    static class ScriptState {
        public boolean dl = false;
        public boolean ul = false;
        public boolean ping = false;
        public boolean mo = false;
        public boolean mt = false;
    }

    private final ScriptState _scriptState = new ScriptState();

    private final Map<Integer, String> _lastValues = new HashMap<>();

    @Override
    public void onLteFragmentInteraction() {
    }

    private class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case "com.enhancell.intent.action.SERVICE_STARTED":
                    if (intent.getBooleanExtra("ok", false)) {
                        android.util.Log.d("RemoteSDK", "Service started ok");
                    } else {
                        android.util.Log.d("RemoteSDK", "Service error: " + intent.getStringExtra("error"));
                    }
                    break;
                case "com.enhancell.intent.action.SERVICE_STOPPED":
                    android.util.Log.d("RemoteSDK", "Service stopped");
                    Toast.makeText(StationaryActivity.this, "Service stopped", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

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

    @SuppressWarnings("SameParameterValue")
    private static int adjustSaturation(int color, float v) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= v;
        return Color.HSVToColor(hsv);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stationary);
        _siteIdTextView = findViewById(R.id.txtvw_siteId);
        _sectorIdTextView = findViewById(R.id.txtvw_sectorId);
        _channelTextView = findViewById(R.id.txtvw_earfcn);
        _bandTextView = findViewById(R.id.txtvw_band_value);
        _systemTextView = findViewById(R.id.txtvw_system);
        _testButton = findViewById(R.id.testButtonView);
        //btnstartTest=findViewById(R.id.button_starttest);
        _siteIdTextView.setText("UNKNOWN SITE");
        _sectorIdTextView.setText("UNKNOWN SECTOR");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        resetTestResults();

        _handler = new Handler();
        _service = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                android.util.Log.d("RemoteSDK", "Service connected");

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
                android.util.Log.d("RemoteSDK", "Service disconnected");
                Toast.makeText(StationaryActivity.this, "Service disconnected", Toast.LENGTH_LONG).show();
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
        ListView deviceList = findViewById(R.id.list_devices1);
        assert deviceList != null;
        deviceList.setAdapter(_adapter);

        startService();
        setLteFragment();

        _testButton.setClickListener(view -> {
            if (!_devices.isEmpty() && _isLicensedAcquired) {
                if (!_isTestsRunning) {
                    startTests();
                } else {
                    stopTests();
                }
            } else {
                Toast.makeText(StationaryActivity.this, "Wait for device to initialize to start tests...", Toast.LENGTH_LONG).show();
            }
        });

        var lockButton = findViewById(R.id.imageViewHeaderLock);
        lockButton.setOnClickListener(view -> {
            if (_isLicensedAcquired && !_devices.isEmpty()) {
                Device device = _devices.get(0);
                var dlg = new LockDialog(StationaryActivity.this, device);
                dlg.show();
            } else {
                Toast.makeText(StationaryActivity.this,"Wait for device to initialize to set locks...",Toast.LENGTH_LONG).show();
            }
        });

        var homeButton = findViewById(R.id.imageViewHeaderHome);
        homeButton.setOnClickListener(view -> {
            Intent intent=new Intent(StationaryActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        var settingsButton = findViewById(R.id.imageViewHeaderSettings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!_isTestsRunning) {
                    Intent intent = new Intent(StationaryActivity.this, SettingsActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(StationaryActivity.this,"Can't change settings when tests are running...",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showTestResults() {
        Intent intent = new Intent(StationaryActivity.this, StationaryResultsActivity.class);
        intent.putExtra("avgDl", _avgDl);
        intent.putExtra("avgUl", _avgUl);
        intent.putExtra("maxDl", _maxDl);
        intent.putExtra("maxUl", _maxUl);
        intent.putExtra("avgPing", _avgPing);
        intent.putExtra("moPass", _moPass);
        intent.putExtra("mtPass", _mtPass);
        resetTestResults();
        startActivity(intent);
    }

    private void resetTestResults() {
        _avgDl = _avgUl = _maxDl = _maxUl = _avgPing = 0;
        _moPass = _mtPass = -1;
    }

    private void resetScriptStates() {
        _scriptState.dl = _scriptState.ul = _scriptState.ping = _scriptState.mo = _scriptState.mt = false;
    }

    private Runnable testProgress = new Runnable() {
        public void run() {
            if (_isTestsRunning) {
                Toast.makeText(getApplicationContext(),"Test In progress",Toast.LENGTH_SHORT).show();
                handler.postDelayed(testProgress, 3000);
            }

        }
    };

    private void parseDuration(NodeList nodes) {
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            String name = node.getNodeName();
            if (name.equals("command")) {
                try {
                    var str = node.getAttributes().getNamedItem("str").getTextContent();
                    int idx = str.indexOf("-duration:");
                    if (idx >= 0) {
                        str = str.substring(idx + 10);
                        idx = str.lastIndexOf(' ');
                        if (idx < 0) {
                            idx = str.length();
                        }
                        str = str.substring(0, idx);
                        _scriptDuration += Integer.parseInt(str);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (name.equals("loop")) {
                try {
                    _scriptRepeats = Integer.parseInt(node.getAttributes().getNamedItem("repeat").getTextContent());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (node.hasChildNodes()) {
                parseDuration(node);
            }
        }
    }

    private void parseDuration(Node node) {
        if (node.hasChildNodes()) {
            parseDuration(node.getChildNodes());
        }
        Node next = node.getNextSibling();
        if (next != null) {
            parseDuration(next);
        }
    }

    private void parseScriptDuration(@NonNull String xml) {
        _scriptDuration = 0;
        _scriptRepeats = 1;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
            Document doc = builder.parse(input);

            parseDuration(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HardwareIds")
    private void startTests() {
        if (_isTestsRunning)
            return;

        if (_devices.isEmpty())
            return;

        _isTestsRunning = true;
        _testButton.setState(false);

        var device = _devices.get(0);

        var prefs = getSharedPreferences("ts3-demo", Context.MODE_PRIVATE);

        String callNumber = prefs.getString("call_number", Configs.DEFAULT_CALL_NUMBER);
        int testDuration = prefs.getInt("test_duration", Configs.DEFAULT_TEST_DURATION);

        boolean addCallTests = !callNumber.isEmpty();
        if (addCallTests) {
            _moPass = _mtPass = 0;
        }

        String script = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                        "<script>" +
                        "<loop repeat=\"1\">" +
                        "<command str=\"speedtest.net -type:ping -duration:" + testDuration + "\"/>" +
                        "<command str=\"wait -duration:3\"/>" +
                        "<command str=\"speedtest.net -type:download -duration:" + testDuration + "\"/>" +
                        "<command str=\"wait -duration:3\"/>" +
                        "<command str=\"speedtest.net -type:upload -duration:" + testDuration + "\"/>" +
                        "<command str=\"wait -duration:3\"/>";
        if (addCallTests) {
            script +=   "<command str=\"dial -number:" + callNumber + "\"/>" +
                        "<command str=\"wait -duration:" + testDuration + "\"/>" +
                        "<command str=\"hangup\"/>" +
                        "<command str=\"wait -duration:" + testDuration + "\"/>" +
                        "<command str=\"hangup\"/>";
        }
        script +=       "</loop>";
        script +=       "</script>";

        parseScriptDuration(script);

        device.startMeasurement(String.format("remote-%s", new SimpleDateFormat("yyyyMMdd-hhmmssSSS", Locale.US).format(new Date())), UUID.randomUUID().toString(), Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), 0);

        device.uploadScript("TestScript", script.getBytes());
        device.startScript();

        final Object waitLock = new Object();
        new Thread(() -> {
            int totalDuration = _scriptDuration * _scriptRepeats;
            int duration = totalDuration + 2;
            while (duration > 0 && _isTestsRunning) {
                try {
                    int progress = (int) (((float) (totalDuration - duration) / (float) totalDuration) * 100f);
                    _testButton.setProgress(progress);
                    synchronized (waitLock) {
                        waitLock.wait(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                --duration;
            }
            _testButton.setProgress(0);
        }).start();
    }

    private void stopTests() {
        if (!_isTestsRunning)
            return;

        _isTestsRunning = false;

        if (!_devices.isEmpty()) {
            _devices.get(0).stopScript();
        }

        resetScriptStates();

        _testButton.setState(true);

        _devices.get(0).stopMeasurement("");

        showTestResults();
    }

    private void startpingTest()
    {
        Toast.makeText(getApplicationContext(), "Ping Test started !!!", Toast.LENGTH_LONG).show();
        String scriptPing = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<script>" +
                "   <loop repeat=\"100000000\">" +
                "       <command str=\"ping -host:24 216.58.199.174 -bytes:16000 -timeout:120 -interval:2\"/>" +
                "       <command str=\"wait -duration:5\"/>" +
                "   </loop>" +
                "</script>";
        _devices.get(0).uploadScript("ping", scriptPing.getBytes());

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"Ping Test completed !!!",Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(),"FTP DL Started !!!",Toast.LENGTH_LONG).show();

                startFtpDlTest();
            }
        }, 6000);
    }


    private void startFtpDlTest()
    {
        // ftp dl test
        Toast.makeText(getApplicationContext(),"FTP DL Started !!!",Toast.LENGTH_LONG).show();

        //ftp command details
/*
                            Key                  Type              Description
                            port                 int               Port number
                            mode               passive/active      FTP mode
                            direction           dl/ul             Transfer direction
                            host               string              Host address
                            transfers            int         Number of simultaneous transfers
                            username            string             FTP username
                            password            string            FTP password
                            dir                 string         File folder (ul mode only)
                            size                 int          Number of bytes to transfer (ul mode only)
                            file                string          Filename (dl mode only)
                            security            isl/esl           Security protocol
                            timeout              int             Timeout in seconds

                            */


        String script = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<script>" +
                "   <loop repeat=\"100000000\">" +
                "       <command str=\"ftp -port:21 -mode:ACTV -direction:dl -host:54.185.4.132 -username:user6 -password:#useR#! -file:testdl4gb.csv -timeout:120\"/>" +
                "       <command str=\"wait -duration:5\"/>" +
                "   </loop>" +
                "</script>";
        _devices.get(0).uploadScript("ftp", script.getBytes());



    }

    private void startFtpUlTest()
    {

        String script = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                            "<script>" +
                            "   <loop repeat=\"100000000\">" +
                            "       <command str=\"ftp -port:21 -mode:ACTV -direction:Ul -host:54.185.4.132 -username:user6 -password:#useR#! -dir:/Private/testftp -size: -timeout:1200\"/>" +
                            "       <command str=\"wait -duration:5\"/>" +
                            "   </loop>" +
                            "</script>";
        _devices.get(0).uploadScript("ftp", script.getBytes());


    }
    class AsyncTaskCellinfo extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            readCellinfoCSV();
            return null;
        }
    }
    /**
     * to set lte fragment
     */
    private void setLteFragment() {
        // lteRfValuesInterface=(LteRfValuesInterface)lteFragment.getContext();
        fragmentTransaction(lteFragment, LTE_FRAGMENT_TAG);
    }


    /**
     * for fragment transaction
     */
    private void fragmentTransaction(Fragment fragment, String fragement_tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.stationary_action_container, fragment, fragement_tag);
        transaction.commitAllowingStateLoss();
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


    @Override
    public void locationUpdate(Connection connection, GpsLocation gpsLocation) {

    }

    @Override
    public void licenseAcquired() {
        _isLicensedAcquired = true;
        runOnUiThread(() -> Toast.makeText(StationaryActivity.this, "License acquired", Toast.LENGTH_LONG).show());
    }

    @Override
    public void licenseReleased() {
        _isLicensedAcquired = false;
        runOnUiThread(() -> Toast.makeText(StationaryActivity.this, "License released", Toast.LENGTH_LONG).show());
    }

    private void refresh() {
        _handler.post(this);
    }

    private boolean isValidValue(int value) {
        return value != Integer.MAX_VALUE;
    }

    private boolean isValidValue(double value) {
        return value != Double.MAX_VALUE;
    }

    @Override
    public void parameterUpdated(Device device, Device.Value value) {
        // Step 6. When parameter updates this method gets called. Here we check
        // if the parameter in question is DL throughput and if so we print its value.
        // All parameters have unique IDs, refer to parameters.config XML file in the SDK folder
        // for list of IDs

        switch (value.id) {
            case 1: // System
                if (value.value != null) {
                    var system = (int)(long) value.value;
                    if (isValidValue(system) && system != _servingSystem) {
                        _servingSystem = system;

                        if (_servingSystem != Systems.FIVE_G) {
                            lteModel.setNrChannel(0);
                            lteModel.setNrBand("");
                            lteModel.setNrPci(0);
                            lteModel.setNrSSrsrp(0);
                            lteModel.setNrSSrsrq(0);
                            lteModel.setNrSSsinr(0);
                            lteFragment.updateRfValues(lteModel, "LTE");
                        }

                        runOnUi(() -> {
                            String channelText = "";
                            String bandText = "";

                            switch (_servingSystem) {
                                case Systems.LTE: {
                                    var it = _lastValues.get(70004);
                                    if (it != null) {
                                        channelText = it;
                                    }
                                    it = _lastValues.get(70018);
                                    if (it != null) {
                                        bandText = it;
                                    }
                                    break;
                                }
                                case Systems.FIVE_G: {
                                    var it = _lastValues.get(180005);
                                    if (it != null) {
                                        channelText = it;
                                    }
                                    it = _lastValues.get(180006);
                                    if (it != null) {
                                        bandText = it;
                                    }
                                    break;
                                }
                            }

                            _channelTextView.setText(channelText);
                            _bandTextView.setText(bandText);
                            _systemTextView.setText(Systems.toString(_servingSystem));
                        });
                    }
                }
                break;
            case 6: // App UL
                if (value.value != null && _scriptState.ul) {
                    var v = (long) value.value;
                    if (isValidValue(v)) {
                        if (v > _maxUl) {
                            _maxUl = v;
                        }
                        if (_avgUl == 0) {
                            _avgUl = v;
                        } else {
                            _avgUl = (_avgUl + v) / 2;
                        }
                    }
                }
                break;
            case 7: // App DL
                if (value.value != null && _scriptState.dl) {
                    var v = (long) value.value;
                    if (isValidValue(v)) {
                        if (v > _maxDl) {
                            _maxDl = v;
                        }
                        if (_avgDl == 0) {
                            _avgDl = v;
                        } else {
                            _avgDl = (_avgDl + v) / 2;
                        }
                    }
                }
                break;
            case 70004:
                if (value.value != null) {
                     var v = (long) value.value;
                     if (isValidValue(v)) {
                         lteModel.setLteearfcn(v);
                         lteFragment.updateRfValues(lteModel, "LTE");
                         _lastValues.put(value.id, String.valueOf(v));
                         if (_servingSystem == Systems.LTE) {
                             runOnUi(() -> _channelTextView.setText(String.valueOf(v)));
                         }
                     }
                }
                break;
            case 180005:
                if (value.value != null) {
                    var v = (long) value.value;
                    if (isValidValue(v)) {
                        lteModel.setNrChannel(v);
                        lteFragment.updateRfValues(lteModel, "LTE");
                        _lastValues.put(value.id, String.valueOf(v));
                        if (_servingSystem == Systems.FIVE_G) {
                            runOnUi(() -> _channelTextView.setText(String.valueOf(v)));
                        }
                    }
                }
                break;
            case 70000:
                if (value.value != null) {
                    var v = (long) value.value;
                    if (isValidValue(v)) {
                        lteModel.setLtepci(v);
                        lteFragment.updateRfValues(lteModel, "LTE");
                    }
                }
                break;
            case 180007:
                if (value.value != null) {
                    var v = (long) value.value;
                    if (isValidValue(v)) {
                        lteModel.setNrPci(v);
                        lteFragment.updateRfValues(lteModel, "LTE");
                    }
                }
                break;
            case 70009:
                if (value.value != null) {
                    var v = (double) value.value;
                    if (isValidValue(v)) {
                        lteModel.setLtersrp(v);
                        lteFragment.updateRfValues(lteModel, "LTE");
                    }
                }
                break;
            case 70010:
                if (value.value != null) {
                    var v = (double) value.value;
                    if (isValidValue(v)) {
                        lteModel.setLtersrq(v);
                        lteFragment.updateRfValues(lteModel, "LTE");
                    }
                }
                break;
            case 70011:
                if (value.value != null) {
                    var v = (double) value.value;
                    if (isValidValue(v)) {
                        lteModel.setLterssi(v);
                        lteFragment.updateRfValues(lteModel, "LTE");
                    }
                }
                break;

            case 70031:
                if (value.value != null) {
                    var v = (double) value.value;
                    if (isValidValue(v)) {
                        lteModel.setLterssi0(v);
                        lteFragment.updateRfValues(lteModel, "LTE");
                    }
                }
                break;
            case 70032:
                if (value.value != null) {
                    var v = (double) value.value;
                    if (isValidValue(v)) {
                        lteModel.setLterssi1(v);
                        lteFragment.updateRfValues(lteModel, "LTE");
                    }
                }
                break;

            case 70033:
                if (value.value != null) {
                    var v = (double) value.value;
                    if (isValidValue(v)) {
                        lteModel.setLtersrp0(v);
                        lteFragment.updateRfValues(lteModel, "LTE");
                    }
                }
                break;
            case 70034:
                if (value.value != null) {
                    var v = (double) value.value;
                    if (isValidValue(v)) {
                        lteModel.setLtersrp1(v);
                        lteFragment.updateRfValues(lteModel, "LTE");
                    }
                }
                break;

            case 70035:
                if (value.value != null) {
                    var v = (double) value.value;
                    if (isValidValue(v)) {
                        lteModel.setLtersrq0(v);
                        lteFragment.updateRfValues(lteModel, "LTE");
                    }
                }
                break;
            case 70036:
                if (value.value != null) {
                    var v = (double) value.value;
                    if (isValidValue(v)) {
                        lteModel.setLtersrq1(v);
                        lteFragment.updateRfValues(lteModel, "LTE");
                    }
                }
                break;
            case 70037:
                if (value.value != null) {
                    var v = (double) value.value;
                    if (isValidValue(v)) {
                        lteModel.setLtesinr0(v);
                        lteFragment.updateRfValues(lteModel, "LTE");
                    }
                }
                break;
            case 70038:
                if (value.value != null) {
                    var v = (double) value.value;
                    if (isValidValue(v)) {
                        lteModel.setLtesinr1(v);
                        lteFragment.updateRfValues(lteModel, "LTE");
                    }
                }
                break;
            case 70018:
                if (value.value != null) {
                    final String band = ((String) value.value).split("\\s+")[0];
                    lteModel.setLteband(band);
                    lteFragment.updateRfValues(lteModel, "LTE");
                    _lastValues.put(value.id, band);
                    if (_servingSystem == Systems.LTE) {
                        try {
                            runOnUi(() -> _bandTextView.setText(band));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case 180006:
                if (value.value != null) {
                    final String band = ((String) value.value).replace("Band ", "");
                    lteModel.setNrBand(band);
                    lteFragment.updateRfValues(lteModel, "LTE");
                    _lastValues.put(value.id, band);
                    if (_servingSystem == Systems.FIVE_G) {
                        try {
                            runOnUi(() -> _bandTextView.setText(band));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case 70084:
                if (value.value != null) {
                    var v = (long) value.value;
                    if (isValidValue(v)) {
                        lteModel.setLteci(v);
                        lteFragment.updateRfValues(lteModel, "LTE");
                        //runOnUi(() -> updateSiteDataAsperNetworkData());
                    }
                }
                break;
            case 27:
                if (value.value != null && _scriptState.ping) {
                    var v = (long) value.value;
                    if (isValidValue(v)) {
                        if (_avgPing == 0) {
                            _avgPing = v;
                        } else {
                            _avgPing = (_avgPing + v) / 2;
                        }
                    }
                }
                break;
            case 180008: // SS-RSRP
                if (value.value != null) {
                    var v = (double) value.value;
                    if (isValidValue(v)) {
                        lteModel.setNrSSrsrp(v);
                        lteFragment.updateRfValues(lteModel, "LTE");
                    }
                }
                break;
            case 180009: // SS-RSRQ
                if (value.value != null) {
                    var v = (double) value.value;
                    if (isValidValue(v)) {
                        lteModel.setNrSSrsrq(v);
                        lteFragment.updateRfValues(lteModel, "LTE");
                    }
                }
                break;
            case 180012: // SS-SINR
                if (value.value != null) {
                    var v = (double) value.value;
                    if (isValidValue(v)) {
                        lteModel.setNrSSsinr(v);
                        lteFragment.updateRfValues(lteModel, "LTE");
                    }
                }
                break;
        }

        refresh();
    }

    @Override
    public void parameterBlockUpdated(Device device, int i, List<Device.Value> list) {
    }

    @Override
    public void stateUpdated(Device device, int i, int i1) {
    }

    @Override
    public void scriptStatus(Device device, int i, int i1, String s) {
        if (s.equals("Speedtest.net ping")) {
            _testButton.setSubText("Test: Ping");
            _scriptState.ping = true;
            _scriptState.dl = false;
            _scriptState.ul = false;
        } else if (s.equals("Speedtest.net download")) {
            _testButton.setSubText("Test: DL");
            _scriptState.dl = true;
            _scriptState.ul = false;
            _scriptState.ping = false;
        } else if (s.equals("Speedtest.net upload")) {
            _testButton.setSubText("Test: UL");
            _scriptState.ul = true;
            _scriptState.dl = false;
            _scriptState.ping = false;
        } else if (s.startsWith("Wait")) {
            _scriptState.ul = false;
            _scriptState.dl = false;
            _scriptState.ping = false;
        } else if (s.equals("Stopped")) {
            stopTests();
        }
    }

    @Override
    public void eventUpdate(Device device, int i, String s, Map<String, String> map) {
        switch (i) {
            case Device.Events.LockStatusUpdate: {
                _lastValues.clear();
                showToast("Locks updated");
                break;
            }
            case Device.Events.CallConnect: {
                if (_isTestsRunning) {
                    if (_scriptState.mo) {
                        _moPass = 1;
                    }
                }
                break;
            }
            case Device.Events.CallIncoming: {
                if (_isTestsRunning && _scriptState.mt) {
                    _mtPass = 1;
                    _testButton.setSubText("Test: MT call");
                    TelecomManager telecom = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            telecom.acceptRingingCall();
                        } else {
                            showToast("Auto answer needs at least Android API 26");
                        }
                    } else {
                        showToast("No permission to answer calls automatically");
                    }
                }
                break;
            }
            case Device.Events.CallOriginating: {
                if (_isTestsRunning) {
                    _testButton.setSubText("Test: MO call");
                    _scriptState.mo = true;
                }
                break;
            }
            case Device.Events.CallDisconnect: {
                if (_isTestsRunning) {
                    if (_scriptState.mo) {
                        _scriptState.mo = false;
                        _scriptState.mt = true;
                        _testButton.setSubText("Waiting incoming call...");
                    }
                }
                break;
            }
        }
    }

    @Override
    public void testResult(Device device, String s, String s1, boolean b, Map<String, Object> map) {
    }

    @Override
    public void errorUpdate(long l) {
    }

    @Override
    public void newConnection(final Connection connection) {
        // Step 3. When new connection is opened the Listener gets notified, at this point you can
        // start listening for updates on device updates, by calling addListener on the Connection
        // and passing in Connection.Listener interface
        runOnUiThread(() -> {
            connection.addListener(StationaryActivity.this);
            _connections.add(connection);
            //Toast.makeText(StationaryActivity.this, "New connection: " + connection.getName(), Toast.LENGTH_SHORT).show();
            handler.postDelayed(() -> {
                connection.requestLicense(LICENSE_USERNAME, LICENSE_PASSWORD);
            }, 3000);
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
            Toast.makeText(StationaryActivity.this, "Device connected: " + device.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void deviceDisconnected(final Device device) {
        device.removeListener(this);

        // Step 13. When device is disconnect this method is called. A good place to do some cleanup
        runOnUiThread(() -> {
            _devices.remove(device);
            Toast.makeText(StationaryActivity.this, "Device disconnected: " + device.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void connectionEnded(final Connection connection) {
        // Step 14. This is the final method that gets called when connection ends.
        runOnUiThread(() -> {
            connection.removeListener(StationaryActivity.this);
            _connections.remove(connection);
            //Toast.makeText(StationaryActivity.this, "Connection ended: " + connection.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void run() {

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
            handler.removeCallbacks(testProgress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void readCellinfoCSV() {
        FileReader filereader = null;
        CSVReader csvReader = null;
        String path = Environment.getExternalStorageDirectory().toString()
                + DebugConstant.SSV_FOLDER_PATH + "/cellinfo.csv";

        try {
            filereader = new FileReader(path);
            csvReader = new CSVReader(filereader);
            cellinfocontent = csvReader.readAll();
        } catch (FileNotFoundException e1) {
            Log.e(TAG, "File not found exception :" + e1.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException :" + e.getMessage());
        } finally {
            try {
                if (csvReader != null)
                    csvReader.close();
                if (filereader != null)
                    filereader.close();
            } catch (IOException e) {
                Log.e(TAG, "IOException :" + e.getMessage());
            }
        }
    }

    private void updateSiteDataAsperNetworkData() {
        int cnt = 0, ref_cellid = 0;
        String[] row = null;
        int observer_cid = 0;
        String siteid,sectorid;

        if (cellinfocontent != null) {
            for (Object object : cellinfocontent) {
                row = (String[]) object; // parse each record
                if (cnt++ == 0) // skip field description
                    continue;
                try {
                    ref_cellid = Integer.parseInt(row[CELLID_COLUMN], 16);
                } catch (Exception e) {
                    Log.e(TAG, " Error " + e.getMessage());
                }

                // *************** Check for cellid condition only ************
                if (ref_cellid == observer_cid) {
                    try {
                        siteid = row[SITEID_COLUMN];
                        _siteIdTextView.setText(siteid+"");

                    } catch (Exception e) {
                        Log.e(TAG, " Error " + e.getMessage());
                    }
                    try {
                        sectorid = row[SECTORID_COLUMN];
                        _sectorIdTextView.setText(sectorid+"");

                    } catch (Exception e) {
                        Log.e(TAG, " Error " + e.getMessage());
                    }
                    break;

                }//  close for loop
            }
        }
    }

    private void showToast(@NonNull String text) {
        runOnUi(() -> Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show());
    }

    private void runOnUi(@NonNull VoidCallback func) {
        new Thread(() -> runOnUiThread(func::execute)).start();
    }
}