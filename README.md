Current documentation for Tapstream's PhoneGap plugin is available on [Tapstream's developer portal](https://tapstream.com/developer/phonegap-sdk-documentation/).

tapstream-sdk-phonegap
================

Tapstream is a marketing SDK that lets you instantly work with hundreds of ad networks, measeure ARPU and retention from any traffic source, and modify your app's user experience depending on what your users were doing before they installed it.

## Upstream SDK versions

* Android: 3.0.4
* iOS: 2.8.5

## Integrating the Tapstream PhoneGap SDK

### Using Tapstream without PhoneGap Build

From your main project directory, run:

```bash
phonegap plugin add tapstream-sdk-phonegap
```

This will download the Tapstream plugin and add it to your project. (Note that only iOS and Android PhoneGap projects are supported.)

## Initialize the Tapstream SDK

Initialize Tapstream from your `onDeviceReady:` function like this:

```javascript
tapstream.create('TAPSTREAM_ACCOUNT_NAME', 'TAPSTREAM_SDK_SECRET', {
    // (Optional) Config overrides go here
});
```

**For iOS projects, we strongly recommend that you collect and provide the iOS Advertising Identifier (IDFA) value to the Tapstream SDK.** Please see [Apple's documentation on collecting the IDFA](http://developer.apple.com/library/ios/#documentation/AdSupport/Reference/ASIdentifierManager_Ref/ASIdentifierManager.html).

**For Android projects, it is strongly recommended that you include the Google Play Services SDK.**

### Firing extra events

By default, Tapstream fires an event whenever a user runs the app. You can define further events for recording key actions in your app by using the syntax below:

```javascript
// Regular event:
tapstream.fireEvent('test-event', false);

// Regular event with custom params:
tapstream.fireEvent('test-event', false, {
    'my-custom-param': 3,
});

// One-time-only event:
tapstream.fireEvent('install', true);

// One-time-only event with custom params:
tapstream.fireEvent('install', true, {
    'my-custom-param': 'hello world',
});
```

## Global custom event parameters

You may find that you have some data that needs to be attached to every single event sent by the Tapstream SDK,
including your Install and Open events.. Instead of writing the code to attach this data in each place that you send an event,
add these data values to the global event parameters member of the config object.  The parameters in this dictionary
will automatically be attached to every event, including the automatic events fired by the SDK.

```javascript
tapstream.create('TAPSTREAM_ACCOUNT_NAME', 'TAPSTREAM_SDK_SECRET', {
    custom_parameters: {
      some_key: "value"
   }
});
```

## Changing the default behavior of the Tapstream SDK

**Note**: Changing this behavior is not usually required.

To change the default Tapstream config, provide config overrides like this:

```javascript
tapstream.create('TAPSTREAM_ACCOUNT_NAME', 'TAPSTREAM_SDK_SECRET', {
    idfa: '<IDFA goes here>',
    collectDeviceId: true,
    collectWifiMac: false,
    secureUdid: '<SecureUDID goes here>',
    installEventName: 'custom-install-event-name',
    openEventName: 'custom-open-event-name',
});
```

Consult the platform-specific SDK documentation to see what config variables are available.  Don't use accessor methods; instead, just set the variables directly, using camel-case capitalization

## First-run app experience modification

The Tapstream SDK gives you a mechanism for receiving a callback that will contain
everything Tapstream knows about the user's timeline, including any links the user
clicked on or websites of yours they visited before running the app.

If you want to customize the behavior of your application based on the user's source
campaign, click parameters, or other pre-install behavior, you'll need to asynchronously
request this info from the SDK.

Here's how it works:

```javascript
tapstream.lookupTimeline(function(jsonData){
    if(jsonData){
        // Read some data from this json object, and modify your application's behavior accordingly
    }
});
```

An example JSON response might look like this:

```javascript
{
  "hits": [
    // This is an array of all hits (e.g. campaign clicks and website visits) associated with this user.
    {
      "custom_parameters": { // All custom query string parameters
        "__deeplink": "trending://stocks/", // If available, the deeplink destination you defined for this platform
        "campaign-name": "TESTING"
      },
      "created": 1383695050683,
      "ip": "199.21.86.54",
      "app": "tapfoliotrending",
      "session_id": "2b3d397e-4674-11e3-a2a0-525400f1a7ad",
      "tracker": "example-campaign", // The slug of the campaign link
      "dest_url_platform": "ANY",
      "user_agent": "Mozilla/5.0 (iPhone; CPU iPhone OS 7_0_3 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11B511 Safari/9537.53",
      "referrer": "",
      "device_specific_hardware_ids": {
        "ios_idfa": "141E8456-3D20-46D1-92F8-8EE0728BE7BF"
      },
      "id": "2b3d608f-4674-11e3-a2a0-525400f1a7ad",
      "dest_url": "http://itunes.apple.com/us/app/stocks-trending-stocks-at-a-glance/id512068016?ls=1&mt=8"
    }
  ],
  "events": [
    // This is an array of all in-app events associated with this user.
    {
      "profile_model": "iPhone5,1",
      "profile_gmtoffset": 4294938496,
      "profile_resolution": "640x1136",
      "created": 1383695183000,
      "ip": "199.21.86.54",
      "app": "tapfoliotrending",
      "profile_sdkversion": "2.4",
      "session_id": "df9b7f5d-ca9c-4ef3-a0b9-1e66795eef5e",
      "package_name": "com.paperlabs.TapfolioTrending",
      "tracker": "ios-trending-open", // The slug of the event
      "profile_os": "iPhone OS 7.0.3",
      "profile_vendor": "Apple",
      "device_specific_hardware_ids": {
        "ios_idfa": "142D8456-4E70-46D1-92F8-8EE0728BE7BF",
        "ios_secure_udid": "EAAD91D5-EAEA-40BC-A244-13A3D7748F95"
      },
      "app_name": "Trending",
      "id": "7a1f50ce-4674-11e3-a312-525400d091e2",
      "profile_locale": "en_US",
      "profile_platform": "iOS"
    },
  ]
}
```

If you wish to simulate conversions to test this functionality, please refer to the documentation on
[simulating Tapstream conversions]({% url 'developer_simulating_conversions' %}).

## Deep linking when the user doesn't already have the app

Tapstream's [first-run modification](#first-run-modification) lets you deeplink your users to different parts of
your app, even if they didn't already have it. Parse the JSON response and look for the custom parameter called
`__deeplink`. This parameter's value is the deeplink destination that your user would have been sent to when
clicking your campaign link, had they already had the app.

## Preventing conflicts with Tapstream's JavaScript

If you're using your website inside of PhoneGap, and your website loads Tapstream's JavaScript, you need to modify your Tapstream JavaScript before proceeding.

First, add the following JavaScript snippet in PhoneGap so that it fires before `onload`:

```javascript
window.__ts_suppress = true;
```

Then, modify your site's Tapstream JavaScript from this:

```javascript
<script type="text/javascript">

var _tsq = _tsq || [];
_tsq.push(["setAccountName", "ergerg"]);
_tsq.push(["fireHit", "javascript_tracker", []]);

(function() {
    function z(){
        var s = document.createElement("script");
        s.type = "text/javascript";
...
```

to this:

```javascript
<script type="text/javascript">

var _tsq = _tsq || [];
_tsq.push(["setAccountName", "ergerg"]);
_tsq.push(["fireHit", "javascript_tracker", []]);

(function() {
    function z(){
        // Return if the PhoneGap-only window variable is set
        if(window.__ts_suppress) return;
        var s = document.createElement("script");
        s.type = "text/javascript";
...
```

This will prevent Tapstreams JavaScript from firing hits from within your app.
