
package com.kelmer.goeurotest;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;

import com.kelmer.goeurotest.model.Location;

public class SearchFormActivity extends Activity {
    
    private boolean mChange = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_form);
        
        final AutoCompleteTextView from = (AutoCompleteTextView) findViewById(R.id.from);
        from.setAdapter(new LocationAutoCompleteAdapter(this, R.layout.list_item));
        from.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                setFrom((Location) parent.getItemAtPosition(position));
                from.requestFocus();
            }
        });
        from.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // clear location
                setFrom(null);
            }
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });
        
     // To text input

        final AutoCompleteTextView to = (AutoCompleteTextView) findViewById(R.id.to);
        to.setAdapter(new LocationAutoCompleteAdapter(this, R.layout.list_item));
        to.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                setTo((Location) parent.getItemAtPosition(position));
                to.requestFocus();
            }
        });
        to.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // clear location
                setTo(null);
            }
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });
    }

    
    private void setFrom(Location loc) {
        if(!mChange) {
            mChange = true;
            AutoCompleteTextView fromView = (AutoCompleteTextView) findViewById(R.id.from);
            fromView.setTag(loc);

            if(loc != null) {
                fromView.setText(loc.getName());
                fromView.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue), PorterDuff.Mode.SRC_ATOP);
                fromView.dismissDropDown();
            }
            else {
                fromView.getBackground().setColorFilter(getResources().getColor(R.color.holo_red), PorterDuff.Mode.SRC_ATOP);
            }
            mChange = false;
        }
    }
    
    private void setTo(Location loc) {
        if(!mChange) {
            mChange = true;
            AutoCompleteTextView toView = (AutoCompleteTextView) findViewById(R.id.to);
            toView.setTag(loc);

            if(loc != null) {
                toView.setText(loc.getName());
                toView.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue), PorterDuff.Mode.SRC_ATOP);
                toView.dismissDropDown();
            }
            else {
                toView.getBackground().setColorFilter(getResources().getColor(R.color.holo_red), PorterDuff.Mode.SRC_ATOP);
            }
            mChange = false;
        }
    }

}
