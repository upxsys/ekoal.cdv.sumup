package org.apache.cordova.sumup;


import android.content.Intent;
import android.os.Bundle;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumup.merchant.api.SumUpAPI;
import com.sumup.merchant.api.SumUpPayment;

import java.util.UUID;

public class sumup extends CordovaPlugin {

    private CallbackContext callback = null;
    //
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (action.equals("pay")) {
            int affKey = this.cordova.getActivity().getResources().getIdentifier("SUMUP_API_KEY", "string", this.cordova.getActivity().getPackageName());
            SumUpPayment payment = SumUpPayment.builder()
                    // Your affiliate key is bound to the applicationID entered in the SumUp dashboard at https://me.sumup.com/integration-tools
                    .affiliateKey(this.cordova.getActivity().getResources().getString(affKey))
                    .productAmount(Double.parseDouble(args.get(0).toString()))
                    .currency(SumUpPayment.Currency.valueOf(args.get(1).toString()))
                    .foreignTransactionId(UUID.randomUUID().toString()) // can not exceed 128 chars
                    .skipSuccessScreen()
                    .build();

            this.callback = callbackContext;
            this.cordova.setActivityResultCallback(this);

            SumUpAPI.openPaymentActivity(this.cordova.getActivity(), payment, 1);
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle extras = data.getExtras();

        String code = "";
        String txcode = "";
        String message = "";
        if (extras != null) {
            message = "" + extras.getString(SumUpAPI.Response.MESSAGE);
            txcode = "" + extras.getString(SumUpAPI.Response.TX_CODE);
            code = "" + extras.getInt(SumUpAPI.Response.RESULT_CODE);
        }

        JSONObject res = new JSONObject();
        try {
            res.put("code", code);
            res.put("message", message);
            res.put("txcode", txcode);
        } catch (Exception e) {}

        PluginResult result = new PluginResult(PluginResult.Status.OK, res);
        result.setKeepCallback(true);
        this.callback.sendPluginResult(result);
    }
}

