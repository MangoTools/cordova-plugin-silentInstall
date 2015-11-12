package tools.mango.cordova.plugin.android;

import java.util.List;
import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.lang.Process;

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
import android.util.Log;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.content.Intent;

/**
 * This class listens to the proximity sensor and stores the latest value.
 */
public class SilentInstall extends CordovaPlugin {

    private static final String TAG = "SilentInstall";
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
        } else if(action.equals("copyApk")) {
             String filePath = args.getJSONObject(0).getString("filePath");
             this.copyApk(filePath);
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

    public void doRestart(Context c) {
        try {
            //check if the context is given
            if (c != null) {
                //fetch the packagemanager so we can get the default launch activity
                // (you can replace this intent with any other activity if you want
                PackageManager pm = c.getPackageManager();
                //check if we got the PackageManager
                if (pm != null) {
                    //create the intent with the default start activity for your application
                    Intent mStartActivity = pm.getLaunchIntentForPackage(
                            c.getPackageName()
                    );
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //create a pending intent so the application is restarted after System.exit(0) was called.
                        // We use an AlarmManager to call this intent in 100ms
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent
                                .getActivity(c, mPendingIntentId, mStartActivity,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        System.out.println("ABo - Set restart: " + c.getPackageName().toString());
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 15000, mPendingIntent);
                    } else {
                        Log.e(TAG, "Was not able to restart application, mStartActivity null");
                    }
                } else {
                    Log.e(TAG, "Was not able to restart application, PM null");
                }
            } else {
                Log.e(TAG, "Was not able to restart application, Context null");
            }
        } catch (Exception ex) {
            Log.e(TAG, "Was not able to restart application");
        }
    }

    public void installApk(String uri) {

        System.out.println("ABo - uri="+uri);
        try{
            URL url = new URL( uri );
            System.out.println("ABo - url=" + url);
                File file = new File( url.getFile() );
                if(file.exists()){
                    try {
                        // Prepare for restart
                        System.out.println("ABo - Prepare restart");
                        Context context=this.cordova.getActivity().getApplicationContext();
                        doRestart(context);
                        // Do update
                        System.out.println("ABo - File exist: "+file);
                        final String command = "pm install -r " + file.getAbsolutePath();
                        //final String command = "adb install -r " + file.getAbsolutePath();
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
public void setRW(){
    Process process;
    try {
        process = Runtime.getRuntime().exec("su");
        DataOutputStream out = new DataOutputStream(process.getOutputStream());
        out.writeBytes("mount -o remount,rw -t yaffs2 /dev/block/mtdblock3 /system\n");
        out.writeBytes("exit\n");
        out.flush();
        process.waitFor();

    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
}
public void setR0(){
    Process process;
    try {
        process = Runtime.getRuntime().exec("su");
        DataOutputStream out = new DataOutputStream(process.getOutputStream());
        out.writeBytes("mount -o remount,ro -t yaffs2 /dev/block/mtdblock3 /system\n");
        out.writeBytes("exit\n");
        out.flush();
        process.waitFor();

    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
}
public void copyApk(String uri) {

        System.out.println("ABo - uri="+uri);
        try{
            URL url = new URL( uri );
            System.out.println("ABo - url=" + url);
                try {
                    setRW();
                    FileInputStream inStream = new FileInputStream(new File(url.getFile()));
                    FileOutputStream outStream = new FileOutputStream(new File("/system/app/MangoSwitch.apk"));
                    FileChannel inChannel = inStream.getChannel();
                    FileChannel outChannel = outStream.getChannel();
                    inChannel.transferTo(0, inChannel.size(), outChannel);
                    inStream.close();
                    outStream.close();
                    setRO();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        catch(IOException e){
            System.out.println(e);
        }

        //return true;
    }


}

