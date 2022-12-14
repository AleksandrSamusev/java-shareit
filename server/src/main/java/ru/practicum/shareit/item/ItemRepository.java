package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE i.owner.id = ?1")
    List<Item> findAllItemsByOwner(Long id);

    @Query("SELECT i FROM Item i WHERE i.requestId = ?1 ")
    List<Item> findAllByRequestId(Long id);

}
