Gooru Conversion App
==============
Gooru Conversion App project consists of APIs that required for covert PDF to Image ,HTML to PDF and resize Images.


## Dependencies 
<table>
  <tr>
    <th style="text-align:left;">JDK</th>
    <td>1.7 or above</td>
  </tr>
  <tr>
    <th style="text-align:left;">Operating System</th>
    <td>Ubuntu</td>
  </tr>
   <tr>
    <th style="text-align:left;">Application container</th>
    <td>Apache tomcat7</td>
  </tr>
   <tr>
    <th style="text-align:left;">Apache Maven</th>
    <td>Maven 3.0.4</td>
  </tr>
</table>

## Build
* Update your tomcat location in "webapp.container.home" property in root pom.xml
For example, `<webapp.container.home>${env.CATALINA_HOME}</webapp.container.home>`
* Navigate to the development project folder.
For example, cd Home\Projects\Gooru-Conversion-App 
* From the linux terminal Clean install the build.
Command: `mvn clean install -P conversion -Dmaven.test.skip=true`
* Project deployed on `${env.CATALINA_HOME}/webapps/` location


## Learn more 
Learn more about our open source software on our developer’s page where you can find links to documentation and resources at: http://developers.goorulearning.org/


## License
Gooru Conversion App is released under the [MIT License](http://opensource.org/licenses/MIT) .
