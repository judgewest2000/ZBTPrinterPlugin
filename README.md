# zbtprinter
A Cordova/Phonegap driver for Zebra bluetooth printers

##Usage
Detect the printer
```
 cordova.plugins.zbtprinter.getprinter(
        function (mac) {
            console.log('found printer with mac address of ' + mac);
        },
        function (err) {
            alert('could not find printer: ' + err);
        });
```

You can send data in ZPL Zebra Programing Language:
```
cordova.plugins.zbtprinter.print(
    "^XA^FO10,10^AFN,26,13^FDHello, World!^FS^XZ", 
    "AC:3F:A4:1D:BE:90",
    function(success) { 
        alert("Print ok"); 
    }, function(fail) { 
        alert(fail); 
    }
);
```

###Important

##Print times
After detection of the printer for whatever reason it will not print your document for around a minute if you call it immediately afterwards (tested on an iMZ 320).
My suggestion is to detect the printer on first boot and store the mac address for later use.

##Breaking change
Compared to the original version of this plugin there is a breaking change as the cordova.plugins.zbtprinter.print command - you must now pass the MAC address of the printer you wish to print to.  This can be detected initially using the cordova.plugins.zbtprinter.getprinter command. 

###Cordova
For the cordova.plugins.zbtprinter.getprinter command to detect the printer, the printer's name must start with 'XXXX' which zebra printers have as their default.  If you change this it will not work.

```
cordova plugin add https://github.com/judgewest2000/ZBTPrinterPlugin.git
```

##CPCL - Comtec Printer Control Language
For more information about CPCL please see the [PDF Official Manual](https://www.zebra.com/content/dam/zebra/manuals/en-us/printer/cpcl-pm-en.pdf)

##ZPL - Zebra Programming Language
For more information about ZPL please see the  [PDF Official Manual](https://support.zebra.com/cpws/docs/zpl/zpl_manual.pdf)
