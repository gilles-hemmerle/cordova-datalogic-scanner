function Plugin(){}
Plugin.listen = function(onSuccess, onFail) {
    cordova.exec(onSuccess, onFail, 'ScannerPlugin', 'listen', []);
}
module.exports = Plugin;