package com.example.appblockr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appblockr.adapter.LockedAppAdapter;
import com.example.appblockr.model.AppModel;
import com.example.appblockr.shared.SharedPrefUtil;

import java.util.ArrayList;
import java.util.List;

public class LockedApps extends AppCompatActivity {

    RecyclerView recyclerView; //giúp tạo danh sách động
    List<AppModel> apps = new ArrayList<>();
    LockedAppAdapter adapter;
    ProgressDialog progressDialog; //hộp thoại ngăn người dùng tương tác với ứng dụng
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locked_apps);
        recyclerView = findViewById(R.id.lockedAppsListt);
        adapter = new LockedAppAdapter(apps, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        progressDialog = new ProgressDialog(this);
        progressDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                getLockedApps();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.setTitle("Fetching Apps");
        progressDialog.setMessage("Loading");
        progressDialog.show();
    }

    public void getLockedApps() {
        List<String> prefAppList = SharedPrefUtil.getInstance(this).getLockedAppsList();
        List<ApplicationInfo> packageInfos = getPackageManager().getInstalledApplications(0);
        for (int i = 0; i < packageInfos.size(); i++) {
            if (packageInfos.get(i).icon > 0) {
                String name = packageInfos.get(i).loadLabel(getPackageManager()).toString();
                Drawable icon = packageInfos.get(i).loadIcon(getPackageManager());
                String packageName = packageInfos.get(i).packageName;
                //1 là khóa, 0 là mở khóa
                if (prefAppList.contains(packageName)) {
                    apps.add(new AppModel(name, icon, 1, packageName));
                } else {
                    continue;
                }

            }

        }
        adapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }
}