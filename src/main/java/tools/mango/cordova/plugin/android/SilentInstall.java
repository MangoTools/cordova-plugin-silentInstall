package tools.mango.cordova.plugin.android;

import java.util.List;
import java.io.*;
import java.net.URL;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;

import android.os.Handler;
import android.os.Looper;

/**
 * This class listens to the proximity sensor and stores the latest value.
 */
public class SilentInstall extends CordovaPlugin {

    private CallbackContext callbackContext;
    private CordovaWebView webView;
    /**
     * Constructor.
     */
    public SilentInstall() {

    }

    /**
     * Sets the context of the Command. This can then be used to do things like
     * get file paths associated with the Activity.
     *
     * @param cordova The context of the main Activity.
     * @param webView The CordovaWebView Cordova is running in.
     */
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        this.webView = webView;
    }

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action                The action to execute.
     * @param args          	    JSONArry of arguments for the plugin.
     * @param callbackS=Context     The callback id used when calling back into JavaScript.
     * @return              	    True if the action was valid.
     * @throws JSONException
     */
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("installApk")) {
            String filePath = args.getJSONObject(0).getString("filePath");
            this.installApk(filePath);
            /*if(this.installApk()){
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "done"));
            }
            else{
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.EXCEPTION, "error on apk install"));
            };*/
        } else {
            // Unsupported action
            return false;
        }
        return true;
    }

    /**
     * Called when listener is to be shut down and object is being destroyed.
     */
    public void onDestroy() {

    }

    /**
     * Called when app has navigated and JS listeners have been destroyed.
     */
    public void onReset() {

    }

    //--------------------------------------------------------------------------
    // LOCAL METHODS
    //--------------------------------------------------------------------------

    public void installApk(String uri) {

        System.out.println("ABo - uri="+uri);
        try{
            URL url = new URL( uri );
            System.out.println("ABo - url="+url);
                File file = new File( url.getFile() );
                if(file.exists()){
                    try {
                        System.out.println("ABo - File exist: "+file);
                        //final String command = "pm install -r " + file.getAbsolutePath();
                        final String command = "adb install -r " + file.getAbsolutePath();
                        System.out.println("ABo - SU run : " + command);
                        Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
                        proc.waitFor();
                    } catch (Exception e) {
                        e.printStackTrace();
                        //return false;
                    }
                }
        }
        catch(IOException e){
            System.out.println(e);
        }

        //return true;
    }



}

