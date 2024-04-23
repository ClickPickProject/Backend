package com.clickpick.repository;

import com.clickpick.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository <Admin, String> {

    Optional<Admin> findById(String admin_id);
}
