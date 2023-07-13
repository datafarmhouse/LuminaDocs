package be.datafarmhouse.luminadocs.template.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CSSRepository extends JpaRepository<CSSData, String> {

    CSSData findByCode(String code);
}
