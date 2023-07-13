package be.datafarmhouse.luminadocs.template.data;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class CSSData {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String name;


    @Lob
    private String content;
}
