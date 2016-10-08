package it.zenitlab.cordova.plugins.zbtprinter;

import java.io.IOException;

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

import com.zebra.android.discovery.BluetoothDiscoverer;
import com.zebra.android.discovery.DiscoveryHandler;


public class ZebraBluetoothPrinter extends CordovaPlugin {

    private static final String LOG_TAG = "ZebraBluetoothPrinter";
    String mac =  "AC:3F:A4:1D:BE:90"; //  "AC:3F:A4:53:51:50";

    public ZebraBluetoothPrinter() {
    }

 

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    
        

        if (action.equals("print")) {
            try {
            	//mac = args.getString(0);
                String msg = args.getString(0);
                if(msg.startsWith("PRINTMAC")) {
            	
                	int index = msg.indexOf("MACPRINT");	
                	mac = msg.substring(8,index);
                	msg = msg.substring((index+8), msg.length());
                }
                //sendData(callbackContext, msg);
                getMacAddressOfDiscoveredPrinterAndPrint(callbackContent, msg);
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    private String getMacAddressOfDiscoveredPrinterAndPrint(CallbackContext callbackContent, String msg){

        BluetoothDiscoverer.findPrinters(getActivity(), new DiscoveryHandler(){
            @Override
            public void foundPrinter(DiscoveredPrinter printer) {
                mac = printer.address;
                sendData(callbackContent, msg);
            }
        });
    }

    /*
     * This will send data to be printed by the bluetooth printer
     */
    void sendData(final CallbackContext callbackContext, final String msg) throws IOException {
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

