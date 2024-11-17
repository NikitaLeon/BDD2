package ru.netology.web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ru.netology.web.data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class TransPage {

    private final SelenideElement sumField = $("[data-test-id=amount] .input__control");
    private final SelenideElement fromField = $("[data-test-id=from] .input__control");
    private final SelenideElement button = $("[data-test-id=action-transfer]");
    private final SelenideElement transHead = $(byText("Пополнение карты"));
    private final SelenideElement transError = $("[data-test-id=error-notification] .notification__content");

    public TransPage() {
        transHead.shouldBe(visible);
    }

    public DashboardPage toTransPage(String sumToTransfer, DataHelper.CardInfo cardInfo) {
        makeTransfer(sumToTransfer, cardInfo);
        return new DashboardPage();
    }

    public void makeTransfer(String sumToTransfer, DataHelper.CardInfo cardInfo) {
        sumField.setValue(sumToTransfer);
        fromField.setValue(cardInfo.getCardNumber());
        button.click();
    }

    public void findError(String expectedText) {
        transError.shouldHave(Condition.text(expectedText), Duration.ofSeconds(15)).shouldBe(visible);
    }



}
