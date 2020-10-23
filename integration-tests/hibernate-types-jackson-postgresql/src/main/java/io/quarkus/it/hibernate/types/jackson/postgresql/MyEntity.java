package io.quarkus.it.hibernate.types.jackson.postgresql;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import io.quarkus.hibernate.types.json.JsonBinaryType;
import io.quarkus.hibernate.types.json.JsonStringType;
import io.quarkus.hibernate.types.json.JsonTypes;

@TypeDef(name = JsonTypes.JSON_STRING, typeClass = JsonStringType.class)
@TypeDef(name = JsonTypes.JSON_BIN, typeClass = JsonBinaryType.class)
@Entity
public class MyEntity {

    @Id
    @Column(name = "ID")
    private String id;

    @Type(type = JsonTypes.JSON_STRING)
    @Column(name = "P_STRING", columnDefinition = "varchar(255)")
    private MyParam varchar;

    @Type(type = JsonTypes.JSON_BIN)
    @Column(name = "P_JSONB", columnDefinition = JsonTypes.JSON_BIN)
    private MyParam jsonb;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MyParam getVarchar() {
        return varchar;
    }

    public void setVarchar(MyParam varchar) {
        this.varchar = varchar;
    }

    public MyParam getJsonb() {
        return jsonb;
    }

    public void setJsonb(MyParam jsonb) {
        this.jsonb = jsonb;
    }
}
