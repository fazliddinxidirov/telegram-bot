

import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Bot extends TelegramLongPollingBot {

    HttpClient client = HttpClient.newHttpClient();
    String API_KEY = "AIzaSyDQGwmrPUV6KNCbvNvgK-v2rOggxk0N90g";
    public Bot() {
        super("7277869058:AAGE6tT3roTkRxnZYqz2luXihJfcK9N8Qg4");
    }
    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String userInput = message.getText();
//        String prompt = "Could you translate this content from [source language] to [target language] while keeping the cultural context in mind? Make sure the translation is both accurate and culturally appropriate" + userInput;
        String prompt1 = "\"Matnni kiriting, ko‘p tillarga avtomatik tarjima qilaman.\"" + userInput;
//        String prompt2 = "" + userInput;

        String json = "{\n" +
                "   \"contents\":[\n" +
                "      {\n" +
                "         \"parts\":[\n" +
                "            {\n" +
                "               \"text\": \"" + userInput + "\"\n" +
                "            }\n" +
                "         ]\n" +
                "      }\n" +
                "   ]\n" +
                "}";


        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        HttpResponse<String> response = null;
        try {
            response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        JSONObject jsonObject = new JSONObject(response.body());

        JSONArray candidatesArray = jsonObject.getJSONArray("candidates");
        JSONObject firstCandidate = candidatesArray.getJSONObject(0);
        JSONObject contentObject = firstCandidate.getJSONObject("content");
        JSONArray partsArray = contentObject.getJSONArray("parts");
        String text = partsArray.getJSONObject(0).getString("text");

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String getBotUsername() {
        return "@Translete_00bot";
    }
}