package it.zenitlab.cordova.plugins.zbtprinter;

import java.io.IOException;
import android.content.Context;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import android.util.Log;
import com.zebra.sdk.comm.BluetoothConnectionInsecure;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import com.zebra.sdk.printer.discovery.BluetoothDiscoverer;
import com.zebra.sdk.printer.discovery.DiscoveredPrinter;
import com.zebra.sdk.printer.discovery.DiscoveredPrinterBluetooth;
import com.zebra.sdk.printer.discovery.DiscoveryHandler;


public class ZebraBluetoothPrinter extends CordovaPlugin {

    private static final String LOG_TAG = "ZebraBluetoothPrinter";
    //String mac =  "AC:3F:A4:1D:BE:90";
    //"AC:3F:A4:1D:BE:90";
    //  "AC:3F:A4:53:51:50";

    public ZebraBluetoothPrinter() {
    }

        @Override
        public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

            if (action.equals("print")) {
                try {
                    //mac = args.getString(0);
                    String msg = args.getString(0);
                    String mac = args.getString(1);

                    sendData(callbackContext, msg, mac);
                    //getMacAddressOfDiscoveredPrinterAndPrint(callbackContext, msg);
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
                return true;
            }

            if (action.equals("getprinter")){
                getPrinter(callbackContext);
                return true;
            }

            return false;
    }

    private void getPrinter(final CallbackContext callbackContext){

        Context ctx = this.cordova.getActivity().getApplicationContext();

        try{
            BluetoothDiscoverer.findPrinters(ctx, new DiscoveryHandler() {
                @Override
                public void foundPrinter(DiscoveredPrinter discoveredPrinter) {

                    DiscoveredPrinterBluetooth bt = (DiscoveredPrinterBluetooth)discoveredPrinter;

                    String friendlyName = bt.friendlyName;

                    if(friendlyName.startsWith("XXXX")){

                        String mac = bt.address;

                        callbackContext.success(mac);

                    }
                }

                @Override
                public void discoveryFinished() {
                    Log.d("Discovery finished", "done");
                }

                @Override
                public void discoveryError(String s) {
                    callbackContext.error("Discovery error: " + s);
                }
            });
        } catch(Exception ex){
            callbackContext.error("Error: " + ex.getMessage());
        }

    }

    /*
     * This will send data to be printed by the bluetooth printer
     */
    void sendData(final CallbackContext callbackContext, final String msg, final String mac) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Instantiate insecure connection for given Bluetooth MAC Address.
                    Connection thePrinterConn = new BluetoothConnectionInsecure(mac);

                    // Verify the printer is ready to print
                    if (isPrinterReady(thePrinterConn)) {

                        // Open the connection - physical connection is established here.
                        thePrinterConn.open();

                        // Send the data to printer as a byte array.
//                        thePrinterConn.write("^XA^FO0,20^FD^FS^XZ".getBytes());
                        thePrinterConn.write(msg.getBytes());


                        // Make sure the data got to the printer before closing the connection
                        Thread.sleep(500);

                        // Close the insecure connection to release resources.
                        thePrinterConn.close();
                        callbackContext.success("Stampa terminata");
                    } else {
                        callbackContext.error("printer is not ready");
                    }
                } catch (Exception e) {
                    // Handle communications error here.
                    callbackContext.error(e.getMessage());
                }
            }
        }).start();
    }

    private Boolean isPrinterReady(Connection connection) throws ConnectionException, ZebraPrinterLanguageUnknownException {
        Boolean isOK = false;
        connection.open();
        // Creates a ZebraPrinter object to use Zebra specific functionality like getCurrentStatus()
        ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
        PrinterStatus printerStatus = printer.getCurrentStatus();
        if (printerStatus.isReadyToPrint) {
            isOK = true;
        } else if (printerStatus.isPaused) {
            throw new ConnectionException("Cannot Print because the printer is paused.");
        } else if (printerStatus.isHeadOpen) {
            throw new ConnectionException("Cannot Print because the printer media door is open.");
        } else if (printerStatus.isPaperOut) {
            throw new ConnectionException("Cannot Print because the paper is out.");
        } else {
            throw new ConnectionException("Cannot Print.");
        }
        return isOK;
    }
}

