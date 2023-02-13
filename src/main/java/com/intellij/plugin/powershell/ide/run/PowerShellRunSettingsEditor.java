package com.intellij.plugin.powershell.ide.run;

import com.intellij.execution.configuration.EnvironmentVariablesData;
import com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton;
import com.intellij.ide.macro.MacrosDialog;
import com.intellij.ide.util.BrowseFilesListener;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.plugin.powershell.lang.lsp.ide.settings.PowerShellExecutableChooserPanel;
import com.intellij.ui.components.fields.ExtendableTextField;
import java.io.File;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PowerShellRunSettingsEditor extends SettingsEditor<PowerShellRunConfiguration> {

  private final PowerShellRunConfiguration runConfiguration;
  private JPanel mainPanel;
  private TextFieldWithBrowseButton scriptTextField;
  private JTextField parametersTextField;
  private JTextField commandOptionsTextField;
  private ExtendableTextField workingDirectoryTextField;
  private TextFieldWithBrowseButton workingDirectoryTextFieldWithBrowseBtn;
  private EnvironmentVariablesTextFieldWithBrowseButton
      environmentVariablesTextFieldWithBrowseButton;
  private PowerShellExecutableChooserPanel psExecutableChooserComponent;

  public PowerShellRunSettingsEditor(Project project, PowerShellRunConfiguration runConfiguration) {
    this.runConfiguration = runConfiguration;

    FileChooserDescriptor descriptor =
        new FileChooserDescriptor(true, false, false, false, false, false);
    scriptTextField.addBrowseFolderListener(
        "Chose Script", "Please choose script to run", project, descriptor);
  }

  @Override
  protected void resetEditorFrom(@NotNull PowerShellRunConfiguration configuration) {
    String configName = configuration.getName();
    String scriptPath = configuration.getScriptPath();
    String workingDir = configuration.getWorkingDirectory();
    EnvironmentVariablesData envVars = configuration.getEnvironmentVariables();
    String executablePath = configuration.getExecutablePath();

    if (workingDir.equals(PSExecutionUtilKt.getDefaultWorkingDirectory())) {
      workingDirectoryTextField.getEmptyText().setText(workingDir);
    } else {
      workingDirectoryTextFieldWithBrowseBtn.setText(workingDir);
    }
    String scriptParameters = configuration.getScriptParameters();
    String commandOptions = configuration.getCommandOptions();
    if (!StringUtil.isEmpty(scriptPath)) {
      scriptTextField.setText(scriptPath);
      String[] parts = scriptPath.split(File.separatorChar == '/' ? "/" : "[\\\\/]");

      if (!StringUtil.isEmpty(configName)) {
        runConfiguration.setName(configName);
      } else if (parts.length > 0) {
        runConfiguration.setName(parts[parts.length - 1]);
      }
    }
    if (!StringUtil.isEmpty(scriptParameters)) {
      parametersTextField.setText(scriptParameters);
    }
    if (!StringUtil.isEmpty(commandOptions)) {
      commandOptionsTextField.setText(commandOptions);
    }

    environmentVariablesTextFieldWithBrowseButton.setData(envVars);
    psExecutableChooserComponent.updateExecutablePath(executablePath);
  }

  @Override
  protected void applyEditorTo(@NotNull PowerShellRunConfiguration configuration) {
    configuration.setScriptPath(scriptTextField.getText().trim());
    configuration.setWorkingDirectory(workingDirectoryTextFieldWithBrowseBtn.getText().trim());
    configuration.setScriptParameters(parametersTextField.getText().trim());
    configuration.setCommandOptions(commandOptionsTextField.getText().trim());
    configuration.setEnvironmentVariables(environmentVariablesTextFieldWithBrowseButton.getData());
    configuration.setExecutablePath(psExecutableChooserComponent.getExecutablePath());
  }

  @NotNull
  @Override
  protected JComponent createEditor() {
    return mainPanel;
  }

  @Nullable
  public String getScriptName() {
    return runConfiguration.getScriptPath();
  }

  private void createUIComponents() {
    workingDirectoryTextField = new ExtendableTextField();
    workingDirectoryTextFieldWithBrowseBtn =
        new TextFieldWithBrowseButton(workingDirectoryTextField);
    workingDirectoryTextFieldWithBrowseBtn.addBrowseFolderListener(
        "Choose directory",
        null,
        runConfiguration.getProject(),
        BrowseFilesListener.SINGLE_DIRECTORY_DESCRIPTOR,
        TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);
    JTextField textField = workingDirectoryTextFieldWithBrowseBtn.getChildComponent();
    FileChooserFactory.getInstance()
        .installFileCompletion(
            textField, BrowseFilesListener.SINGLE_DIRECTORY_DESCRIPTOR, true, null);
    MacrosDialog.addMacroSupport(workingDirectoryTextField, MacrosDialog.Filters.ALL, () -> false);
    psExecutableChooserComponent =
        new PowerShellExecutableChooserPanel(runConfiguration.getExecutablePath());
  }
}
