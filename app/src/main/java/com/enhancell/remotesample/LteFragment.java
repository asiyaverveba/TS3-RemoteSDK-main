package com.enhancell.remotesample;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.verveba.ts3.R;

import java.util.Locale;

public class LteFragment extends Fragment {
    public String TAG = LteFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private OnLteFragmentInteractionListener mListener;
    private LteModel lteModel;
    private FragmentActivity mcoContext;
    private Handler handler_rfvalues = new Handler();
    private String ruuningtech;
    private android.app.AlertDialog alerdialog;
    private boolean pscflag = true;
    public static String[] RSRP = new String[]{"-85.0", "0.0", "-100.0", "0.0"};
    public static String[] SINR = new String[]{"20", "", "15", "0"};
    public static String[] RSRQ = new String[]{"-11.0", "0.0", "-14.0", "0.0"};
    public static String[] RxQual = new String[]{"0.0", "0.0", "0.0", "0.0"};
    public static String[] RXlev = new String[]{"0.0", "0.0", "0.0", "0.0"};
    public static String[] PeakULThroughput = new String[]{"5.0", "0.0", "5.0", "0.0"};
    public static String[] PeakDLThroughput = new String[]{"10.0", "0.0", "10.0", "0.0"};
    public static String[] AvgULThroughput = new String[]{"5.0", "0.0", "5.0", "0.0"};
    public static String[] AvgDLThroughput = new String[]{"10.0", "0.0", "10.0", "0.0"};
    public static String[] Ping = new String[]{"50.0", "0.0", "60.0", "0.0"};

    // LTE PCI
    TextView textvwLtePciValue;
    LinearLayout bgLtePci;

    // NR PCI
    TextView textvwNrPciValue;
    LinearLayout bgNrPci;

    // LTE CHANNEL
    TextView textvwLteChannelValue;
    LinearLayout bgLteChannel;

    // NR CHANNEL
    TextView textvwNrChannelValue;
    LinearLayout bgNrChannel;

    // LTE BAND
    TextView textvwLteBandValue;
    LinearLayout bgLteBand;

    // NR BAND
    TextView textvwNrBandValue;
    LinearLayout bgNrBand;

    TextView textvwssrsrpValue;

    LinearLayout bgssRsrp;

    TextView textvwssRsrqValue;

    LinearLayout bgssRsrq;

    TextView textvwsssinrValue;

    LinearLayout bgsssinr;


    TextView textvwRsrpValue;

    LinearLayout bgRsrp;

    //RSRP_0

    TextView textvwRsrp_0_Value;

    LinearLayout bgRsrp_0;

    //RSRP_1

    TextView textvwRsrp_1_Value;

    LinearLayout bgRsrp_1;

    //RSRQ

    TextView textvwRsrqValue;

    LinearLayout bgRsrq;

    //RSRQ_0

    TextView textvwRsrqValue0;

    LinearLayout bgRsrq_0;

    //RSRQ_1

    TextView textvwRsrqValue1;

    LinearLayout bgRsrq_1;

    //SINR_0

    TextView textvwSinr_0_Value;

    LinearLayout bgSinr0;

    //SINR_1

    TextView textvwSinr_1_Value;

    LinearLayout bgSinr1;


    TextView textvwRssiValue;

    LinearLayout bgRssi;

    TextView textvwCellidValue;

    LinearLayout bgCellid;


    public LteFragment() {
    }

    public static LteFragment newInstance(String param1, String param2) {
        LteFragment fragment = new LteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view_lte = inflater.inflate(R.layout.fragment_lte, container, false);
//        lteViewHolder = new ViewHolder(view_lte);
//
//        unbinder = ButterKnife.bind(this, view_lte);
        mcoContext = getActivity();

        textvwssrsrpValue = view_lte.findViewById(R.id.textvw_ssrsrp_value);
        bgssRsrp= view_lte.findViewById(R.id.bg_ssrsrp);

        textvwssRsrqValue = view_lte.findViewById(R.id.textvw_ssrsrq_value);
        bgssRsrq = view_lte.findViewById(R.id.bg_ssrsrq);

        textvwsssinrValue = view_lte.findViewById(R.id.textvw_sssinr_value);
        bgsssinr = view_lte.findViewById(R.id.bg_sssinr);

        textvwLtePciValue = view_lte.findViewById(R.id.textvw_lte_pci_value);
        bgLtePci = view_lte.findViewById(R.id.linear_layout_lte_pcibg);

        textvwNrPciValue = view_lte.findViewById(R.id.textvw_nr_pci_value);
        bgNrPci = view_lte.findViewById(R.id.linear_layout_nr_pcibg);

        textvwLteChannelValue = view_lte.findViewById(R.id.textvw_lte_channel_value);
        bgLteChannel = view_lte.findViewById(R.id.linear_layout_lte_channelbg);

        textvwNrChannelValue = view_lte.findViewById(R.id.textvw_nr_channel_value);
        bgNrChannel = view_lte.findViewById(R.id.linear_layout_nr_channelbg);

        textvwLteBandValue = view_lte.findViewById(R.id.textvw_lte_band_value);
        bgLteBand = view_lte.findViewById(R.id.linear_layout_lte_bandbg);

        textvwNrBandValue = view_lte.findViewById(R.id.textvw_nr_band_value);
        bgNrBand = view_lte.findViewById(R.id.linear_layout_nr_bandbg);

        textvwRsrpValue = view_lte.findViewById(R.id.textvw_rsrp_value);
        bgRsrp = view_lte.findViewById(R.id.bg_rsrp);

        textvwRsrp_0_Value = view_lte.findViewById(R.id.textvw_rsrp0_value);
        bgRsrp_0 = view_lte.findViewById(R.id.bg_rsrp_0);

        textvwRsrp_1_Value = view_lte.findViewById(R.id.textvw_rsrp1_value);
        bgRsrp_1 = view_lte.findViewById(R.id.bg_rsrp_1);

        textvwRsrqValue = view_lte.findViewById(R.id.textvw_rsrq_value);
        bgRsrq = view_lte.findViewById(R.id.bg_rsrq);

        textvwRsrqValue0 = view_lte.findViewById(R.id.textvw_rsrq_0_value);
        bgRsrq_0 = view_lte.findViewById(R.id.bg_rsrq_0);

        textvwRsrqValue1 = view_lte.findViewById(R.id.textvw_rsrq_1_value);
        bgRsrq_1 = view_lte.findViewById(R.id.bg_rsrq_1);

        textvwSinr_0_Value = view_lte.findViewById(R.id.textvw_sinr_0_value);
        bgSinr0 = view_lte.findViewById(R.id.bg_sinr_0);

        textvwSinr_1_Value = view_lte.findViewById(R.id.textvw_sinr_1_value);
        bgSinr1 = view_lte.findViewById(R.id.bg_sinr_1);

        textvwRssiValue = view_lte.findViewById(R.id.textvw_rssi_value);
        bgRssi = view_lte.findViewById(R.id.bg_rssi);
        textvwCellidValue = view_lte.findViewById(R.id.textvw_ltecellid_value);

        bgCellid = view_lte.findViewById(R.id.bg_cellid);

        return view_lte;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onLteFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLteFragmentInteractionListener) {
            mListener = (OnLteFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnWcdmaFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public void updateRfValues(LteModel lteModel, String ruuningtech) {
        this.lteModel = lteModel;
        this.ruuningtech = ruuningtech;
        updateValues();
    }

    @NonNull
    private String printReal(double value) {
        return String.format(Locale.US, "%.1f", value);
    }

    @NonNull
    private String printReal(double value, @NonNull String unit) {
        return printReal(value) + " " + unit;
    }

    private void updateValues() {
        handler_rfvalues.post(() -> {
            try {
                // NR
                textvwNrChannelValue.setText(String.valueOf(lteModel.getNrChannel()));
                textvwNrBandValue.setText(lteModel.getNrBand());
                textvwNrPciValue.setText(String.valueOf(lteModel.getNrPci()));
                textvwssrsrpValue.setText(printReal(lteModel.getNrSSrsrp(), "dBm"));
                textvwssRsrqValue.setText(printReal(lteModel.getNrSSrsrq(), "dB"));
                textvwsssinrValue.setText(printReal(lteModel.getNrSSsinr(), "dB"));

                // LTE
                textvwLteChannelValue.setText(String.valueOf(lteModel.getLteearfcn()));
                textvwLteBandValue.setText(lteModel.getLteband());
                textvwLtePciValue.setText(String.valueOf(lteModel.getLtepci()));
                textvwRsrpValue.setText(printReal(lteModel.getLtersrp(), "dBm"));
                textvwRsrp_0_Value.setText(printReal(lteModel.getLtersrp0(), "dBm"));
                textvwRsrp_1_Value.setText(printReal(lteModel.getLtersrp1(), "dBm"));
                textvwRsrqValue.setText(printReal(lteModel.getLtersrq(), "dB"));
                textvwRsrqValue0.setText(printReal(lteModel.getLtersrq0(), "dB"));
                textvwRsrqValue1.setText(printReal(lteModel.getLtersrq1(), "dB"));
            } catch (Exception e) {
                Log.e(TAG, "111:" + e.getLocalizedMessage());
            }
        });

        final String cellidhex = Long.toHexString(lteModel.getLteci()).toUpperCase();
        if (!cellidhex.contains("7FFFF")) {
            handler_rfvalues.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        textvwCellidValue.setText(cellidhex);
                    } catch (Exception e) {
                        Log.e(TAG, "222:" + e.getLocalizedMessage());
                    }
                }
            });
        }

        if (lteModel.getNrSSsinr()!= 0)
            handler_rfvalues.post(() -> {
                try {
                    textvwsssinrValue.setText(printReal(lteModel.getNrSSsinr(), "dB"));
                } catch (Exception e) {
                    Log.e(TAG, "333:" + e.getLocalizedMessage());
                }
            });

        if (lteModel.getLtesinr0() != 0)
            handler_rfvalues.post(() -> {
                try {
                    textvwSinr_0_Value.setText(printReal(lteModel.getLtesinr0(), "dB"));
                } catch (Exception e) {
                    Log.e(TAG, "333:" + e.getLocalizedMessage());
                }
            });
        else {

            double valsinr = lteModel.getLterssnr() / 10.0;
            lteModel.setLtesinr0(valsinr);
            handler_rfvalues.post(() -> {
                try {
                    textvwSinr_0_Value.setText(printReal(lteModel.getLtesinr0(), "dB"));
                } catch (Exception e) {
                    Log.e(TAG, "444:" + e.getLocalizedMessage());
                }
            });
        }

        if (lteModel.getLtesinr1() != 0)
            handler_rfvalues.post(() -> {
                try {
                    textvwSinr_1_Value.setText(printReal(lteModel.getLtesinr1(), "dB"));
                } catch (Exception e) {
                    Log.e(TAG, "333:" + e.getLocalizedMessage());
                }
            });
        else {
            double valsinr1 = lteModel.getLterssnr() / 10.0;
            lteModel.setLtesinr1(valsinr1);
            handler_rfvalues.post(() -> {
                try {
                    textvwSinr_1_Value.setText(printReal(lteModel.getLtesinr1(), "dB"));
                } catch (Exception e) {
                    Log.e(TAG, "444:" + e.getLocalizedMessage());
                }
            });
        }

        handler_rfvalues.post(() -> {
            try {
                textvwRssiValue.setText(printReal(lteModel.getLterssi(), "dBm"));
            } catch (Exception e) {
                Log.e(TAG, "555:" + e.getLocalizedMessage());
            }
        });
        if (lteModel.isCellid_matched() && lteModel.isPci_matched()) {
            handler_rfvalues.post(() -> {
                try {
                    bgLtePci.setBackgroundColor(getResources().getColor(R.color.pass_green1));
                } catch (Exception e) {
                    Log.e(TAG, "666:" + e.getLocalizedMessage());
                }
            });
        } else {
            handler_rfvalues.post(() -> {
                try {
                    bgLtePci.setBackgroundColor(getResources().getColor(R.color.fail_red1));
                } catch (Exception e) {
                    Log.e(TAG, "777:" + e.getLocalizedMessage());
                }
            });
        }

        try {
            processEarfcn();
            processCellid();
            processRSRPAndSINR();
            processRSRQ();
            processRSSI();
        } catch (Exception e) {
            Log.d(TAG, "Error in LTe fragment AsyncProcessLteModel 111 : " + e.getMessage());
        }
    }

    public void dismissAlert() {
        if (alerdialog != null)
            alerdialog.dismiss();
    }

    private void processRSSI() {
        double quality = 2 * (lteModel.getLterssi() + 100);
        if (quality > 50)
            handler_rfvalues.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bgRssi.setBackgroundColor(getResources().getColor(R.color.pass_green1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        else if (quality >= 30 && quality < 50)
            handler_rfvalues.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bgRssi.setBackgroundColor(getResources().getColor(R.color.warning_yellow1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        else
            // lteViewHolder.bgRssi.setBackgroundColor(getResources().getColor(R.color.fail_red1));
            handler_rfvalues.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bgRssi.setBackgroundColor(getResources().getColor(R.color.fail_red1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

    }

    private void processCellid() {
        handler_rfvalues.post(() -> {
            try {
                textvwCellidValue.setText(Long.toHexString(lteModel.getLteci()).toUpperCase());
                if (!lteModel.isCellid_matched()) {
                    bgCellid.setBackgroundColor(getResources().getColor(R.color.fail_red1));
                } else
                    bgCellid.setBackgroundColor(getResources().getColor(R.color.pass_green1));
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        });


//        if (StationaryActivity.isTestRunning() == true && ruuningtech.equalsIgnoreCase("LTE")) {
//            if (lteModel.getPrvLteci() == 0) {
//                lteModel.setPrvLteci(lteModel.getLteci());
//                Log.d(TAG, "prv cellids:" + lteModel.getPrvLteci());
//            }
//            if (lteModel.getPrvLteci() != lteModel.getLteci() && pscflag) {
//                if (!TapasTelApi.parameterchange && StationaryActivity.isTestRunning() == true) {
//                    try {
//                        alerts alt = new alerts(getActivity());
//                        alerdialog = alt.AlertPciPsc("Changing CELLID", "CELLID CHANGED");
//                        Log.d("prvci and lteci :", lteModel.getPrvLteci() + " " + lteModel.getLteci());
//                        TapasTelApi.parameterchange = true; // FIXME: 26-09-2017
//                        // fix in alerts //parameterContext.Parameterchange("Ok"); @// FIXME: 18-08-2017
//                        lteModel.setPrvLteci(0);// FIXME: 26-09-2017
//                    } catch (Exception e) {
//                        Timber.d(TAG, "Error in alert show,parameter change " + e.getMessage());
//                    }
//                }
//            }
//        }
    }

    private void processEarfcn() {
        if (lteModel.getPrvearfcn() == 0) {
            lteModel.setPrvearfcn(lteModel.getLteearfcn());
        }
//        if (StationaryActivity.isTestRunning() == true && ruuningtech.equalsIgnoreCase("LTE")) {
//            if (lteModel.getPrvearfcn() != 0 && lteModel.getPrvearfcn() != lteModel.getLteearfcn() && pscflag) {
//                if (!TapasTelApi.parameterchange) {
//                    alerts alt = new alerts(getActivity());
//                    Timber.d("changed EARFCN prvs:" + lteModel.getPrvearfcn() + " new EARFCN:" + lteModel.getLteearfcn());
//                    alerdialog = alt.AlertPciPsc("Changing EARFCN", "EARFCN CHANGED");
//                    TapasTelApi.parameterchange = true;
//                    lteModel.setPrvearfcn(0);
//                }
//            }
//        }

    }

    private void processRSRQ() {
        // RSRQ
        double valrsrq = 0.0;
        try {
            valrsrq = lteModel.getLtersrq();
        } catch (Exception e) {

            valrsrq = 0.0;
            Log.i(TAG, "" + e.getMessage());
        }
        double rsrq_high = 0.0;
        try {
            rsrq_high = Double.parseDouble(RSRQ[0]);
        } catch (Exception e) {
            rsrq_high = 0.0;
            Log.i(TAG, "" + e.getMessage());
        }
        double rsrq_low = 0.0;
        try {
            rsrq_low = Double.parseDouble(RSRQ[2]);
        } catch (Exception e) {

            rsrq_low = 0.0;
            Log.i(TAG, "" + e.getMessage());
        }

        if (valrsrq >= rsrq_high) {
            handler_rfvalues.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bgRsrq.setBackgroundColor(getResources().getColor(R.color.pass_green1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        if (valrsrq < rsrq_high && valrsrq >= rsrq_low)
            handler_rfvalues.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bgRsrq.setBackgroundColor(getResources().getColor(R.color.warning_yellow1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


        if (valrsrq < rsrq_low) {
            handler_rfvalues.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bgRsrq.setBackgroundColor(getResources().getColor(R.color.fail_red1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        // RSRQ_0

        double valrsrq0 = 0.0;
        try {
            valrsrq0 = lteModel.getLtersrq0();
        } catch (Exception e) {
            valrsrq0 = 0.0;
            Log.i(TAG, "" + e.getMessage());
        }
        double rsrq_high0 = 0.0;
        try {
            rsrq_high0 = Double.parseDouble(RSRQ[0]);
        } catch (Exception e) {
            rsrq_high0 = 0.0;
            Log.i(TAG, "" + e.getMessage());
        }
        double rsrq_low0 = 0.0;
        try {
            rsrq_low0 = Double.parseDouble(RSRQ[2]);
        } catch (Exception e) {
            rsrq_low0 = 0.0;
            Log.i(TAG, "" + e.getMessage());
        }

        if (valrsrq0 >= rsrq_high0) {
            handler_rfvalues.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bgRsrq_0.setBackgroundColor(getResources().getColor(R.color.pass_green1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        if (valrsrq0 < rsrq_high0 && valrsrq0 >= rsrq_low0)
            handler_rfvalues.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bgRsrq_0.setBackgroundColor(getResources().getColor(R.color.warning_yellow1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


        if (valrsrq0 < rsrq_low0) {
            handler_rfvalues.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bgRsrq_0.setBackgroundColor(getResources().getColor(R.color.fail_red1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        // RSRQ_1

        double valrsrq1 = 0.0;
        try {
            valrsrq1 = lteModel.getLtersrq1();
        } catch (Exception e) {
            valrsrq1 = 0.0;
            Log.i(TAG, "" + e.getMessage());
        }
        double rsrq_high1 = 0.0;
        try {
            rsrq_high1 = Double.parseDouble(RSRQ[0]);
        } catch (Exception e) {
            rsrq_high1 = 0.0;
            Log.i(TAG, "" + e.getMessage());
        }
        double rsrq_low1 = 0.0;
        try {
            rsrq_low1 = Double.parseDouble(RSRQ[2]);
        } catch (Exception e) {
            rsrq_low1 = 0.0;
            Log.i(TAG, "" + e.getMessage());
        }
        if (valrsrq1 >= rsrq_high1) {
            handler_rfvalues.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bgRsrq_1.setBackgroundColor(getResources().getColor(R.color.pass_green1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        if (valrsrq1 < rsrq_high1 && valrsrq1 >= rsrq_low1)
            handler_rfvalues.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bgRsrq_1.setBackgroundColor(getResources().getColor(R.color.warning_yellow1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        if (valrsrq1 < rsrq_low1) {
            handler_rfvalues.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bgRsrq_1.setBackgroundColor(getResources().getColor(R.color.fail_red1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void processRSRPAndSINR() {

        // RSRP
        double valrsrp = 0.0;
        try {
            valrsrp = lteModel.getLtersrp();
        } catch (Exception e) {
            valrsrp = 0.0;
            Log.i(TAG, "valrsrp " + e.getMessage());
        }
        // RSRP
        double rsrp_high = 0.0;
        try {
            rsrp_high = Double.parseDouble(RSRP[0]);
        } catch (Exception e) {
            rsrp_high = 0.0;
            Log.i(TAG, "rsrp_high " + e.getMessage());
        }
        double rsrp_low = 0.0;
        try {
            rsrp_low = Double.parseDouble(RSRP[2]);
        } catch (Exception e) {
            rsrp_low = 0.0;
            Log.i(TAG, "rsrp_low " + "" + e.getMessage());
        }

        if (valrsrp >= rsrp_high) {
            handler_rfvalues.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bgRsrp.setBackgroundColor(getResources().getColor(R.color.pass_green1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        if (valrsrp < rsrp_high && valrsrp >= rsrp_low)
            handler_rfvalues.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bgRsrp.setBackgroundColor(getResources().getColor(R.color.warning_yellow1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        if (valrsrp < rsrp_low) {
            handler_rfvalues.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bgRsrp.setBackgroundColor(getResources().getColor(R.color.fail_red1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        // RSRP_0
        double valrsrp0 = 0.0;
        try {
            valrsrp0 = lteModel.getLtersrp0();
        } catch (Exception e) {
            valrsrp0 = 0.0;
            Log.i(TAG, "valrsrp0 " + e.getMessage());
        }


        if (valrsrp0 >= rsrp_high) {
            handler_rfvalues.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bgRsrp_0.setBackgroundColor(getResources().getColor(R.color.pass_green1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        if (valrsrp0 < rsrp_high && valrsrp0 >= rsrp_low)
            handler_rfvalues.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bgRsrp_0.setBackgroundColor(getResources().getColor(R.color.warning_yellow1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        if (valrsrp0 < rsrp_low) {
            handler_rfvalues.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bgRsrp_0.setBackgroundColor(getResources().getColor(R.color.fail_red1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        //RSRP_1

        double valrsrp1 = 0.0;
        try {
            valrsrp1 = lteModel.getLtersrp1();
        } catch (Exception e) {
            valrsrp1 = 0.0;
            Log.i(TAG, "valrsrp1 " + e.getMessage());
        }

        if (valrsrp1 >= rsrp_high) {
            handler_rfvalues.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bgRsrp_1.setBackgroundColor(getResources().getColor(R.color.pass_green1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        if (valrsrp1 < rsrp_high && valrsrp1 >= rsrp_low)
            handler_rfvalues.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bgRsrp_1.setBackgroundColor(getResources().getColor(R.color.warning_yellow1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        if (valrsrp1 < rsrp_low) {
            handler_rfvalues.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bgRsrp_1.setBackgroundColor(getResources().getColor(R.color.fail_red1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }


        // SINR_0
        final double valsinr;
        if (lteModel.getLtesinr0() == 0)
            valsinr = lteModel.getLterssnr() / 10.0;
        else
            valsinr = lteModel.getLtesinr0();
        double sinr_high = 0.0;

        try {
            sinr_high = Integer.parseInt(SINR[0]);
        } catch (Exception e) {
            sinr_high = 0.0;
            Log.i(TAG, " sinr_high " + e.getMessage());
        }
        double sinr_low = 0.0;
        try {
            sinr_low = Integer.parseInt(SINR[2]);
        } catch (Exception e) {
            sinr_low = 0.0;
            Log.i(TAG, "sinr_low " + e.getMessage());
        }

        handler_rfvalues.post(() -> {
            try {
                textvwSinr_0_Value.setText(printReal(valsinr, "dB"));
            } catch (Exception e) {
                Log.e(TAG, "valsinr set " + e.getLocalizedMessage());
            }
        });

        if (valsinr >= sinr_high) {
            handler_rfvalues.post(() -> {
                try {
                    bgSinr0.setBackgroundColor(getResources().getColor(R.color.pass_green1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        if (valsinr < sinr_high && valsinr >= sinr_low)
            handler_rfvalues.post(() -> {
                try {
                    bgSinr0.setBackgroundColor(getResources().getColor(R.color.warning_yellow1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        if (valsinr < sinr_low) {
            handler_rfvalues.post(() -> {
                try {
                    bgSinr0.setBackgroundColor(getResources().getColor(R.color.fail_red1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }


        // SINR_1
        final double valsinr1;
        if (lteModel.getLtesinr1() == 0)
            valsinr1 = lteModel.getLterssnr() / 10.0;
        else
            valsinr1 = lteModel.getLtesinr1();

        handler_rfvalues.post(() -> {
            try {
                textvwSinr_1_Value.setText(printReal(valsinr1, "dB"));
            } catch (Exception e) {
                Log.e(TAG, "valsinr set " + e.getLocalizedMessage());
            }
        });
        // SINR_1 color values
        if (valsinr1 >= sinr_high) {
            handler_rfvalues.post(() -> {
                try {
                    bgSinr1.setBackgroundColor(getResources().getColor(R.color.pass_green1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        if (valsinr1 < sinr_high && valsinr1 >= sinr_low)
            handler_rfvalues.post(() -> {
                try {
                    bgSinr1.setBackgroundColor(getResources().getColor(R.color.warning_yellow1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        if (valsinr1 < sinr_low) {
            handler_rfvalues.post(() -> {
                try {
                    bgSinr1.setBackgroundColor(getResources().getColor(R.color.fail_red1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    public interface OnLteFragmentInteractionListener {
        // TODO: Update argument type and name
        void onLteFragmentInteraction();
    }


}
