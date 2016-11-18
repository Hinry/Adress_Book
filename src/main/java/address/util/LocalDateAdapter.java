package address.util;

/**
 * Created by mrhri on 16.11.2016.
 */
import java.time.LocalDate;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocalDateAdapter extends XmlAdapter {

    @Override
    public LocalDate unmarshal(Object v) throws Exception {
        return LocalDate.parse((CharSequence) v);
    }

    @Override
    public String marshal(Object v) throws Exception {
        return v.toString();
    }
}