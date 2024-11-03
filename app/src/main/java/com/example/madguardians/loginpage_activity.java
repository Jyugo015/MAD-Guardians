package com.example.madguardians;

/*
	 *	This content is generated from the API File Info.
	 *	(Alt+Shift+Ctrl+I).
	 *
	 *	@desc 		
	 *	@file 		loginpage
	 *	@date 		Friday 01st of November 2024 07:54:54 AM
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

public class loginpage_activity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginpage);
		configureSignUpButton();
		configureloginButton();


	
		
		//custom code goes here
	
	}
	private void configureSignUpButton() {
		TextView TVsign_up = (TextView) findViewById(R.id.TVsign_up);
		TVsign_up.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(loginpage_activity.this,signuppage_activity.class));
			}
		});
	}
	private void configureloginButton() {
		Button btnLogin = (Button) findViewById(R.id.login);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(loginpage_activity.this, NavVewBnv.class));
			}
		});
	}
}
	
	