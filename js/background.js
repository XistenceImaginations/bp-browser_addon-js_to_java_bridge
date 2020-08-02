console.debug('Bridge for JS-to-JAVA: background.js');

/**
 * An asynchrone function to send information to an application
 * (here a Java-application) that works as a server.
 * 
 */
const postAsync = async (route, data, success, fail, mtd = 'POST') => {
  // Note: the server must define a route for '/test', otherwise this
  //  will create an error as the server won't reply for the OPTIONS-preflight
  //  for the CORS-check.

  await fetch(`http://localhost:11211/${route || ''}`, {
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
