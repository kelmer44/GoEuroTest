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
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.kelmer.goeurotest.model.RemoteLocation;
import com.kelmer.goeurotest.provider.LocationProvider;
import com.kelmer.goeurotest.util.AndroidUtils;

import java.io.IOException;
import java.util.List;

public class AsyncLocationAutoCompleteTask extends AsyncTask<Void, Void, List<RemoteLocation>> {
    private static final String LOG_TAG = AsyncLocationAutoCompleteTask.class.getName();
    private Context context;
    private String query = "";

    public AsyncLocationAutoCompleteTask(Context context, String query) {
        this.context = context;
        this.query = query;
    }

    @Override
    protected List<RemoteLocation> doInBackground(Void... params) {
        LocationProvider locationProvider = new LocationProvider();

        List<RemoteLocation> loc_list = null;

        try {
            if (AndroidUtils.isNetworkConnected(context)) {
                loc_list = locationProvider.autocompleteLocations(query);
            }
            else {

                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "There was an error processing the request");
        }

        return loc_list;
    }

    @Override
    protected void onPostExecute(List<RemoteLocation> resultList) {

        super.onPostExecute(resultList);
        if (null == resultList)
        {
            //We alert that there is no connection. We need to do it here because from doInBackground we have no access to UI thread
            Toast.makeText(context, context.getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
        }
    }

}
