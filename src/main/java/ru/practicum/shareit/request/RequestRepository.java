package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT r FROM Request r WHERE r.requestor.id = ?1")
    List<Request> findRequestByRequestorId(Long id);

    @Query("SELECT r FROM Request r WHERE r.id = ?1")
    Request findRequestById(Long id);

    @Query("SELECT r FROM Request r WHERE r.id <> ?1")
    List<Request> findOthersRequestsWithPagination(Long id, Pageable pageable);
}
