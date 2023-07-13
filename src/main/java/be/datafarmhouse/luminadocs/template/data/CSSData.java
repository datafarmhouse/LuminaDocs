package be.datafarmhouse.luminadocs.template.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

@Data
@Entity
public class CSSData {

    @Id
    private String code;
    private String name;


    @Lob
    private String content;
}
