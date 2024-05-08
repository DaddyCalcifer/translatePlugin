package bois.chernichkin.translateplugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.icu.text.Transliterator;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TranslateAction extends AnAction {
    private static final String API_KEY = "AQVN0p2PkEPRepITuVYgedWRC6YIUR4LRKNXZnrz";
    private static final String FOLDER_ID = "b1g2cep9bs1e44iudb9a";
    private static final String TARGET_LANGUAGE = "en";
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // Получаем проект и редактор
        Project project = e.getProject();
        Editor editor = e.getDataContext().getData(PlatformDataKeys.EDITOR);

        // Проверяем, что редактор доступен
        if (editor == null) {
            Messages.showMessageDialog(project, "Редактор не доступен!", "Ошибка", Messages.getErrorIcon());
            return;
        }

        // Получаем модель выделения текста
        SelectionModel selectionModel = editor.getSelectionModel();
        // Получаем выделенный текст
        String selectedText = selectionModel.getSelectedText();

        // Проверяем, что есть выделенный текст
        if (selectedText == null || selectedText.isEmpty()) {
            Messages.showMessageDialog(project, "Нет выделенного текста!", "Ошибка", Messages.getErrorIcon());
            return;
        }
        // Создаем действие записи для модификации документа
        WriteCommandAction.runWriteCommandAction(project, () -> {
            String translated = "";
            try {
                translated = translateText(selectedText);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), translated);
        });
    }
    public static String translateText(String sourceText) throws IOException {
        String urlString = "https://translate.api.cloud.yandex.net/translate/v2/translate";
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Api-Key " + API_KEY);
        connection.setDoOutput(true);

        String jsonInputString = "{\"targetLanguageCode\": \"" + TARGET_LANGUAGE + "\", \"texts\": [\"" + sourceText + "\"]}";
        byte[] input = jsonInputString.getBytes("utf-8");
        connection.setRequestProperty("Content-Length", String.valueOf(input.length));

        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        JsonElement jsonElement = JsonParser.parseString(response.toString());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonArray translations = jsonObject.getAsJsonArray("translations");
        JsonObject translationObject = translations.get(0).getAsJsonObject();

        return translationObject.get("text").getAsString()
                .replace(" ","_")
                .replace("'","")
                .replace(",","")
                .replace(".","")
                .toLowerCase();
    }
}
