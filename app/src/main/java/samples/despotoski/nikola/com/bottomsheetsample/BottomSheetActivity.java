package samples.despotoski.nikola.com.bottomsheetsample;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetActivity extends AppCompatActivity {

    private TextView mStateText;
    private TextView mOffsetText;
    private BottomSheetBehavior mBottomSheetBehavior;
     RecyclerView recyclerView,recyclerView2;
    LinearLayoutManager linearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_sheet);
         recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView2= (RecyclerView) findViewById(R.id.recyclerview2);
        mOffsetText = (TextView) findViewById(R.id.offsetText);
        mStateText = (TextView) findViewById(R.id.stateText);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
          linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        linearLayoutManager.setAutoMeasureEnabled(true);
        ApplicationAdapter adapter = new ApplicationAdapter(this, listApplications(this));
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);


        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        linearLayoutManager.setAutoMeasureEnabled(true);
        ApplicationAdapter adapter2 = new ApplicationAdapter(this, listApplications(this));
        recyclerView2.setLayoutManager(linearLayoutManager);
        recyclerView2.setAdapter(adapter2);

        FrameLayout parentThatHasBottomSheetBehavior = (FrameLayout) recyclerView.getParent().getParent();
        mBottomSheetBehavior = BottomSheetBehavior.from(parentThatHasBottomSheetBehavior);
        if (mBottomSheetBehavior != null) {
            setStateText(mBottomSheetBehavior.getState());
            mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    setStateText(newState);
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    setOffsetText(slideOffset);
                }
            });
        }

        View peekButton = findViewById(R.id.peek_me);
        peekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Let's peek it, programmatically
                View peakView = findViewById(R.id.drag_me);
               // mBottomSheetBehavior.setPeekHeight(peakView.getHeight());
                peakView.requestLayout();
            }
        });
        View modal = findViewById(R.id.as_modal);
        modal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new CustomBottomSheetDialogFragment();
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    private void setOffsetText(float slideOffset) {
        mOffsetText.setText(getString(R.string.offset, slideOffset));
    }

    private void setStateText(int newState) {
        mStateText.setText(getStateAsString(newState));
    }

    public  int getStateAsString(int newState) {
        switch (newState) {
            case BottomSheetBehavior.STATE_COLLAPSED:
                linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
                linearLayoutManager.setAutoMeasureEnabled(true);
                recyclerView.setLayoutManager(linearLayoutManager);

                linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
                linearLayoutManager.setAutoMeasureEnabled(true);
                recyclerView2.setLayoutManager(linearLayoutManager);
                return R.string.collapsed;
            case BottomSheetBehavior.STATE_DRAGGING:

                linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
                linearLayoutManager.setAutoMeasureEnabled(true);
                recyclerView.setLayoutManager(linearLayoutManager);

                linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
                linearLayoutManager.setAutoMeasureEnabled(true);
                recyclerView2.setLayoutManager(linearLayoutManager);

                return R.string.dragging;
            case BottomSheetBehavior.STATE_EXPANDED:
                linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
                linearLayoutManager.setAutoMeasureEnabled(true);
                recyclerView.setLayoutManager(linearLayoutManager);

                linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
                linearLayoutManager.setAutoMeasureEnabled(true);
                recyclerView2.setLayoutManager(linearLayoutManager);
                return R.string.expanded;
            case BottomSheetBehavior.STATE_HIDDEN:
                return R.string.hidden;
            case BottomSheetBehavior.STATE_SETTLING:
                Log.e("Settling","Settling");
                return R.string.settling;
        }
        return R.string.undefined;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bottom_sheet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static List<ApplicationInfo> listApplications(Context context) {
        int flags = PackageManager.GET_META_DATA;
        List<ApplicationInfo> installedApps = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> applications = pm.getInstalledApplications(flags);
        for (ApplicationInfo appInfo : applications) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
                installedApps.add(appInfo);
            }
        }
        return installedApps;
    }

    public void setHorizontal(RecyclerView recyclerView){
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        linearLayoutManager.setAutoMeasureEnabled(true);
        ApplicationAdapter adapter = new ApplicationAdapter(this, listApplications(this));
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }


    public void setVertical(RecyclerView recyclerView){
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        linearLayoutManager.setAutoMeasureEnabled(true);
        ApplicationAdapter adapter = new ApplicationAdapter(this, listApplications(this));
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }
}
