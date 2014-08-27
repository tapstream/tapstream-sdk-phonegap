
module.exports = {
    create: function(accountName, developerSecret, config) {
        cordova.exec(null, null, 'Tapstream', 'create', [accountName, developerSecret, config]);
    },
    fireEvent: function(eventName, oneTimeOnly, params) {
        cordova.exec(null, null, 'Tapstream', 'fireEvent', [eventName, oneTimeOnly, params]);
    },
    getConversionData: function(callback) {
        cordova.exec(callback, null, 'Tapstream', 'getConversionData', []);
    }
};
