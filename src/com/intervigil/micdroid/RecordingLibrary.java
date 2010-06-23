package com.intervigil.micdroid;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RecordingLibrary extends ListActivity {
	private RecordingAdapter libraryAdapter;
	private ArrayList<Recording> recordings;

	/**
     * Called when the activity is starting.  This is where most
     * initialization should go: calling setContentView(int) to inflate
     * the activity's UI, etc.
     * 
     * @param   savedInstanceState	Activity's saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recording_library);
        
        recordings = new ArrayList<Recording>();
        this.libraryAdapter = new RecordingAdapter(this, R.layout.library_row, recordings);
        setListAdapter(libraryAdapter);
		new LoadRecordingsTask().execute((Void)null);
		libraryAdapter.notifyDataSetChanged();
    }
    
    @Override
    protected void onStart() {
        Log.i(getPackageName(), "onStart()");
        super.onStart();
    }
    
    @Override
    protected void onResume() {
    	Log.i(getPackageName(), "onResume()");
    	super.onResume();
    }
    
    @Override
    protected void onPause() {
    	Log.i(getPackageName(), "onPause()");
    	super.onPause();
    }
    
    @Override
    protected void onStop() {
    	Log.i(getPackageName(), "onStop()");
    	super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(getPackageName(), "onSaveInstanceState()");
        super.onSaveInstanceState(outState);
    }
    
    private class RecordingAdapter extends ArrayAdapter<Recording> {		
		public RecordingAdapter(Context context, int textViewResourceId, List<Recording> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView (int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.library_row, null);
            }
            Recording r = this.getItem(position);
            if (r != null) {
                TextView first = (TextView) v.findViewById(R.id.row_first_line);
                TextView second = (TextView) v.findViewById(R.id.row_second_line);
            	first.setText("Name: " + r.getRecordingName());
            	second.setText("Length: " + r.getRecordingLength());
            }
            return v;
        }
    }
    
    private class LoadRecordingsTask extends AsyncTask<Void, Void, Void> {
    	// Async load all the recordings already in the directory
    	private final ProgressDialog spinner = new ProgressDialog(RecordingLibrary.this);
    	WaveReader reader;
    	
    	@Override
    	protected void onPreExecute() {
    		this.spinner.setMessage("Loading");
    		this.spinner.show();
    	}
    	
		@Override
		protected Void doInBackground(Void... params) {
			File libraryDir = new File(getLibraryDirectory());
			File[] waveFiles = libraryDir.listFiles();
			
			for (int i = 0; i < waveFiles.length; i++) {
				reader = new WaveReader(waveFiles[i]);
				try {
					reader.OpenWave();
					Recording r = new Recording(waveFiles[i].getName(), reader.GetLength());
					recordings.add(r);
					Log.d("LoadRecordings", String.format("added recording %s", r.getRecordingName()));
					reader.CloseWaveFile();
					reader = null;
				} catch (IOException e) {
					e.printStackTrace();
					Log.d("RecordingLibrary", "Non-wave file found in library!");
				}
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			this.spinner.dismiss();
			libraryAdapter.notifyDataSetChanged();
		}
    }
    
    private String getLibraryDirectory() {
    	return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getPackageName() + File.separator + "library";
    }
}