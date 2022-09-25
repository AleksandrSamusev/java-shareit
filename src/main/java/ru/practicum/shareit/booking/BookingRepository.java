package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1")
    List<Booking> findBookingsByBookerId(Long id);


    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1")
    Page<Booking> findBookingsByBookerId(Long id, Pageable pageable);


    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND" +
            " b.start < CURRENT_TIMESTAMP AND b.end > CURRENT_TIMESTAMP")
    List<Booking> findBookingsByBookerIdWithCurrentStatus(Long id);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND" +
            " b.start < CURRENT_TIMESTAMP AND b.end < CURRENT_TIMESTAMP")
    List<Booking> findBookingsByBookerIdWithPastStatus(Long id);


    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND" +
            " b.start > CURRENT_TIMESTAMP ")
    List<Booking> findBookingsByBookerIdWithFutureStatus(Long id);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND  b.status = ?2")
    List<Booking> findBookingsByBookerIdWithWaitingOrRejectStatus(Long id, BookingStatus status);


    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.id IN (SELECT it FROM Item AS it WHERE it.owner.id = ?1) " +
            "AND b.status  = ?2 ")
    List<Booking> findAllOwnersBookingsWithStatus(Long id, BookingStatus status);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.id IN (SELECT it FROM Item AS it WHERE it.owner.id = ?1) ")
    List<Booking> findAllOwnersBookings(Long id);


    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.id IN (SELECT it FROM Item AS it WHERE it.owner.id = ?1) ")
    Page<Booking> findAllOwnersBookings(Long id, Pageable pageable);


    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.id IN (SELECT it FROM Item AS it WHERE it.owner.id = ?1) " +
            "AND b.start > CURRENT_TIMESTAMP ")
    List<Booking> findAllOwnersBookingsWithFutureStatus(Long id);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.id IN (SELECT it FROM Item AS it WHERE it.owner.id = ?1) " +
            "AND b.start < CURRENT_TIMESTAMP AND b.end > CURRENT_TIMESTAMP ")
    List<Booking> findAllOwnersBookingsWithCurrentStatus(Long id);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.id IN (SELECT it FROM Item AS it WHERE it.owner.id = ?1) " +
            "AND b.end < CURRENT_TIMESTAMP ")
    List<Booking> findAllOwnersBookingsWithPastStatus(Long id);


    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.id = ?1")
    List<Booking> findAllItemBookings(Long id);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.id = ?1 AND b.start < CURRENT_TIMESTAMP ")
    List<Booking> findAllItemBookingsPast(Long id);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.id = ?1 AND b.start > CURRENT_TIMESTAMP ")
    List<Booking> findAllItemBookingsFuture(Long id);
}

