package io.ionic.starter.MyPlugin;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@NativePlugin(requestCodes = {MyPlugin.REQUEST_CONTACTS})
public class MyPlugin extends Plugin{
    protected static final int REQUEST_CONTACTS = 12345;

    @PluginMethod
    public void echo(PluginCall call){
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", value);
        call.success(ret);
    }

    @PluginMethod()
    public void getContacts(PluginCall call) {
        String value = call.getString("filter");
        // Filter based on the value if want

        saveCall(call);
        pluginRequestPermission(Manifest.permission.READ_CONTACTS, REQUEST_CONTACTS);
    }

    @Override
    protected void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.handleRequestPermissionsResult(requestCode, permissions, grantResults);


        PluginCall savedCall = getSavedCall();
        if (savedCall == null) {
            Log.d("Test", "No stored plugin call for permissions request result");
            return;
        }

        for(int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                Log.d("Test", "User denied permission");
                return;
            }
        }

        if (requestCode == REQUEST_CONTACTS) {
            // We got the permission!
            loadContacts(savedCall);
        }
    }

    void loadContacts(PluginCall call) {
        ArrayList<Map> contactList = new ArrayList<>();
        ContentResolver cr = this.getContext().getContentResolver();

        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                Map<String,String> map =  new HashMap<String, String>();

                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                map.put("firstName", name);
                map.put("lastName", "");

                String contactNumber = "";

                if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    pCur.moveToFirst();
                    contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Log.i("phoneNUmber", "The phone number is "+ contactNumber);
                }
                map.put("telephone", contactNumber);
                contactList.add(map);
            }
        }
        if (cur != null) {
            cur.close();
        }

        JSONArray jsonArray = new JSONArray(contactList);
        JSObject ret = new JSObject();
        ret.put("results", jsonArray);
        call.success(ret);
    }
}
