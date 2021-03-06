
package com.kelmer.goeurotest;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.kelmer.goeurotest.model.RemoteLocation;
import com.kelmer.goeurotest.util.LocationHelper;
import com.kelmer.goeurotest.util.LocationHelper.LocationResult;

import java.text.DateFormat;
import java.util.Calendar;

public class SearchFormActivity extends FragmentActivity {

    private boolean mChange = false;
    private boolean fromSet = false;
    private boolean toSet = false;
    private AutoCompleteTextView from;
    private Button dateButton;
    private EditText textDate;
    private AutoCompleteTextView to;
    private Button searchButton;
    
    private LocationAutoCompleteAdapter fromAdapter;
    private LocationAutoCompleteAdapter toAdapter;
    /**
     * Helpers para obtener la posición actual
     */
    private Location mCurrentLocation;
    private LocationHelper mLocationHelper;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_form);

        from = (AutoCompleteTextView) findViewById(R.id.from);
        dateButton = (Button) findViewById(R.id.dateButton);
        textDate = (EditText) findViewById(R.id.dateText);
        to = (AutoCompleteTextView) findViewById(R.id.to);
        searchButton = (Button) findViewById(R.id.searchButton);
        
        
  //from search input
        fromAdapter = new LocationAutoCompleteAdapter(this, R.layout.list_item);
        from.setAdapter(fromAdapter);
        from.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                setFrom((RemoteLocation) parent.getItemAtPosition(position));
                from.requestFocus();
            }
        });
        from.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // clear location
                setFrom(null);
            }

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        // To text input
        toAdapter = new LocationAutoCompleteAdapter(this, R.layout.list_item);
        to.setAdapter(toAdapter);
        to.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                setTo((RemoteLocation) parent.getItemAtPosition(position));
                to.requestFocus();
            }
        });
        to.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // clear location
                setTo(null);
            }

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        
        //date picker
        final DateFormat df = android.text.format.DateFormat.getDateFormat(this);
        Calendar date = Calendar.getInstance();
        textDate.setText(df.format(date.getTime()));
        dateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                
                Calendar newCalendar = Calendar.getInstance();
                DatePickerDialog dateDialog = new DatePickerDialog(SearchFormActivity.this, new OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                            int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        textDate.setText(df.format(newDate.getTime()));
                    }

                },newCalendar.get(Calendar.YEAR)
                 ,newCalendar.get(Calendar.MONTH)
                 ,newCalendar.get(Calendar.DAY_OF_MONTH));

                dateDialog.show();
            }
        });
        
        
        //search button
        searchButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                showAlertDialog(getResources().getString(R.string.information), getResources().getString(R.string.search));
            }
        });
        
        

        //Get current location
        mLocationHelper = new LocationHelper();
        mLocationHelper.getLocation(SearchFormActivity.this, locationResult);
        
      
        
    }

    
    
    private LocationResult locationResult = new LocationResult() {
        @Override
        public void gotLocation(Location location) {
            if (location != null) {
                mCurrentLocation = location;
                fromAdapter.setLocation(mCurrentLocation);
                toAdapter.setLocation(mCurrentLocation);
            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SearchFormActivity.this, getResources().getString(R.string.error_no_location), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };
    
    protected void checkIfSearchable() {
        if(fromSet && toSet)
        {
            searchButton.setEnabled(true);
        }
    }



    protected void showAlertDialog(String title, String message) {
        Builder alert = new AlertDialog.Builder(SearchFormActivity.this);
        alert.setTitle(title);
        alert.setMessage(message);
        //default android ok message
        alert.setPositiveButton(getResources().getString(android.R.string.ok),null);
        alert.show(); 
    }



    private void setFrom(RemoteLocation loc) {
        if (!mChange) {
            mChange = true;
            AutoCompleteTextView fromView = (AutoCompleteTextView) findViewById(R.id.from);
            fromView.setTag(loc);

            if (loc != null) {
                fromView.setText(loc.getName());
                fromView.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue), PorterDuff.Mode.SRC_ATOP);
                fromView.dismissDropDown();
                fromSet = true;
                checkIfSearchable();
            }
            else {
                fromView.getBackground().setColorFilter(getResources().getColor(R.color.holo_red), PorterDuff.Mode.SRC_ATOP);
                fromSet = false;
                searchButton.setEnabled(false);
            }
            mChange = false;
        }
    }

    private void setTo(RemoteLocation loc) {
        if (!mChange) {
            mChange = true;
            AutoCompleteTextView toView = (AutoCompleteTextView) findViewById(R.id.to);
            toView.setTag(loc);

            if (loc != null) {
                toView.setText(loc.getName());
                toView.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue), PorterDuff.Mode.SRC_ATOP);
                toView.dismissDropDown();
                toSet = true;
                checkIfSearchable();
            }
            else {
                toView.getBackground().setColorFilter(getResources().getColor(R.color.holo_red), PorterDuff.Mode.SRC_ATOP);
                toSet = false;
                searchButton.setEnabled(false);
            }
            mChange = false;
        }
    }

}
