package com.example.appblockr.testTimeUse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.appblockr.R;

import java.util.ArrayList;

public class AppsAdapter extends ArrayAdapter<App> {
    public AppsAdapter(Context context, ArrayList<App> usageStatDTOArrayList) {
        super(context, 0, usageStatDTOArrayList);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Lấy mục dữ liệu cho vị trí này
        App usageStats = getItem(position);

        // Kiểm tra xem một chế độ xem hiện tại có đang được sử dụng lại hay không, nếu không thì hãy tăng cường chế độ xem
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_app, parent, false);
        }

        // Chế độ xem tra cứu cho dân số dữ liệu
        TextView app_name_tv = convertView.findViewById(R.id.app_name_tv);
        TextView usage_duration_tv =  convertView.findViewById(R.id.usage_duration_tv);
        TextView usage_perc_tv = convertView.findViewById(R.id.usage_perc_tv);
        ImageView icon_img =  convertView.findViewById(R.id.icon_img);
        ProgressBar progressBar = convertView.findViewById(R.id.progressBar);


        // Điền dữ liệu vào chế độ xem mẫu bằng cách sử dụng đối tượng dữ liệu
        app_name_tv.setText(usageStats.appName);
        usage_duration_tv.setText(usageStats.usageDuration);
        usage_perc_tv.setText(usageStats.usagePercentage + "%");
        icon_img.setImageDrawable(usageStats.appIcon);
        progressBar.setProgress(usageStats.usagePercentage);

        // Trả lại chế độ xem đã hoàn thành để hiển thị trên màn hình
        return convertView;
    }
}
