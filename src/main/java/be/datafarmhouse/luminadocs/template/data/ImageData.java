package be.datafarmhouse.luminadocs.template.data;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Blob;

@Data
@Entity(name = "images")
public class ImageData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String code;


    @Lob
    private Blob content;
}
