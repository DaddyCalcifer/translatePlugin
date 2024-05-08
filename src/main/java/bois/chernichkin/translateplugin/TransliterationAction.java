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
        // �������� ������ � ��������
        Project project = e.getProject();
        Editor editor = e.getDataContext().getData(PlatformDataKeys.EDITOR);

        // ���������, ��� �������� ��������
        if (editor == null) {
            Messages.showMessageDialog(project, "�������� �� ��������!", "������", Messages.getErrorIcon());
            return;
        }

        // �������� ������ ��������� ������
        SelectionModel selectionModel = editor.getSelectionModel();
        // �������� ���������� �����
        String selectedText = selectionModel.getSelectedText();

        // ���������, ��� ���� ���������� �����
        if (selectedText == null || selectedText.isEmpty()) {
            Messages.showMessageDialog(project, "��� ����������� ������!", "������", Messages.getErrorIcon());
            return;
        }

        // ������� �������� ������ ��� ����������� ���������
        WriteCommandAction.runWriteCommandAction(project, () -> {
            // �������� ���������� ����� �� �������������������
            Transliterator transliterator = Transliterator.getInstance("Russian-Latin/BGN");
            String transliteratedText = transliterator.transliterate(selectedText);
            editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), transliteratedText);
        });
    }
}
