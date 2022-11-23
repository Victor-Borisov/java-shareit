package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.StatusType;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingDao extends JpaRepository<Booking, Long>, BookingCustomDao {
    Optional<Booking> findTopByItemIdAndEndBeforeAndStatusInOrderByEndDesc(Long itemId,
                                                                                 LocalDateTime finishBefore,
                                                                                 List<StatusType> statuses);

    Optional<Booking> findTopByItemIdAndStartAfterAndStatusInOrderByStartAsc(Long itemId,
                                                                             LocalDateTime startAfter,
                                                                             List<StatusType> statuses);

    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long bookerId, Long itemId,
                                                                          StatusType status, LocalDateTime end);

    List<Booking> findAllByBookerId(Long bookerId, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long bookerId,
                                                             LocalDateTime start,
                                                             LocalDateTime end,
                                                             PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime end,
                                                PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId,
                                                 LocalDateTime start,
                                                 PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStatusEquals(Long bookerId,
                                                   StatusType status,
                                                   PageRequest pageRequest);

    List<Booking> findAllByItemOwnerId(Long ownerId, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(Long ownerId,
                                                                LocalDateTime start,
                                                                LocalDateTime end,
                                                                PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndEndBefore(Long ownerId,
                                                   LocalDateTime end,
                                                   PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartAfter(Long ownerId,
                                                    LocalDateTime start,
                                                    PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStatusEquals(Long ownerId,
                                                      StatusType status,
                                                      PageRequest pageRequest);
}
