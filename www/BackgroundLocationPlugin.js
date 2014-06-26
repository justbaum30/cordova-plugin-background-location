var BackgroundLocationPlugin = function() {

};

BackgroundLocationPlugin.prototype = {

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
		cordova.exec(callbackFn, null, 'BackgroundLocationPlugin', 'test', []);
	}
};

var plugin = new BackgroundLocationPlugin();
var channel = require('cordova/channel');

channel.deviceready.subscribe(function () {
	console.log('channel.deviceready');
	cordova.exec(null, null, 'BackgroundLocationPlugin', 'deviceready', []);
});

module.exports = plugin;