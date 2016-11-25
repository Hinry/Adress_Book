package address;

import address.model.Person;
import address.model.PersonListWrapper;
import address.view.PersonEditDialogController;
import address.view.PersonOverviewController;
import address.view.RootLayoutController;
import com.google.gson.Gson;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.hildan.fxgson.FxGson;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class MainApp extends Application {

    transient private Stage primaryStage;
    transient private BorderPane rootLayout;
    /**
     * Данные, в виде наблюдаемого списка адресатов.
     */
    private ObservableList<Person> personData = FXCollections.observableArrayList();

    /**
     * Конструктор
     */
    public MainApp() {

    }

    public void adData(){
        // В качестве образца добавляем некоторые данные
        personData.add(new Person("Вася", "Петичкин"));
        personData.add(new Person("Игорь", "Коновалов"));
        personData.add(new Person("Петя", "Васичкин"));
        personData.add(new Person("Алиса", "Чудесная"));
        personData.add(new Person("Владимир", "Гринин"));
        personData.add(new Person("Антон", "Городецкий"));
        personData.add(new Person("Филип", "Киркоров"));
        personData.add(new Person("Britney", "Spears"));
        personData.add(new Person("Martin", "Luter"));

    }



    /**
     * Возвращает данные в виде наблюдаемого списка адресатов.
     * @return
     */
    public ObservableList<Person> getPersonData() {
        return personData;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Адресная книга");
        this.primaryStage.getIcons().add(new Image("/images/adrbook.png"));

        initRootLayout();

        showPersonOverview();
    }

    /**
     * Инициализация главного окна.
     */
    public void initRootLayout() throws IOException {
        try {
            // Загружаем корневой макет из fxml файла.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class
                    .getResource("/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Отображаем сцену, содержащую корневой макет.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            // Даём контроллеру доступ к главному приложению.
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Пытается загрузить последний открытый файл с адресатами.
        File file = getPersonFilePath();
        if (file != null) {
            if (file.getPath().endsWith(".xml")){
                loadPersonDataFromFile(file);
            }
            if (file.getPath().endsWith(".json")){
                loadPersonDataFromJSON(file);
            }

        }
    }

    /**
     * Вывод контактов внутри главного окна.
     */
    public void showPersonOverview() {
        try {
            // Загружаем обзор контактов
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/PersonOverview.fxml"));
            AnchorPane personOverview = (AnchorPane) loader.load();
		
            rootLayout.setCenter(personOverview);
            // Даём контроллеру доступ к главному приложению.
            // Даём контроллеру доступ к главному приложению.
            PersonOverviewController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Открывает диалоговое окно для изменения деталей указанного адресата.
     * Если пользователь кликнул OK, то изменения сохраняются в предоставленном
     * объекте адресата и возвращается значение true.
     *
     * @param person - объект адресата, который надо изменить
     * @return true, если пользователь кликнул OK, в противном случае false.
     */
    public boolean showPersonEditDialog(Person person) {
        try {
            // Загружаем fxml-файл и создаём новую сцену
            // для всплывающего диалогового окна.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/PersonEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Создаём диалоговое окно Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Редактировать контакт");
            dialogStage.getIcons().add(new Image("/images/edit.png"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Передаём адресата в контроллер.
            PersonEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPerson(person);

            // Отображаем диалоговое окно и ждём, пока пользователь его не закроет
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Возвращает preference файла адресатов, то есть, последний открытый файл.
     * Этот preference считывается из реестра, специфичного для конкретной
     * операционной системы. Если preference не был найден, то возвращается null.
     *
     * @return
     */
    public File getPersonFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /**
     * Задаёт путь текущему загруженному файлу. Этот путь сохраняется
     * в реестре, специфичном для конкретной операционной системы.
     *
     * @param file - файл или null, чтобы удалить путь
     */
    public void setPersonFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());

            // Обновление заглавия сцены.
            primaryStage.setTitle("Адресная книга - " + file.getName());
        } else {
            prefs.remove("filePath");

            // Обновление заглавия сцены.
            primaryStage.setTitle("Адресная книга");
        }
    }

    /**
     * Загружает информацию о контактах из указанного файла.
     * Текущая информация будет заменена.
     *
     * @param file
     */
    public void loadPersonDataFromFile(File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(PersonListWrapper.class);
            Unmarshaller um = context.createUnmarshaller();

            // Чтение XML из файла и демаршализация.
            PersonListWrapper wrapper = (PersonListWrapper) um.unmarshal(file);

            personData.clear();
            personData.addAll(wrapper.getPersons());

            // Сохраняем путь к файлу в реестре.
            setPersonFilePath(file);

        } catch (Exception e) {
            // если есть ошибка
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Не удалось загрузить данные");
            alert.setContentText("Не удалось загрузить данные из:\n" + file.getPath());

            alert.showAndWait();
        }
    }

    /**
     * Сохраняет текущие контакты в указанном файле.xml
     *
     * @param file
     */
    public void savePersonDataToFile(File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(PersonListWrapper.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            // Обёртываем наши данные об адресатах.
            PersonListWrapper wrapper = new PersonListWrapper();
            wrapper.setPersons(personData);

            // Маршаллируем и сохраняем XML в файл.
            marshaller.marshal(wrapper, file);

        } catch (JAXBException e) {
            //если есть ошибка
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Не удалось сохранить данные");
            alert.setContentText("Не удалось сохранить данные в файл:\n" + file.getPath());

            alert.showAndWait();
        }
    }
    
	/**
	 * Возврат на главную страницу
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Сохраняет текущие контакты в формате JSON
     *
     */
    public void savePersonDataToJSON(File file) {
        // Обёртываем наши данные об адресатах.
        //PersonListWrapper wrapper = new PersonListWrapper();
        //wrapper.setPersons(personData);
        Gson gson = FxGson.coreBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        String string = gson.toJson(MainApp.this);
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(string);
        } catch (IOException ex) {
            Logger.getLogger(MainApp.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

    }
    /**
     * Загружает контакты из файла JSON
     *
     */
    public void loadPersonDataFromJSON(File file) throws IOException {
        FxGson gson2 = new FxGson();

        try {
            InputStream in = null;
            in  = new FileInputStream(file);
            byte[] buf = new byte[in.available()];
            int readed = in.read(buf);
            StringBuilder sb = new StringBuilder();
            while ( readed > 0){
                sb.append(new String(buf,0,readed));
                readed = in.read(buf);
            }
            // записываем из буфера в файл
            MainApp object;
            object = gson2.create().fromJson(sb.toString(), MainApp.class);
            ObservableList<Person> per = FXCollections.observableArrayList();
            per.addAll(object.getPersonData());
            personData.clear();
            personData.addAll(per);
            setPersonFilePath(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainApp.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Сохраняет текущие контакты в формате CSV
     *
     */
    public void savePersonDataToCSV(File file) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(file));

        ArrayList<Person> persons = new ArrayList();
        persons.addAll(getPersonData());

        List<String[]> data = new ArrayList<>(persons.size());

        String[][] persona = new String[persons.size()][6];

        for(int i = 0; i <persons.size(); i++){
            persona[i][0] = getPersonData().get(i).getFirstName();
            persona[i][1] = getPersonData().get(i).getLastName();
            persona[i][2] = getPersonData().get(i).getPhoneNumber();
            persona[i][3] = getPersonData().get(i).getStreet();
            persona[i][4] = getPersonData().get(i).getMail();
            persona[i][5] = getPersonData().get(i).getCity();
        }
        for (int a = 0 ; a <persons.size();a++){
            data.add(persona[a]);
        }
        writer.writeAll(data);
        writer.close();
    }
    /**
     * Загружает контакты из файла CSV
     *
     */
    public void loadPersonDataFromCSV(File file) throws IOException {

        //Очищаем текущий лист и добавляем в него прочитанные обьекты
        personData.clear();
        CSVReader csvReader = new CSVReader(new FileReader(file));
        String[] nextLine;
        while ((nextLine = csvReader.readNext()) != null) {
            Person per = new Person();
            per.setFirstName(nextLine[0]);
            per.setLastName(nextLine[1]);
            per.setPhoneNumber(nextLine[2]);
            per.setMail(nextLine[3]);
            per.setStreet(nextLine[4]);
            per.setCity(nextLine[5]);
            personData.add(per);
        }
        setPersonFilePath(file);
    }
}
