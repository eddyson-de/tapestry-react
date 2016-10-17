import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.remote.DesiredCapabilities

reportsDir = 'build/reports/geb'
baseUrl = "http://localhost:${System.properties['jettyPort']}/"
driver = {
  DesiredCapabilities.firefox().with {
    setCapability("marionette", false);
    new FirefoxDriver(it)
  }
}