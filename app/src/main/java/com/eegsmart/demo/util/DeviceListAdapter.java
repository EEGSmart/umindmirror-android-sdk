package com.eegsmart.demo.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eegsmart.blesdk.scanner.SleepDevice;

import java.util.ArrayList;
import java.util.Iterator;

import com.eegsmart.demo.R;

public class DeviceListAdapter extends BaseAdapter {
    private ArrayList<SleepDevice> mDeviceBoxes;
    private LayoutInflater mInflater;

    public DeviceListAdapter(LayoutInflater inflater) {
        super();
        mDeviceBoxes = new ArrayList<>();
        mInflater = inflater;
    }

    public void addDevice(ArrayList<SleepDevice> list) {
        clear();
        mDeviceBoxes.addAll(list);
        notifyDataSetChanged();
    }

    public void clearAllDevices() {
        if (!mDeviceBoxes.isEmpty()) {
            Iterator iterator = mDeviceBoxes.iterator();
            while (iterator.hasNext()) {
                iterator.remove();
            }
        }
    }

    public void removeDevice(int pos) {
        if (mDeviceBoxes.size() > pos && pos >= 0) {
            mDeviceBoxes.remove(pos);
        }
    }

    public SleepDevice getDevice(int position) {
        return mDeviceBoxes.get(position);
    }

    public void clear() {
        mDeviceBoxes.clear();
    }

    @Override
    public int getCount() {
        return mDeviceBoxes.size();
    }

    @Override
    public Object getItem(int i) {
        return mDeviceBoxes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = mInflater.inflate(R.layout.device_item, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceName = (TextView) view.findViewById(R.id.nameTv);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        SleepDevice device = mDeviceBoxes.get(i);
        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0) {
            viewHolder.deviceName.setText(deviceName);
        } else {
            viewHolder.deviceName.setText("unKnown");
        }
        return view;
    }

    static class ViewHolder {
        TextView deviceName;
    }
}