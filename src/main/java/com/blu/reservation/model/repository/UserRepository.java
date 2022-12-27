package com.blu.reservation.model.repository;

import com.blu.reservation.model.PanelUser;
import com.blu.reservation.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<PanelUser,Integer> {

    List<PanelUser> findAllByRole(Role role);


}
