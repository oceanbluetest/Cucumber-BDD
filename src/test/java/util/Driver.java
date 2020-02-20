package util;

import contstants.SeleniumConstants;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.remote.LocalFileDetector;

public class Driver {
    private static WebDriver driver = null;
    public static final String propertyPath = "./src/test/resources/conf/configuration.properties";

    public static final String sauceUsername = ConfigReader.readProperty("sauceUsername");
    public static final String sauceKey = ConfigReader.readProperty("sauceKey");
    public static final String URL = "http://" + sauceUsername + ":" + sauceKey + "@ondemand.saucelabs.com:80/wd/hub";

    public static ThreadLocal<String> sauceSessionId = new ThreadLocal<>();

    public static void initialize(String browser){
        if (driver != null )
            return;
        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            case "ie":
                WebDriverManager.iedriver().setup();
                driver = new InternetExplorerDriver();
                break;
            default:
                System.out.println("Invalid browser type");
        }
        driver.manage().timeouts().implicitlyWait(SeleniumConstants.IMPLICIT_WAIT_TIME, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(SeleniumConstants.PAGE_LOAD_TIME, TimeUnit.SECONDS);
    }

    public static void closeDriver(){
        if (driver != null){
            driver.close();
            driver = null;
        }
    }

    public static void quitDriver(){
        if (driver!=null)
            driver.quit();
        driver = null;
    }

    public static WebDriver getDriver(){
        if (driver != null)
            return driver;

        //SETTING UP FOR SAUCELABS OF STATED IN CONFIG FILE AS "saucelabs"
        if (ConfigReader.readProperty("seleniumHub").equalsIgnoreCase("saucelabs")){
            try {
            DesiredCapabilities capabilities = DesiredCapabilities.chrome();
            capabilities.setCapability("browserName", "chrome");
//                capabilities.setBrowserName(System.getenv("SELENIUM_BROWSER"));
//                capabilities.setVersion(System.getenv("SELENIUM_VERSION"));
//                capabilities.setCapability(CapabilityType.PLATFORM, System.getenv("SELENIUM_PLATFORM"));
            capabilities.setCapability("version", ConfigReader.readProperty("version"));
            capabilities.setCapability("platform", ConfigReader.readProperty("os"));

            //TO TRIGGER SAUCELABS LOCALLY
//            String sauceUsername=System.getenv(ConfigReader.readProperty("sauceUsername"));
//            String sauceKey=System.getenv(ConfigReader.readProperty("sauceKey"));

                driver = new RemoteWebDriver(new URL(URL), capabilities);

                //TO TRIGGER FROM JENKINS
//                String sauceUsername_Jenkins = System.getenv(ConfigReader.readProperty("sauceUsername_Jenkins"));
//                String sauceKey_Jenkins = System.getenv(ConfigReader.readProperty("sauceKey_Jenkins"));

//                capabilities.setCapability("username", sauceUsername);
//                capabilities.setCapability("access-key", sauceKey);
                //driver = new RemoteWebDriver(new URL("https://ondemand.saucelabs.com/wd/hub"), capabilities);

                ((RemoteWebDriver)driver).setFileDetector(new LocalFileDetector());
                sauceSessionId.set(((((RemoteWebDriver)driver).getSessionId().toString())));
                driver.manage().window().maximize();
                driver.manage().timeouts().implicitlyWait(2L, TimeUnit.MINUTES);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else {
            initialize(ConfigReader.readProperty("browser"));
        }
        return driver;
    }



}
