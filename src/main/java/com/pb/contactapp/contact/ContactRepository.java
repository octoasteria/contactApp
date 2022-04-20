package com.pb.contactapp.contact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface ContactRepository extends JpaRepository<ContactEntity, Long> {

    List<ContactEntity> findByUserId(Long userId);
}
