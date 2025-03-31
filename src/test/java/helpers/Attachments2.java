package helpers;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static com.codeborne.selenide.Selenide.sessionId;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.openqa.selenium.logging.LogType.BROWSER;

public class Attachments2 {

    @Attachment(value = "{attachName}", type = "image/png")
    public static byte[] screenshotAs(String attachName) {
        if (getWebDriver() == null) {
            System.err.println("WebDriver не запущен, невозможно сделать скриншот!");
            return new byte[0];
        }
        return ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
    }

    @Attachment(value = "Page source", type = "text/plain")
    public static byte[] pageSource() {
        if (getWebDriver() == null) {
            return "WebDriver не запущен".getBytes(StandardCharsets.UTF_8);
        }
        return getWebDriver().getPageSource().getBytes(StandardCharsets.UTF_8);
    }

    @Attachment(value = "{attachName}", type = "text/plain")
    public static String attachAsText(String attachName, String message) {
        return message;
    }

    public static void browserConsoleLogs() {
        var logs = Selenide.getWebDriverLogs(BROWSER);
        attachAsText("Browser console logs", logs.isEmpty() ? "Логов нет" : String.join("\n", logs));
    }

    @Attachment(value = "Video", type = "text/html", fileExtension = ".html")
    public static String addVideo() {
        URL videoUrl = getVideoUrl();
        String urlString = (videoUrl != null) ? videoUrl.toString() : "https://example.com/placeholder.mp4";

        return "<html><body><video width='100%' height='100%' controls autoplay>" +
                "<source src='" + urlString + "' type='video/mp4'></video></body></html>";
    }

    public static URL getVideoUrl() {
        String videoUrl = "https://selenoid.autotests.cloud/video/" + sessionId() + ".mp4";
        try {
            return new URL(videoUrl);
        } catch (MalformedURLException e) {
            System.err.println("Ошибка формирования URL: " + e.getMessage());
            return null;
        }
    }
}
