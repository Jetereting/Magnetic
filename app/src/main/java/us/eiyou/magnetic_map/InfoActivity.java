package us.eiyou.magnetic_map;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        initView();
    }
    private void initView() {
        Bmob.initialize(this, "52c499abafd075320161de647bdf5dfa");
        final TableLayout tableLayout = (TableLayout) findViewById(R.id.tl);
        tableLayout.setStretchAllColumns(true);
        BmobQuery<Info> bmobQuery = new BmobQuery<>();
        bmobQuery.order("-createdAt");
        bmobQuery.findObjects(getApplicationContext(), new FindListener<Info>() {
            @Override
            public void onSuccess(List<Info> list) {
                Toast.makeText(getApplicationContext(), list.size() + "", Toast.LENGTH_LONG).show();
                for (int row = 0; row < list.size(); row++) {
                    TableRow tableRow = new TableRow(getApplicationContext());
                    Info express_info = list.get(row);
                    TextView textView = new TextView(getApplicationContext());
                    textView.setPadding(0, 0, 20, 0);
                    textView.setTextColor(Color.parseColor("#000000"));
                    textView.setText(express_info.getCreatedAt().substring(2));
                    tableRow.addView(textView);
                    TextView textView0 = new TextView(getApplicationContext());
                    textView0.setPadding(0, 0, 20, 0);
                    textView0.setTextColor(Color.parseColor("#000000"));
                    textView0.setText(express_info.getLatitude()+"");
                    tableRow.addView(textView0);
                    TextView textView1 = new TextView(getApplicationContext());
                    textView1.setText(express_info.getLongitude()+"");
                    textView1.setPadding(0, 0, 20, 0);
                    textView1.setTextColor(Color.parseColor("#000000"));
                    tableRow.addView(textView1);
                    TextView textView2 = new TextView(getApplicationContext());
                    textView2.setText(express_info.getCichang());
                    textView2.setPadding(0, 0, 20, 0);
                    textView2.setTextColor(Color.parseColor("#000000"));
                    tableRow.addView(textView2);
                    tableLayout.addView(tableRow);
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(getApplicationContext(), "查询失败" + s, Toast.LENGTH_LONG).show();
            }
        });
    }
}
