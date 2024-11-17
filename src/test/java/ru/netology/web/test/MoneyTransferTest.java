package ru.netology.web.test;

import dev.failsafe.internal.util.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPageV2;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.web.data.DataHelper.*;

public class MoneyTransferTest {
    DashboardPage dashboardPage;
    DataHelper.CardInfo firstCardInfo;
    DataHelper.CardInfo secondCardInfo;
    int firstCardBalance;
    int secondCardBalance;


    @BeforeEach
    void voidSetup() {
        var loginPage = open("http://localhost:9999", LoginPageV2.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
        firstCardInfo = DataHelper.getFirstCardInfo();
        secondCardInfo = DataHelper.getSecondCardInfo();
        firstCardBalance = dashboardPage.getCardBalance(0);
        secondCardBalance = dashboardPage.getCardBalance(1);
    }

    @Test
    void transferFromFirstToSecond() {
        int sum = generateValidSum(firstCardBalance);
        int expectedFirstCardBalance = firstCardBalance - sum;
        int expectedSecondCardBalance = secondCardBalance + sum;
        var TransPage = dashboardPage.selectCardToTransfer(secondCardInfo);
        dashboardPage = TransPage.toTransPage(String.valueOf(sum), firstCardInfo);
        int actualFirstCardBalance = dashboardPage.getCardBalance(0);
        int actualSecondCardBalance = dashboardPage.getCardBalance(1);
        dashboardPage.reloadDashboardPage();
        assertAll(() -> assertEquals(expectedFirstCardBalance,
                actualFirstCardBalance),
                () -> assertEquals(expectedSecondCardBalance, actualSecondCardBalance));

    }

    @Test
    void transferFromSecondToFirst() {
        int sum = generateValidSum(secondCardBalance);
        int expectedSecondCardBalance = secondCardBalance - sum;
        int expectedFirstCardBalance = firstCardBalance + sum;
        var TransPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        dashboardPage = TransPage.toTransPage(String.valueOf(sum), secondCardInfo);
        int actualFirstCardBalance = dashboardPage.getCardBalance(0);
        int actualSecondCardBalance = dashboardPage.getCardBalance(1);
        dashboardPage.reloadDashboardPage();
        assertAll(() -> assertEquals(expectedFirstCardBalance, actualFirstCardBalance),
                () -> assertEquals(expectedSecondCardBalance, actualSecondCardBalance));
    }

    @Test
    void transferFromFirstToThird() {
        int sum = generateValidSum(firstCardBalance);
        var thirdCardInfo = getThirdCardInfo();
        var TransPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        dashboardPage = TransPage.toTransPage(String.valueOf(sum), thirdCardInfo);
        TransPage.findError("Ошибка! Произошла ошибка");
    }

    @Test
    void transferMoreBalance() {
        int sum = generateInValidSum(firstCardBalance);
        var TransPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        dashboardPage = TransPage.toTransPage(String.valueOf(sum), secondCardInfo);
        assertAll(() -> TransPage.findError("Ошибка! Произошла ошибка"),
                () -> dashboardPage.reloadDashboardPage(),
                () -> assertEquals(firstCardBalance, dashboardPage.getCardBalance(0)),
                () -> assertEquals(secondCardBalance, dashboardPage.getCardBalance(1)));
    }

}

