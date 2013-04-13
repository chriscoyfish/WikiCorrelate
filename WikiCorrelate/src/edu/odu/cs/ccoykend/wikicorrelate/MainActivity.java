package edu.odu.cs.ccoykend.wikicorrelate;

import java.util.Vector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Main activity class for WikiCorrelator Android application
 * @author Chris Coykendall
 *
 */
public class MainActivity extends Activity {

	WikiCorrelator wc;
	ProgressDialog pd;
	Vector<String> results;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button btnCorrelate = (Button)findViewById(R.id.btnCorrelate);
		/**
		 * Set up the Correlate button to run the WikiCorrelator
		 */
		btnCorrelate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				/**
				 *  Hide virtual keyboard
				 */
				getWindow().setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
				
				pd = new ProgressDialog(MainActivity.this);
				pd.setTitle(getString(R.string.pleasewait));
				pd.setMessage(getString(R.string.datawarning));
				pd.show();
				
				TextView tvSrc = (TextView) findViewById(R.id.etSource);
				String src = tvSrc.getText().toString();
				TextView tvDest = (TextView) findViewById(R.id.etTarget);
				String dest = tvDest.getText().toString();
				
				/**
				 * Create new WikiCorrelator and thread to run it in.
				 */
				wc = new WikiCorrelator(src,dest,3,0,5);
				
				new Thread() {
			        public void run() {
			        	results = wc.find();
			        	final TextView res;
						res = (TextView) findViewById(R.id.tvResults);
						res.post(new Runnable() {
							@Override
							public void run() {
								String resTxt="";
					    		if (results==null) resTxt="Error!";
					    		else if (results.size()==0) 
					    			resTxt="No correlation found!";
					    		else {
					    			for (String s : results) {
					    				resTxt+=s + "\n";
					    			}
					    		}
					    		if (res!=null) res.setText(resTxt);
							}
						});
						if (pd!=null) pd.dismiss();
			        }
			    }.start();
			      
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
