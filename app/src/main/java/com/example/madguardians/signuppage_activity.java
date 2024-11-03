package com.example.madguardians;

/*
	 *	This content is generated from the API File Info.
	 *	(Alt+Shift+Ctrl+I).
	 *
	 *	@desc 		
	 *	@file 		signuppage
	 *	@date 		Friday 01st of November 2024 07:54:30 AM
	 *	@title 		Main
	 *	@author 	
	 *	@keywords 	
	 *	@generator 	Export Kit v1.3.figma
	 *
	 */
	


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class signuppage_activity extends Activity {


	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.signuppage);
		configureLogInButton();
	

		//custom code goes here
	
	}

	private void configureLogInButton() {
		TextView logInHere = (TextView) findViewById(R.id.log_in_here_ek1);
		logInHere.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(signuppage_activity.this,loginpage_activity.class));
			}
		});
	}


}
	
	