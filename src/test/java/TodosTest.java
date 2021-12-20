import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import io.github.bonigarcia.wdm.WebDriverManager;


public class TodosTest {
    WebDriver driver;
    public static Path localDir = Paths.get(".").toAbsolutePath();
    public static final String screenshotsPath = "\\screenshots\\%s.png";

    @BeforeAll
    public static void initialize() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void prepareDriver() {

        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Backbone.js",
            "AngularJS",
            "Dojo",
            "React"
    })
    public void todosTestCase(String techno) throws Exception {
        driver.get("https://todomvc.com");
        WebElement element = driver.findElement(By.linkText(techno));
        element.click();

        String[] todos = new String[]{ "walk the dog", "get the trash out", "work"};

        // add todos
        for(String todo: todos){
            WebElement todoElement = driver.findElement(By.className("new-todo"));
            todoElement.sendKeys(todo);
            todoElement.sendKeys(Keys.ENTER);
            Thread.sleep(2000);
        }

        tickTodo(1);
        assertTodos(2);
        TakesScreenshot screenshot = ((TakesScreenshot) driver);
        File sourceFile = screenshot.getScreenshotAs(OutputType.FILE);
        File destinationFile = new File(localDir.toAbsolutePath().toString() +
                String.format(screenshotsPath, "techno_" + techno));
        FileUtils.copyFile(sourceFile, destinationFile);
    }

    private void tickTodo(int number) {
        driver.findElement(By.cssSelector("li:nth-child(" + number + ") > div > input")).click();
    }

    private void assertTodos(int nb_todos_remaining) throws InterruptedException {
        WebElement element = driver.findElement(By.cssSelector("footer > span > strong"));
        ExpectedCondition<Boolean> expectedConditions =
                ExpectedConditions.textToBePresentInElement(element,  Integer.toString(nb_todos_remaining));
        Thread.sleep(2000);
    }

    @AfterEach
    public void quitDriver() {
        driver.quit();
    }

}
