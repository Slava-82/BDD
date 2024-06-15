
package ru.netology.web.page;

import com.codeborne.selenide.SelenideElement;
import ru.netology.web.data.DataHelper;

import static com.codeborne.selenide.Selenide.$;


public class LoginPage {
    private final SelenideElement loginElement = $("[data-test-id=login] input");
    private final SelenideElement passwordElement = $("[data-test-id=password] input");
    private final SelenideElement loginButton = $("[data-test-id='action-login']");

    public VerificationPage validLogin(DataHelper.AuthInfo info) {
        loginElement.setValue(info.getLogin());
        passwordElement.setValue(info.getPassword());
        loginButton.click();
        return new VerificationPage();
    }
}
