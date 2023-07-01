package com.benny.openlauncher.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import com.benny.openlauncher.R;
import com.benny.openlauncher.util.AppSettings;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Below is copied from{@link com.benny.openlauncher.activity.ColorActivity}
 * and enables us to use the selected/default theme for the file picker.
 */
public class CustomFilePickerActivity extends FilePickerActivity {
    protected AppSettings _appSettings;
    private String _currentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        _appSettings = AppSettings.get();
        _currentTheme = _appSettings.getTheme();

        if (_appSettings.getTheme().equals("0")) {
            setTheme(R.style.NormalActivity_Light);
        } else if (_appSettings.getTheme().equals("1")) {
            setTheme(R.style.NormalActivity_Dark);
        } else {
            setTheme(R.style.NormalActivity_Black);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(dark(_appSettings.getPrimaryColor(), 0.8));
            getWindow().setNavigationBarColor(_appSettings.getPrimaryColor());
        }
        super.onCreate(savedInstanceState);

        // Get items async, since they are added async
        View rootView = getWindow().getDecorView().getRootView();
        Handler handler = new Handler();
        new Thread(() -> {
            AtomicReference<Toolbar> toolbar = new AtomicReference<>();
            List<View> itemContainers = new ArrayList<>();
            while(toolbar.get() == null)
                handler.post(() -> {
                    toolbar.set(findViewById(R.id.nnf_picker_toolbar));
                    if(toolbar.get() != null)
                        toolbar.get().setBackground(new ColorDrawable(_appSettings.getPrimaryColor()));
                });
            // Check every 250ms if the size changed
            try{
                AtomicBoolean isDestroyed = new AtomicBoolean(false);
                while(!isDestroyed.get()){
                    handler.post(() -> {
                        int oldSize = itemContainers.size();
                        findAll((ViewGroup) getWindow().getDecorView(), R.id.nnf_item_container, itemContainers);
                        int newSize = itemContainers.size();
                        if(oldSize != newSize){
                            for (View _i : itemContainers) {
                                ViewGroup container = (ViewGroup) _i;
                                AppCompatImageView fileIcon = (AppCompatImageView) container.getChildAt(0);
                                fileIcon.setColorFilter(_appSettings.getPrimaryColor());
                            }
                        }
                    });
                    isDestroyed.set(isDestroyed());
                    if(isDestroyed.get()) break;
                    Thread.sleep(250);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    private List<View> findAll(ViewGroup viewGroup, int id) {
        return findAll(viewGroup, id, new ArrayList<>());
    }
    private List<View> findAll(ViewGroup viewGroup, int id, List<View> results) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child.getId() == id) {
                results.add(child);
            } else if (child instanceof ViewGroup) {
                findAll((ViewGroup) child, id, results);
            }
        }
        return results;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!_appSettings.getTheme().equals(_currentTheme)) {
            restart();
        }
    }

    protected void restart() {
        Intent intent = new Intent(this, getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    public static int dark(int color, double factor) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.argb(a, Math.max((int) (r * factor), 0), Math.max((int) (g * factor), 0), Math.max((int) (b * factor), 0));
    }
}
