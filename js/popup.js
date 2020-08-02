console.debug('Bridge for JS-to-JAVA: popup.js');

/**
 * Example to add a small indication to the icon.
 * Note that this effect -here- will apply only if the icon was clicked once.
 * But for sure you can use it in combination with listeners, states and more. 
 */
chrome.browserAction.setBadgeText({text: 'foo'});
chrome.browserAction.setBadgeBackgroundColor({color: '#777'});

