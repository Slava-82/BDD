package ru.netology.web.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.*;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.web.data.DataHelper.*;

class MoneyTransferTest {

    DashboardPage dashboardPage;
    CardInfo firstCardInfo;
    CardInfo secondCardInfo;
    int firstCardBalance;
    int secondCardBalance;


    @BeforeEach
    void setup() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode();
        dashboardPage = verificationPage.validVerify(verificationCode);
        firstCardInfo = DataHelper.getFirstCard();
        secondCardInfo = DataHelper.getSecondCard();
        firstCardBalance = dashboardPage.getCardBalance(DataHelper.getMaskedNumber(firstCardInfo.getCardNumber()));
        secondCardBalance = dashboardPage.getCardBalance(1);
    }

    @Test
    void shouldTransferMoneyFrom1To2() {

        var amount = generateValidAmount(firstCardBalance);
        var expectedBalanceFirstCard = firstCardBalance - amount;
        var expectedBalanceSecondCard = secondCardBalance + amount;
        var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);
        dashboardPage.reloadDashboardPage();
        var actualBalanceFirstCard = dashboardPage.getCardBalance(getMaskedNumber(firstCardInfo.getCardNumber()));
        var actualBalanceSecondCard = dashboardPage.getCardBalance(1);
        assertAll(() -> assertEquals(expectedBalanceFirstCard,
                        actualBalanceFirstCard),
                () -> assertEquals(expectedBalanceSecondCard, actualBalanceSecondCard));
    }

    @Test
    void shouldTransferMoneyBetweenOwnCardsSF() {
        int sum = generateValidAmount(secondCardBalance);
        var expectedBalanceSecondCard = dashboardPage.getCardBalance(1) - sum;
        var expectedBalanceFirstCard = dashboardPage.getCardBalance(0) + sum;
        var transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(sum), secondCardInfo);
        dashboardPage.reloadDashboardPage();
        var actualBalanceSecondCard = dashboardPage.getCardBalance(1);
        var actualBalanceFirstCard = dashboardPage.getCardBalance(0);
        assertAll(() -> assertEquals(expectedBalanceFirstCard,
                        actualBalanceFirstCard),
                () -> assertEquals(expectedBalanceSecondCard, actualBalanceSecondCard));
    }

    @Test
    void shouldErrorMassage() {
        var amount = generateInvalidAmount(secondCardBalance);
        var transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        transferPage.makeTransfer(String.valueOf(amount), secondCardInfo);
        transferPage.findErrorMessage("Выполнена попытка перевода суммы, превышающей остаток на карте списания");
        dashboardPage.reloadDashboardPage();

        var actualBalanceFirstCard = dashboardPage.getCardBalance(getMaskedNumber(firstCardInfo.getCardNumber()));
        var actualBalanceSecondCard = dashboardPage.getCardBalance(getMaskedNumber(secondCardInfo.getCardNumber()));

        assertAll(() -> assertEquals(firstCardBalance, actualBalanceFirstCard),
                () -> assertEquals(secondCardBalance, actualBalanceSecondCard));
    }
    @Test
    void shouldGetErrorMessageIfCardFromNotExist(){
        var amount = generateValidAmount(firstCardBalance);
        var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
        transferPage.makeTransfer(String.valueOf(amount), getThirdCardInfo());
        transferPage.findErrorMessage("Ошибка! Произошла ошибка");
    }
}


