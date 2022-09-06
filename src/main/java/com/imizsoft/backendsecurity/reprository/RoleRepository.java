package com.imizsoft.backendsecurity.reprository;

import com.imizsoft.backendsecurity.model.ERole;
import com.imizsoft.backendsecurity.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {

    Optional<Role> findByName(ERole name);

}
