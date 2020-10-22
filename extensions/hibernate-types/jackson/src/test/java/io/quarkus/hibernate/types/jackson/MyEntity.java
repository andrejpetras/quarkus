package io.quarkus.hibernate.types.jackson;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import io.quarkus.hibernate.types.json.JsonStringType;
import io.quarkus.hibernate.types.json.JsonTypes;

@TypeDef(name = JsonTypes.JSON_STRING, typeClass = JsonStringType.class)
@Entity
public class MyEntity {

    @Id
    @Column(name = "ID")
    private String id;

    @Type(type = JsonTypes.JSON_STRING)
    @Column(name = "PARAM", columnDefinition = "varchar(255)")
    private MyParam param;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MyParam getParam() {
        return param;
    }

    public void setParam(MyParam param) {
        this.param = param;
    }
}
