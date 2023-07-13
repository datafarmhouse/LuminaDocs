package be.datafarmhouse.luminadocs.template.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateRepository extends JpaRepository<TemplateData, String> {
}
