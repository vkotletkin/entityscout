package ru.kotletkin.entityscout.ui;

import io.javelit.core.Jt;
import io.javelit.core.Server;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kotletkin.entityscout.language.LanguageService;

@Service
@RequiredArgsConstructor
public class JavelitService {

    private final LanguageService languageService;

    @PostConstruct
    public void startJavelitServer() {
        var server = Server.builder(this::app, 8888).build();
        server.start();
    }

    private void app() throws InterruptedException {
        var form = Jt.form().use();

        String name = Jt.textInput("Your Name").use(form);
        String message = Jt.textArea("Message").use(form);

        if (Jt.formSubmitButton("Send Message").use(form)) {
            Jt.text("Message sent successfully!").use();
            Jt.text("From: " + name).use();
            Jt.text("Language: " + languageService.detectLanguage(message).language()).use();
        }
    }
}
