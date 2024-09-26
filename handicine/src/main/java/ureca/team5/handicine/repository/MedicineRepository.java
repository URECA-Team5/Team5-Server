package ureca.team5.handicine.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ureca.team5.handicine.entity.Medicine;

import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    List<Medicine> findByItemNameContaining(String name); // 이름에 포함된 약품 찾기
}
