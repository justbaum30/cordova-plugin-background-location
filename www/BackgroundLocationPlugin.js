GeoLocationService.prototype = {

	createCallbackFn: function (callbackFn, scope) {
		if (typeof callbackFn !== 'function')
			return;
			
		return function () {
			callbackFn.apply(scope || this, arguments);
		};
	},
	
	test: function (callback, scope) {
		console.log('test');
		var callbackFn = this.createCallbackFn(callback, scope);
		cordova.exec(callbackFn, null, 'GeoLocationService', 'test', []);
	}
};

var plugin = new GeoLocationService();
var channel = require('cordova/channel');

channel.deviceready.subscribe(function () {
	console.log('channel.deviceready');
	cordova.exec(null, null, 'GeoLocationService', 'deviceready', []);
});

module.exports = plugin;