console.debug('Bridge for JS-to-JAVA: background.js');

const serverBaseUrl = 'http://localhost'; // The base url. Note: don't add a trailing '/' at the end
const serverBasePost = 11211; // Note: this should be equal to the port set in the app server

/**
 * An asynchronuous function to send information to an application
 * (here a Java-application) that works as a server.
 */
const postAsync = async (route, data, success, fail, mtd = 'POST') => {

  // Note: the server must define a route for '/test', otherwise this
  //  will create an error as the server won't reply for the OPTIONS-preflight
  //  for the CORS-check.
  await fetch(`${serverBaseUrl}:${serverBasePost}/${route || ''}`, {
    method: mtd,
    body: JSON.stringify(data),
    headers: { 'Content-Type': 'application/json' }
  })
    .then(response => {
      if (!response.ok) {
        throw Error(response.statusText);
      }
      return response.json();
    })
    .then(json => success ? success(json) : console.debug(json))
    .catch(error => fail ? fail(error) : console.error(error));
}

postAsync('test', { 'foo': 'bar' }, console.debug, console.error);
