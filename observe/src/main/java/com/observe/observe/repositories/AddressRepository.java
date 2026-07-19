package com.observe.observe.repositories;

import com.observe.observe.models.Address;
import com.observe.observe.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByUser(User user);
    Optional<Address> findByUserAndIsDefault(User user, boolean isDefault);
    
}