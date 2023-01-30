package com.example.appblockr.testTimeUse;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appblockr.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;

public class MainTimeUse extends AppCompatActivity {

    Button enableBtn, showBtn;
    TextView permissionDescriptionTv, usageTv;
    ListView appsList;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enableBtn = findViewById(R.id.enable_btn);
        showBtn =  findViewById(R.id.show_btn);
        permissionDescriptionTv =findViewById(R.id.permission_description_tv);
        usageTv =  findViewById(R.id.usage_tv);
        appsList =  findViewById(R.id.apps_list);

        this.loadStatistics();
    }


    // mỗi khi ứng dụng vào foreground -> getGrantStatus và hiển thị các nút tương ứng
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStart() {
        super.onStart();
        if (getGrantStatus()) {
            showHideWithPermission();
            showBtn.setOnClickListener(view -> loadStatistics()); //sau khi ấn btnEnable thì hiện ra btnShow
        } else {
            showHideNoPermission();
            enableBtn.setOnClickListener(view -> startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)));
        }
    }


    /**
     * tải số liệu thống kê sử dụng trong 24 giờ qua
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void loadStatistics() {
        //Cung cấp quyền truy cập vào lịch sử và thống kê sử dụng thiết bị. Dữ liệu sử dụng được tổng hợp thành các khoảng thời gian: ngày, tuần, tháng và năm.
        UsageStatsManager usm = (UsageStatsManager) this.getSystemService(USAGE_STATS_SERVICE);

        //Nhận số liệu thống kê về mức sử dụng ứng dụng trong khoảng thời gian nhất định, được tổng hợp theo khoảng thời gian đã chỉ định.
        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  System.currentTimeMillis() - 1000*3600*24,  System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //lấy cấp API lớn hơn với yêu câu
            appList = appList.stream().filter(app -> app.getTotalTimeInForeground() > 0).collect(Collectors.toList());
        }

        // Nhóm các usageStats theo ứng dụng và sắp xếp chúng theo tổng thời gian ở nền trước
        if (appList.size() > 0) {
            Map<String, UsageStats> mySortedMap = new TreeMap<>(); //lưu trữ các phần tử dưới dạng key/values
            for (UsageStats usageStats : appList) {
                mySortedMap.put(usageStats.getPackageName(), usageStats);
            }
            showAppsUsage(mySortedMap);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void showAppsUsage(Map<String, UsageStats> mySortedMap) {
        //public void showAppsUsage(List<UsageStats> usageStatsList) {
        ArrayList<App> appsList = new ArrayList<>();
        List<UsageStats> usageStatsList = new ArrayList<>(mySortedMap.values());

        // sắp xếp các ứng dụng theo thời gian ở nền trước
        Collections.sort(usageStatsList, (z1, z2) -> Long.compare(z1.getTotalTimeInForeground(), z2.getTotalTimeInForeground()));

        // lấy tổng thời gian sử dụng ứng dụng để tính phần trăm sử dụng cho từng ứng dụng
        long totalTime = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            totalTime = usageStatsList.stream().map(UsageStats::getTotalTimeInForeground).mapToLong(Long::longValue).sum();
        }

        //đầy đủ danh sách app
        for (UsageStats usageStats : usageStatsList) {
            try {
                String packageName = usageStats.getPackageName();
                Drawable icon = getDrawable(R.drawable.no_image);
                String[] packageNames = packageName.split("\\.");
                String appName = packageNames[packageNames.length-1].trim();


                if(isAppInfoAvailable(usageStats)){
                    ApplicationInfo ai = getApplicationContext().getPackageManager().getApplicationInfo(packageName, 0);
                    icon = getApplicationContext().getPackageManager().getApplicationIcon(ai);
                    appName = getApplicationContext().getPackageManager().getApplicationLabel(ai).toString();
                }

                //lấy phần trăm
                String usageDuration = getDurationBreakdown(usageStats.getTotalTimeInForeground());
                int usagePercentage = (int) (usageStats.getTotalTimeInForeground() * 100 / totalTime);

                App usageStatDTO = new App(icon, appName, usagePercentage, usageDuration);
                appsList.add(usageStatDTO);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }


        // đảo ngược danh sách để sử dụng nhiều nhất trước
        Collections.reverse(appsList);
        // xây dựng bộ điều hợp
        AppsAdapter adapter = new AppsAdapter(this, appsList);

        // gắn bộ điều hợp vào ListView
        ListView listView = findViewById(R.id.apps_list);
        listView.setAdapter(adapter);

        showHideItemsWhenShowApps();
    }

    /**
     * kiểm tra xem quyền PACKAGE_USAGE_STATS có được phép cho ứng dụng này không
     * @return true nếu được cấp quyền
     */
    private boolean getGrantStatus() {
        //kiểm soát truy cập và theo dõi ứng dụng
        AppOpsManager appOps = (AppOpsManager) getApplicationContext()
                .getSystemService(Context.APP_OPS_SERVICE);
        //Truy cập vào UsageStatsManager
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getApplicationContext().getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            return (getApplicationContext().checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        } else {
            return (mode == MODE_ALLOWED);
        }
    }

    /**
     * kiểm tra xem thông tin ứng dụng có còn trong thiết bị không/nếu không thì không thể hiển thị chi tiết ứng dụng
     * @return true nếu có thông tin ứng dụng
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean isAppInfoAvailable(UsageStats usageStats) {
        try {
            getApplicationContext().getPackageManager().getApplicationInfo(usageStats.getPackageName(), 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    /**
     * phương thức trợ giúp để nhận chuỗi ở định dạng hh:mm:ss từ mili giây
     * @param millis (thời gian ứng dụng ở nền trước)
     * @return chuỗi ở định dạng hh:mm:ss từ mili giây
     */
    private String getDurationBreakdown(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        //đơn vị đo lường thời gian timeunit
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        return (hours + " h " +  minutes + " m " + seconds + " s");
    }


    /**
     * phương thức trợ giúp được sử dụng để hiển thị/ẩn các mục trong chế độ xem khi không cho phép quyền PACKAGE_USAGE_STATS
     */
    public void showHideNoPermission() {
        enableBtn.setVisibility(View.VISIBLE);
        permissionDescriptionTv.setVisibility(View.VISIBLE);
        showBtn.setVisibility(View.GONE);
        usageTv.setVisibility(View.GONE);
        appsList.setVisibility(View.GONE);

    }

    /**
     * phương thức trợ giúp được sử dụng để hiển thị/ẩn các mục trong chế độ xem khi cho phép PACKAGE_USAGE_STATS
     */
    public void showHideWithPermission() {
        enableBtn.setVisibility(View.GONE);
        permissionDescriptionTv.setVisibility(View.GONE);
        showBtn.setVisibility(View.VISIBLE);
        usageTv.setVisibility(View.GONE); //Chế độ xem này là vô hình và nó không chiếm bất kỳ khoảng trống nào cho mục đích bố cục.
        appsList.setVisibility(View.GONE);
    }

    /**
     * phương thức trợ giúp được sử dụng để hiển thị/ẩn các mục trong chế độ xem khi hiển thị danh sách ứng dụng
     */
    public void showHideItemsWhenShowApps() {
        enableBtn.setVisibility(View.GONE);
        permissionDescriptionTv.setVisibility(View.GONE);
        showBtn.setVisibility(View.GONE);
        usageTv.setVisibility(View.VISIBLE);
        appsList.setVisibility(View.VISIBLE);

    }
}