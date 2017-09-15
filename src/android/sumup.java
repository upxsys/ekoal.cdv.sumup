package org.apache.cordova.sumup;


import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import com.sumup.merchant.api.SumUpAPI;
import com.sumup.merchant.api.SumUpPayment;
import com.sumup.merchant.api.SumUpState;

import java.util.UUID;

public class sumup extends CordovaPlugin {
    //
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {


        this.cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                SumUpState.init(cordova.getActivity());
            }
        });

        if (action.equals("pay")) {
            //CURRENCY CONVERSION
            int affKey = this.cordova.getActivity().getResources().getIdentifier("SUMUP_API_KEY", "string", this.cordova.getActivity().getPackageName());
            SumUpPayment payment = SumUpPayment.builder()
                    // mandatory parameters
                    // Your affiliate key is bound to the applicationID entered in the SumUp dashboard at https://me.sumup.com/integration-tools

                    .affiliateKey(this.cordova.getActivity().getResources().getString(affKey))
                    .productAmount(Double.parseDouble(args.get(0).toString()))
                    .currency(SumUpPayment.Currency.valueOf(args.get(1).toString()))
                    .foreignTransactionId(UUID.randomUUID().toString()) // can not exceed 128 chars
                    .build();


            SumUpAPI.openPaymentActivity(this.cordova.getActivity(), payment, 1);
            return true;
        }
        return false;
    }


    private void echo(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

}

