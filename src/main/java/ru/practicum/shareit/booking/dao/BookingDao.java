package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.StatusType;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingDao extends JpaRepository<Booking, Long> {
    List<Booking> findAllByItemIdOrderByStartAsc(Long itemId);

    List<Booking> findAllByItemIdOrderByStartDesc(Long itemId);

    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long bookerId, Long itemId,
                                                                          StatusType status, LocalDateTime end);

    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByBookerIdAndStatusEquals(Long bookerId, StatusType status, Sort sort);

    List<Booking> findAllByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatusEquals(Long ownerId, StatusType status, Sort sort);
}
