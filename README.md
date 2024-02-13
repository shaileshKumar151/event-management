# Sample Event Registration project

This is an AEM project which allows users to register for events.

Event Registration page: /content/event-management/us/en/event-registration.html\
Thank you page: /content/event-management/us/en/event-registration/thank-you.html

## Java Classes written
* EventRegistrationServlet.java
  * This is the main servlet which handles the post response from the Event Registration form.
  * There is server side validations on the required fields before passing on the data to an API.
* EventRegistrationService.java
  * The Service class which is responsible for making the http request to the registration API and handle the response.
* EventRegistrationRequest.java
  * The POJO class to create the request object before the API call.
* EventRegistrationResponse.java
  * The POJO class to map the API response to Java object.

### Unit Tests
* EventRegistrationServletTest.java


## Modules


## How to build

To build all the modules run in the project root directory the following command with Maven 3:

    mvn clean install

To build all the modules and deploy the `all` package to a local instance of AEM, run in the project root directory the following command:

    mvn clean install -PautoInstallSinglePackage

Or to deploy it to a publish instance, run

    mvn clean install -PautoInstallSinglePackagePublish

Or alternatively

    mvn clean install -PautoInstallSinglePackage -Daem.port=4503

Or to deploy only the bundle to the author, run

    mvn clean install -PautoInstallBundle

Or to deploy only a single content package, run in the sub-module directory (i.e `ui.apps`)

    mvn clean install -PautoInstallPackage


### Unit tests

This show-cases classic unit testing of the code contained in the bundle. To
test, execute:

    mvn clean test

## Maven settings

The project comes with the auto-public repository configured. To setup the repository in your Maven settings, refer to:

    http://helpx.adobe.com/experience-manager/kb/SetUpTheAdobeMavenRepository.html
