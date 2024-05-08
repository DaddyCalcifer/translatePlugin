package bois.chernichkin.translateplugin;

import com.ibm.icu.text.Transliterator;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.externalSystem.model.ProjectKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class TransliterationAction extends AnAction {
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
            // Заменяем выделенный текст на транслитерированный
            Transliterator transliterator = Transliterator.getInstance("Russian-Latin/BGN");
            String transliteratedText = transliterator.transliterate(selectedText);
            editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), transliteratedText);
        });
    }
}
