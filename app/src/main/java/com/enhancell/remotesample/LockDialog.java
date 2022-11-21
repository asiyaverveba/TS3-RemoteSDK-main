package com.enhancell.remotesample;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.enhancell.redx.network.Systems;
import com.enhancell.redx.network.band.Bands;
import com.enhancell.remote.Device;

import java.util.ArrayList;
import java.util.List;

public class LockDialog {

    private final @NonNull Context _context;
    private final @NonNull Device _device;

    public LockDialog(@NonNull Context context, @NonNull Device device) {
        _context = context;
        _device = device;
    }

    public void show() {
        String[] items = { "Release all locks", "Band lock", "LTE channel lock" };

        new AlertDialog.Builder(_context)
                .setTitle("Locking")
                .setItems(items, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            _device.releaseLocks();
                            break;
                        case 1:
                            openBandLockDialog();
                            break;
                        case 2:
                            openChannelLockDialog();
                            break;
                    }
                })
                .create()
                .show();
    }

    private void openBandLockDialog() {
        var lockStatus = _device.readLockStatus();

        final int[] ids = lockStatus.supportedBands;
        int[] reqIds = lockStatus.requestedBands;

        if (reqIds.length == 0) {
            reqIds = ids;
        }

        List<String> bands = new ArrayList<>();
        for (int id : ids) {
            bands.add(Systems.toString(Bands.systemFromBand(id)) + " " + _device.bandToString(id));
        }

        final var listView = new ListView(_context);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(new ArrayAdapter<>(_context, android.R.layout.simple_list_item_multiple_choice, bands));

        for (int i = 0; i < listView.getAdapter().getCount(); ++i) {
            int id = ids[i];
            boolean found = false;
            for (int reqId : reqIds) {
                if (id == reqId) {
                    found = true;
                    break;
                }
            }
            listView.setItemChecked(i, found);
        }

        LinearLayout ll = new LinearLayout(_context);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(10, 0, 10, 0);

        int buttonHeight = 100;
        var buttonParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, buttonHeight);

        Button checkAllButton = new Button(_context);
        checkAllButton.setLayoutParams(buttonParams);
        checkAllButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonHeight * 0.25f);
        checkAllButton.setText("Check all");
        checkAllButton.setOnClickListener(view -> {
            for (int i = 0; i < listView.getAdapter().getCount(); ++i) {
                listView.setItemChecked(i, true);
            }
        });

        Button uncheckAllButton = new Button(_context);
        uncheckAllButton.setLayoutParams(buttonParams);
        uncheckAllButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonHeight * 0.25f);
        uncheckAllButton.setText("Uncheck all");
        uncheckAllButton.setOnClickListener(view -> {
            for (int i = 0; i < listView.getAdapter().getCount(); ++i) {
                listView.setItemChecked(i, false);
            }
        });

        ll.addView(checkAllButton);
        ll.addView(uncheckAllButton);
        ll.addView(listView);

        new AlertDialog.Builder(_context)
                .setTitle("Band Lock")
                .setView(ll)
                .setPositiveButton("Lock", (dialogInterface, i) -> {
                    final var items = listView.getCheckedItemPositions();
                    List<Integer> lockIds = new ArrayList<>();
                    for (int pos = 0; pos < items.size(); ++pos) {
                        var checked = items.get(pos);
                        if (checked) {
                            lockIds.add(ids[items.keyAt(pos)]);
                        }
                    }
                    if (lockIds.isEmpty() || lockIds.size() == ids.length) {
                        _device.releaseLocks();
                    } else {
                        _device.applyLock(Utils.toIntArray(lockIds));
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void openChannelLockDialog() {
        LinearLayout ll = new LinearLayout(_context);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(10, 0, 10, 0);

        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);

        EditText channelEdit = new EditText(_context);
        channelEdit.setHint("Channel");
        channelEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        channelEdit.setTextSize(18);
        channelEdit.setLayoutParams(llParams);

        EditText pciEdit = new EditText(_context);
        pciEdit.setHint("PCI");
        pciEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        pciEdit.setTextSize(18);
        pciEdit.setLayoutParams(llParams);

        ll.addView(channelEdit);
        ll.addView(pciEdit);

        new AlertDialog.Builder(_context)
                .setTitle("Channel Lock")
                .setView(ll)
                .setPositiveButton("Lock", (dialog, which) -> {
                    var channel = channelEdit.getText().toString();
                    var pci = pciEdit.getText().toString();
                    if (channel.isEmpty()) {
                        Toast.makeText(_context, "Must set channel", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String cmd = "lock -system:" + Device.Systems.LTE + " -channel:" + channel;
                    if (!pci.isEmpty()) {
                        cmd += " -pci:" + pci;
                    }
                    _device.sendCommand(cmd);
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
