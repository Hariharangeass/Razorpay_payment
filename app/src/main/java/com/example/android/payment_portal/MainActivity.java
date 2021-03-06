package com.example.android.payment_portal;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.payment_portal.ApiInterface.retrofit;

public class MainActivity extends AppCompatActivity implements PaymentResultListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    public int amount = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Checkout.preload(getApplicationContext());

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPayment();
            }
        });


    }


    public void startPayment(){
        final Activity activity = this;
        final Checkout checkout = new Checkout();
        try{
            JSONObject options = new JSONObject();
            options.put("name","merchant name");
            options.put("currency","INR");
            options.put("amount","100");
            checkout.open(activity,options);

        }catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {

        try {
            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
        ApiInterface service = retrofit.create(ApiInterface.class);
        PaymentID paymentID = new PaymentID();
        paymentID.setRazorpayPaymentID(razorpayPaymentID);
        paymentID.setAmount(amount);
        Call<PaymentID> call = service.insertData(paymentID.getRazorpayPaymentID(), paymentID.getAmount());
        call.enqueue(new Callback<PaymentID>() {
            @Override
            public void onResponse(Call<PaymentID> call, Response<PaymentID> response) {

            }

            @Override
            public void onFailure(Call<PaymentID> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Throwable"+t, Toast.LENGTH_LONG).show();

            }
        });


    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }

    }


}


