package org.example.programmjavafx.Server.entityDB;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import java.sql.Timestamp;

/** Класс Message,
 * представляет собой объект - сообщение и его поля,
 * которые по сути являются колонками для отображения информации в окне tableView
 *  **/
public class Message
{
    //  SimpleStringProperty используется для различных полей,
    //  чтобы обеспечить автоматическое обновление интерфейса при изменении значений
    private final SimpleStringProperty plateNumberColumn;
    private final SimpleStringProperty messageColumn;
    private final SimpleStringProperty typeEventColumn;
    private final SimpleObjectProperty<Timestamp> timeEventColumn;

    public Message(String plateNumberColumn, String messageColumn, String typeEventColumn, Timestamp timeEventColumn)
    {
        this.plateNumberColumn = new SimpleStringProperty(plateNumberColumn);
        this.messageColumn = new SimpleStringProperty(messageColumn);
        this.typeEventColumn = new SimpleStringProperty(typeEventColumn);
        this.timeEventColumn = new SimpleObjectProperty<>(timeEventColumn);
    }

    public String getPlateNumberColumn()
    {
        return plateNumberColumn.get();
    }

    public SimpleStringProperty plateNumberColumnProperty()
    {
        return plateNumberColumn;
    }

    public String getMessageColumn()
    {
        return messageColumn.get();
    }

    public SimpleStringProperty messageColumnProperty()
    {
        return messageColumn;
    }

    public String getTypeEventColumn()
    {
        return typeEventColumn.get();
    }

    public SimpleStringProperty typeEventColumnProperty()
    {
        return typeEventColumn;
    }

    public Timestamp getTimeEventColumn()
    {
        return timeEventColumn.get();
    }

    public SimpleObjectProperty<Timestamp> timeEventColumnProperty()
    {
        return timeEventColumn;
    }
}
