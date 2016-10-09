var exec = require('cordova/exec');

exports.print = function(str, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'ZebraBluetoothPrinter', 'print', [str]);
};

exports.getprinter = function(successCallback, errorCallback){
    cordova.exec(successCallback, errorCallback, 'ZebraBluetoothPrinter', 'getprinter', []);
};

exports.getprinter = function(successCallback, errorCallback){
    cordova.exec(successCallback, errorCallback, 'ZebraBluetoothPrinter', 'getprinter', []);
};
