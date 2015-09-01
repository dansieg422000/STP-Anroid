package com.z.stproperty;

/*********************************************************************************************************
 * Class	: LoanCalculator
 * Type		: Activity
 * Date		: 19 Feb 2014
 * 
 * Description:
 * 
 * User can calculate the loan amount from here for 
 * property price based on tenure and intrust rate
 * 
 * /**
	 * calculate()
	 * 
	 * Calculates the loan amount for property price
	 * based on intrust rate and tenure
	 * with margin values
	 * 
 * ********************************************************************************************************/

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.z.stproperty.dialog.STPAlertDialog;
import com.z.stproperty.fonts.Helvetica;
import com.z.stproperty.shared.SharedFunction;

public class LoanCalculator extends Activity{
	private EditText intrestTxt, mof, tenureTxt;
	private Helvetica resultTxt, priceTxt;
	/**
	 * Will calculate the monthly payment based on the predefined values like interest rate and tenure
	 * and will displays the values into loan calculator display view
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calculator);
		try{
			String price = getIntent().getStringExtra("price");
			priceTxt = (Helvetica) findViewById(R.id.PrpertyPrice);
			priceTxt.setKeyListener(null);
			intrestTxt = (EditText) findViewById(R.id.InterestRate);
			mof = (EditText) findViewById(R.id.MarginOfFinance);
			tenureTxt = (EditText) findViewById(R.id.LoanTenure);
			resultTxt = (Helvetica) findViewById(R.id.MonthlyRepay);
			Button calculator = (Button) findViewById(R.id.Calculate);
			priceTxt.setText(price.replaceAll("[\\,\\$\\sa-zA-Z]", ""));
			intrestTxt.setText("1.5");
			mof.setText("80");
			tenureTxt.setText("30");
			calculateValue();
			calculator.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					validate();
				}
			});
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * calculate()
	 * 
	 * Will check all the fields and calculate the result
	 * 
	 */
	private void validate(){
		if (priceTxt.getText().toString().trim().equals("")
				|| tenureTxt.getText().toString().trim().equals("")
				|| intrestTxt.getText().toString().trim().equals("")
				|| mof.getText().toString().trim().equals("")) {
			// no calculator function
			Intent intent = new Intent(getApplicationContext(), STPAlertDialog.class);
			intent.putExtra("message", "All are mandatory fields.");
			startActivity(intent);
		}else if(Double.parseDouble(intrestTxt.getText().toString().trim())>100 || 
			Double.parseDouble(mof.getText().toString().trim())>100){
			Intent intent = new Intent(getApplicationContext(), STPAlertDialog.class);
			intent.putExtra("message", "Check your percentage values.");
			startActivity(intent);
		}else {
			calculateValue();
		}
	}
	/**
	 * Calculates the loan amount for property price
	 * based on intrust rate and tenure
	 * with margin values
	 */
	private void calculateValue(){
		double l = Double.parseDouble(priceTxt.getText().toString());
		double c = Double.parseDouble(intrestTxt.getText().toString());
		double m = Double.parseDouble(mof.getText().toString());
		double n = Double.parseDouble(tenureTxt.getText().toString());
		c = c / 1200;
		n = n * 12;
		double dp = 1 - (1 - m / 100);
		double l1 = l * dp;
		double p = (l1 * (c * Math.pow(1 + c, n))) / (Math.pow(1 + c, n) - 1);
		p = (l1 * (c * Math.pow(1 + c, n))) / (Math.pow(1 + c, n) - 1);
		SharedFunction.postAnalytics(LoanCalculator.this, "Loan Calculator", "Calculate", new DecimalFormat("##.##").format(p) +"");
		resultTxt.setText(new DecimalFormat("##.##").format(p));
	}
}
