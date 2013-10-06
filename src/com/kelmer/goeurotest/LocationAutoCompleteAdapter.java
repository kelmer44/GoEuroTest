/*    Liberario
 *    Copyright (C) 2013 Torsten Grote
 *
 *    This program is Free Software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as
 *    published by the Free Software Foundation, either version 3 of the
 *    License, or (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kelmer.goeurotest;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.kelmer.goeurotest.model.RemoteLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LocationAutoCompleteAdapter extends ArrayAdapter<RemoteLocation> implements Filterable {

    private List<RemoteLocation> resultList;
    private int resource;
    private Location location;
    
    public LocationAutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        resource = textViewResourceId;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public RemoteLocation getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();

                if (constraint != null) {
                    AsyncLocationAutoCompleteTask autocomplete = new AsyncLocationAutoCompleteTask(getContext(), constraint.toString());

                    try {
                        resultList = autocomplete.execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    if (resultList != null) {
                        Collections.sort(resultList, new SortByProximity());
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            view = inflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }
        ((TextView) view).setText(getItem(position).getNameCountry());

        return view;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    //Since we have no control over the api we need to sort the returned search results
    //programmatically
    class SortByProximity implements Comparator<RemoteLocation> {

        @Override
        public int compare(RemoteLocation lhs, RemoteLocation rhs) {
            //check if there is a location
            if(null!=location)
            {
                Double distance1 = getDistance(location.getLatitude(), location.getLongitude(), lhs.getLatitude(), lhs.getLongitude());
                Double distance2 = getDistance(location.getLatitude(), location.getLongitude(), rhs.getLatitude(), rhs.getLongitude());
                return distance1.compareTo(distance2);
            }
            return 0;
        }
        
        //Haversine formula
        //ref: http://phreakhoughts.blogspot.com.es/2011/11/calculate-distance-between-2-geo-points.html
        public double getDistance(double lat1, double lon1, double lat2, double lon2)
        {
            int R = 6371; // radius of earth in km
            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lon2 - lon1);
            double lat1mod = Math.toRadians(lat1);
            double lat2mod = Math.toRadians(lat2);

            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1mod) * Math.cos(lat2mod);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double d = R * c;

            return d;
        }
    }

}
