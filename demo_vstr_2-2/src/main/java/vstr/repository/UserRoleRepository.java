package vstr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vstr.model.Role;

@Repository
public interface UserRoleRepository extends JpaRepository<Role, Long> {
    Role findRoleByUsers_Id(Long userId);

    Role findByName(String name);
}
