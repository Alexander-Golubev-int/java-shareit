package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.startDate < current_timestamp() AND b.endDate > current_timestamp() AND b.booker.id = :bookerId")
    List<Booking> findCurrentBookingsByBooker(@Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.endDate < current_timestamp() AND b.booker.id = :bookerId")
    List<Booking> findPastBookingsByBooker(@Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.endDate < current_timestamp() AND b.booker.id = :bookerId AND b.item.id =" +
            " :itemId AND status = 'APPROVED'")
    List<Booking> findPastBookingsByBookerAndItem(@Param("bookerId") Long bookerId, @Param("itemId") Long itemId);

    @Query("SELECT b FROM Booking b WHERE b.startDate > current_timestamp() AND b.booker.id = :bookerId")
    List<Booking> findFutureBookingsByBooker(@Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.status = 'WAITING' AND b.booker.id = :bookerId")
    List<Booking> findWaitingBookingsByBooker(@Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.status = 'REJECTED' AND b.booker.id = :bookerId")
    List<Booking> findRejectedBookingsByBooker(@Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId ORDER BY b.startDate DESC")
    List<Booking> findByBookerIdOrderByStartDesc(@Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId")
    List<Booking> findAllByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId AND b.endDate < CURRENT_TIMESTAMP " +
            "ORDER BY b.endDate DESC")
    Optional<Booking> findLastCompletedBooking(@Param("itemId") Long itemId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId AND b.startDate > CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate ASC")
    Optional<Booking> findNearestFutureBooking(@Param("itemId") Long itemId);

}
