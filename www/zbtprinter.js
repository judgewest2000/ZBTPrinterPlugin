var exec = require('cordova/exec');

exports.print = function(str, mac, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'ZebraBluetoothPrinter', 'print', [str, mac]);
};

exports.getprinter = function(successCallback, errorCallback){
    cordova.exec(successCallback, errorCallback, 'ZebraBluetoothPrinter', 'getprinter', []);
};

