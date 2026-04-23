package org.lytvynovych.bettingaiverification.repository;

import org.lytvynovych.bettingaiverification.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
