package com.resumereviewer.webapp.repository;

import com.resumereviewer.webapp.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface UserEntityRepository extends CrudRepository<UserEntity, Integer> {

    UserEntity findByEmail(String email);

    UserEntity findByUsername(String username);

    Boolean existsByUsername(String username);

    Optional<UserEntity> findByFirebaseUid(String firebaseUid);

    void deleteByUsername(String username);

    ArrayList<UserEntity> findAll();

    UserEntity save(UserEntity userEntity);

    void deleteById(int id);


}
