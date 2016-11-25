package address.view;

/**
 * Created by mrhri on 16.11.2016.
 */
import address.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

/**
 * Контроллер для корневого макета. Корневой макет предоставляет базовый
 * макет приложения, содержащий строку меню и место, где будут размещены
 * остальные элементы JavaFX.
 */
public class RootLayoutController {

    // Ссылка на главное приложение
    private MainApp mainApp;

    /**
     * Вызывается главным приложением, чтобы оставить ссылку на самого себя.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Создаёт пустую адресную книгу.
     */
    @FXML
    private void handleNew() {
        mainApp.getPersonData().clear();
        mainApp.setPersonFilePath(null);
    }

    /**
     * Открывает FileChooser, чтобы пользователь имел возможность
     * выбрать адресную книгу для загрузки.
     */
    @FXML
    private void handleOpen() throws IOException {
        FileChooser fileChooser = new FileChooser();

        // Задаём фильтр расширений
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter(
                "JSON files (*.json)", "*.json");
        FileChooser.ExtensionFilter extFilter3 = new FileChooser.ExtensionFilter(
                "CSV file(*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.getExtensionFilters().add(extFilter2);
        fileChooser.getExtensionFilters().add(extFilter3);

        // Показываем диалог загрузки файла
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        if (file != null) {
            if(file.getPath().endsWith(".xml")){
                mainApp.loadPersonDataFromFile(file);
            }
            if(file.getPath().endsWith(".json")){
                mainApp.loadPersonDataFromJSON(file);
            }
            if (file.getPath().endsWith(".csv")){
                mainApp.loadPersonDataFromCSV(file);
            }

        }
    }

    /**
     * Сохраняет файл в файл адресатов, который в настоящее время открыт.
     * Если файл не открыт, то отображается диалог "save as".
     */
    @FXML
    private void handleSave() throws IOException {
        File personFile = mainApp.getPersonFilePath();
        if (personFile != null) {
            if(personFile.getPath().endsWith(".xml")){
                mainApp.savePersonDataToFile(personFile);
            }
            if(personFile.getPath().endsWith(".json")){
                mainApp.savePersonDataToJSON(personFile);
            }
            if(personFile.getPath().endsWith(".csv")){
                mainApp.savePersonDataToCSV(personFile);
            }

        } else {
            SaveAsXML();
        }
    }

    /**
     * Открывает FileChooser, чтобы пользователь имел возможность
     * выбрать файл, куда будут сохранены данные
     */
    @FXML
    private void SaveAsXML() {

        FileChooser fileChooser = new FileChooser();
        // Задаём фильтр расширений
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Показываем диалог сохранения файла
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        mainApp.savePersonDataToFile(file);
    }
    @FXML
    private void SaveAsJSON() {

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter =
                new FileChooser.ExtensionFilter("JSON file (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        mainApp.savePersonDataToJSON(file);
    }
    @FXML
    private void SaveAsCSV() throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter =
                new FileChooser.ExtensionFilter("CSV file (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        mainApp.savePersonDataToCSV(file);
    }

    /**
     * Открывает диалоговое окно about.
     */
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Адресная книга");
        alert.setHeaderText("О продукте");
        alert.setContentText("Автор: Гринин Владимир\nВэбсайт: http://vk.com/grininininin");

        alert.showAndWait();
    }

    /**
     * Закрывает приложение.
     */
    @FXML
    private void handleExit() {
        System.exit(0);
    }
}