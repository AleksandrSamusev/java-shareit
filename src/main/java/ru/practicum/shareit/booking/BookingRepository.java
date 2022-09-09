package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends
        JpaRepository<Booking, Long>, BookingRepositoryCustom {

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1")
    List<Booking> findBookingsByBookerId(Long id);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND" +
            " b.start < CURRENT_TIMESTAMP AND b.end > CURRENT_TIMESTAMP")
    List<Booking> findBookingsByBookerIdWithCurrentStatus(Long id);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND" +
            " b.start < CURRENT_TIMESTAMP AND b.end < CURRENT_TIMESTAMP")
    List<Booking> findBookingsByBookerIdWithPastStatus(Long id);


    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND" +
            " b.start > CURRENT_TIMESTAMP AND b.end > CURRENT_TIMESTAMP")
    List<Booking> findBookingsByBookerIdWithFutureStatus(Long id);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND  b.status = ?2")
    List<Booking> findBookingsByBookerIdWithWaitingOrRejectStatus(Long id, BookingStatus status);
}

