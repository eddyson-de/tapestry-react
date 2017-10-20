import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.remote.DesiredCapabilities

import io.github.bonigarcia.wdm.ChromeDriverManager

reportsDir = 'build/reports/geb'
baseUrl = "http://localhost:${System.properties['jettyPort']}/"

environments {
  'chrome-headless' {
    driver = {
      ChromeDriverManager.getInstance().setup()
      ChromeOptions options = new ChromeOptions()
      options.addArguments('headless')
      options.addArguments('disable-gpu') // https://developers.google.com/web/updates/2017/04/headless-chrome
      DesiredCapabilities capabilities = DesiredCapabilities.chrome();
      capabilities.setCapability(ChromeOptions.CAPABILITY, options);
      new ChromeDriver(capabilities)
    }
  }
  'firefox' {
    driver = {
      DesiredCapabilities.firefox().with {
        setCapability("marionette", false);
        new FirefoxDriver(it)
      }
    }
  }
}