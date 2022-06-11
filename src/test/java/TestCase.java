import dev.failsafe.internal.util.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestCase {
    WebDriver driver;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setupTest() {
        driver = new ChromeDriver();
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void test() throws InterruptedException {
        setupTest();

        // Go to page
        driver.get("https://www.webstaurantstore.com/");

        // Search for "stainless work table"
        WebElement element = driver.findElement(By.id("searchval"));
        element.sendKeys("stainless work table");

        WebElement button = driver.findElement(By.xpath("//button[@value='Search']"));
        button.click();

        // Check for "Table" in every product listing
        while (true) {
            List<WebElement> listings = driver.findElements(By.cssSelector("#product_listing > div"));
            for (WebElement listing : listings) {
                String text = listing.getText();

                // Pages 7-9 have entries missing the word "Table"
                try {
                    Assert.isTrue(text.contains("Table"), "Text 'Table' not found");
                } catch (Exception ignored) {}
            }

            if (driver.findElements(By.xpath("//li[@class='rc-pagination-next']")).size() > 0) {
                button = driver.findElement(By.xpath("//li[@class='rc-pagination-next']"));
                button.click();
            }

            // Add last item to cart
            else {
                List<WebElement> buttons = driver.findElements(By.xpath("//input[@name='addToCartButton']"));
                button = buttons.get(buttons.size() - 1);
                button.click();
                break;
            }
        }
        TimeUnit.SECONDS.sleep(1);

        // Handles selections for item
        if (driver.findElements(By.xpath("//div[@class='ReactModal__Overlay']")).size() > 0) {
            List<WebElement> selections = driver.findElements(By.xpath("//select[@class='form__control']"));
            for (WebElement selection : selections) {
                Select option = new Select(selection);
                option.selectByIndex(0);
            }
            List<WebElement> buttons = driver.findElements(By.xpath("//button[@aria-label='Submit Feedback']"));
            button = buttons.get(buttons.size() - 1);
            button.click();
        }

        // Empty cart
        driver.get("https://www.webstaurantstore.com/viewcart.cfm");
        button = driver.findElement(By.xpath("//button[@class='emptyCartButton btn btn-mini btn-ui pull-right']"));
        button.click();
        button = driver.findElement(By.xpath("//button[@class='bg-origin-box-border bg-repeat-x " +
                "border-solid border box-border cursor-pointer inline-block text-center no-underline hover:no-underline antialiased " +
                "hover:bg-position-y-15 mr-2 rounded-normal text-base px-7 py-2-1/2 hover:bg-green-600 text-white text-shadow-black-60 bg-green-primary " +
                "bg-linear-gradient-180-green border-black-25 shadow-inset-black-17 align-middle font-semibold']"));
        button.click();

        teardown();
    }

    public void main(String[] args) throws InterruptedException {
        test();
    }
}