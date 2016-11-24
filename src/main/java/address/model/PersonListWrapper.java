package address.model;

/**
 * Created by mrhri on 16.11.2016.
 */
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * Вспомогательный класс для обёртывания списка адресатов.
 * Используется для сохранения списка адресатов в XML.
 */

@XmlRootElement(name = "persons")
@XmlSeeAlso(value = Person.class)
public class PersonListWrapper {
    private List persons;

    @XmlElement(name = "person")
    public List getPersons() {
        return persons;
    }
    public void setPersons(List persons) {
        this.persons = persons;
    }
}
