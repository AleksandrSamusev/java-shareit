package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

@Query("SELECT r FROM Request r WHERE r.requestor.id = ?1")
    List<Request> findRequestByRequestorId(Long id);
}
