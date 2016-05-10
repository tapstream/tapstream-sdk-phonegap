package com.tapstream.phonegap;

import android.util.Log;
import com.tapstream.sdk.*;
import java.util.*;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.*;

public class TapstreamPlugin extends CordovaPlugin {

    private static final String TAG = "TapstreamPlugin";

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if("create".equals(action)) {
            final String accountName = args.getString(0);
            final String developerSecret = args.getString(1);
            final JSONObject config = args.optJSONObject(2);
            create(accountName, developerSecret, config);
            return true;
        } else if("fireEvent".equals(action)) {
            final String eventName = args.getString(0);
            final boolean oneTimeOnly = args.optBoolean(1);
            final JSONObject params = args.optJSONObject(2);
            fireEvent(eventName, oneTimeOnly, params);
            return true;
        } else if("lookupTimeline".equals(action)){
            lookupTimeline(callbackContext);
            return true;
        }
        return false;
    }

    private void create(String accountName, String developerSecret, JSONObject configVals) throws JSONException {
        final Config config = new Config(accountName, developerSecret);
        config.setActivityListenerBindsLate(true);

        if (configVals != null){

            Iterator<String> configKeyIter = configVals.keys();

            while (configKeyIter.hasNext()){
                final String key = configKeyIter.next();
                final Object value = configVals.get(key);

                if (value == null)
                    continue;

                if ("custom_parameters".equals(key) && value instanceof JSONObject){
                    final JSONObject params = (JSONObject)value;
                    Iterator<String> paramKeyIter = params.keys();

                    while (paramKeyIter.hasNext()){
                        String paramKey = paramKeyIter.next();
                        Object paramValue = params.get(paramKey);
                        if (paramValue != null)
                            config.setGlobalEventParameter(paramKey, paramValue);
                    }
                } else if ("odin1".equals(key))
                    config.setOdin1(value.toString());
                else if ("openUdid".equals(key))
                    config.setOpenUdid(value.toString());
                else if ("deviceId".equals(key))
                    config.setDeviceId(value.toString());
                else if ("wifiMac".equals(key))
                    config.setWifiMac(value.toString());
                else if ("androidId".equals(key))
                    config.setAndroidId(value.toString());
                else if ("installEventName".equals(key))
                    config.setInstallEventName(value.toString());
                else if ("openEventName".equals(key))
                    config.setOpenEventName(value.toString());
                else if ("fireAutomaticInstallEvent".equals(key))
                    config.setFireAutomaticInstallEvent(Boolean.parseBoolean(value.toString()));
                else if ("fireAutomaticOpenEvent".equals(key))
                    config.setFireAutomaticOpenEvent(Boolean.parseBoolean(value.toString()));
                else if ("collectAdvertisingId".equals(key))
                    config.setCollectAdvertisingId(Boolean.parseBoolean(value.toString()));
                else
                    Log.w(TAG, "Skipping unknown config key: " + key);
            }
        }


        Tapstream.create(cordova.getActivity().getApplication(), config);
    }

    private void fireEvent(String eventName, boolean oneTimeOnly, JSONObject params) throws JSONException {
        Event e = new Event(eventName, oneTimeOnly);
        if(params != null) {
            Iterator<String> paramKeyIter = params.keys();
            while (paramKeyIter.hasNext()){
                String key = paramKeyIter.next();
                Object value = params.get(key);
                if (value != null)
                    e.setCustomParameter(key, value.toString());
            }
        }
        Tapstream.getInstance().fireEvent(e);
    }

    private void lookupTimeline(final CallbackContext callbackContext){
        Tapstream.getInstance()
                .lookupTimeline()
                .setCallback(new Callback<TimelineApiResponse>(){
            @Override
            public void success(TimelineApiResponse resp){
                try{
                    callbackContext.success(resp.parse());
                } catch (Exception e){
                    String msg = "Failed to parse timeline response";
                    Log.e(TAG, msg, e);
                    callbackContext.error(msg);
                }
            }

            @Override
            public void error(Throwable reason){
                String msg = "Error during timeline lookup";
                Log.e(TAG, msg, reason);
                callbackContext.error(msg);
            }
        });
    }
}
