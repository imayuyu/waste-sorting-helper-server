package com.charliechiang.wastesortinghelperserver.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "server_settings")
public class ServerSetting {
    @Id
    private String id;
    private String value;
    private String type;

    public ServerSetting() {

    }

    public ServerSetting(String id,
                         String value,
                         String type) {
        this.id = id;
        this.value = value;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
