package com.mm.v1;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.time.Duration;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

import com.mm.v1.requests.AuthorizationRequest;

public class AuthorizationDriver {

    private String client_id = "7cbd084df6f043f1addef58bc5057f7a";
    private String redirect_uri = "http://localhost:8080/spotify";
    private String scopes = "user-read-currently-playing user-read-playback-state user-modify-playback-state";
    private String username = "maroldaluke@gmail.com";
    private String password = "guwxa8-syhcar-kAzhef";

    public void authorize()  {

        // set the path to the ChromeDriver executable
        System.setProperty("webdriver.chrome.driver", "core/bin/chrome/chromedriver");

        // create a new instance of the ChromeDriver
        WebDriver driver = new ChromeDriver();

        // navigate to the Spotify authorization URL
        driver.get("https://accounts.spotify.com/authorize?client_id=" + client_id + 
                   "&response_type=code&redirect_uri=" + redirect_uri + "&scope=" + scopes);

        System.out.println("### Sending password credentials ###");

        // fill in login credentials if necessary
        driver.findElement(By.id("login-username")).sendKeys(username);
        driver.findElement(By.id("login-password")).sendKeys(password);

        driver.findElement(By.id("login-button")).click();

        /** TODO: may have to add an additional button click */

        boolean retry = false;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("login-button")));
        } catch (TimeoutException e)    {
            System.out.println("Exception: Login button click failed. Trying again");
            retry = true;
        }

        if (retry)  {
            driver.findElement(By.id("login-button")).click();
            try {
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("login-button")));
            } catch (TimeoutException e)    {
                System.out.println("Exception: Login button click failed. Fatal.");
            }
        }

        // close the browser
        // driver.quit();

    }

    public void get_driver()    {

        String desiredVersion = "121.0.6167.184";

        try {
            // Construct the download URL for the ChromeDriver binary
            String downloadUrl = "https://storage.googleapis.com/chrome-for-testing-public/" + desiredVersion + "/mac-x64/chromedriver-mac-x64.zip";
            // Download the ChromeDriver binary
            URL url = new URL(downloadUrl);
            ReadableByteChannel channel = Channels.newChannel(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream("chromedriver_mac64.zip");
            fileOutputStream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
            fileOutputStream.close();
            channel.close();

            System.out.println("ChromeDriver " + desiredVersion + " downloaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
