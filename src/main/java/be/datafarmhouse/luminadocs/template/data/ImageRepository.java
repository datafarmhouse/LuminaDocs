package be.datafarmhouse.luminadocs.template.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ImageData, String> {

    ImageData findByCode(String code);
}
