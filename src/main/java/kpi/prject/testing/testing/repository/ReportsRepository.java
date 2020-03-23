package kpi.prject.testing.testing.repository;

import kpi.prject.testing.testing.entity.Report;
import kpi.prject.testing.testing.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportsRepository extends JpaRepository<Report, Long> {
    Optional<Page<Report>> findByUser(User user , Pageable page);
}