package bois.chernichkin.translateplugin;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TranslateAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            Editor editor = (e.getDataContext().getData(PlatformDataKeys.EDITOR));
            if (editor != null) {
                String selectedText = editor.getSelectionModel().getSelectedText();
                if (selectedText != null) {
                    translate(selectedText);
                }
            }
        }
    }
    public void translate(String text)
    {
        try {
            // Кодируем текст для вставки в URL
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);

            // Собираем URL для открытия страницы Yandex.Translate
            String url = "https://translate.yandex.ru/?source_lang=auto&target_lang=en&text=" + encodedText;

            // Открываем URL в браузере
            BrowserUtil.browse(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
