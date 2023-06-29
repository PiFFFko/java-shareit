package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b "
            + "from Booking as b "
            + "join fetch b.booker as booker "
            + "join fetch b.item as i "
            + "where b.booker.id = ?1 "
            + "order by b.end desc")
    List<Booking> findAllByBookerId(Long userId, Pageable pageable);

    @Query("select b "
            + "from Booking as b "
            + "join fetch b.booker as booker "
            + "join fetch b.item as i "
            + "where b.booker.id = ?1 and b.start > current_timestamp "
            + "order by b.end desc")
    List<Booking> findAllFutureByUserIdAndSortByDesc(Long bookerId, Pageable pageable);

    @Query("select b "
            + "from Booking as b "
            + "join fetch b.booker as booker "
            + "join fetch b.item as i "
            + "where b.booker.id = ?1 and b.end < current_timestamp "
            + "order by b.end desc")
    List<Booking> findAllPastByUserIdAndSortByDesc(Long bookerId, Pageable pageable);

    @Query("select b "
            + "from Booking as b "
            + "join fetch b.booker as booker "
            + "join fetch b.item as i "
            + "where b.booker.id = ?1 and b.status like 'WAITING' "
            + "order by b.end desc")
    List<Booking> findAllWaitingByUserIdAndSortByDesc(Long bookerId, Pageable pageable);

    @Query("select b "
            + "from Booking as b "
            + "join fetch b.booker as booker "
            + "join fetch b.item as i "
            + "where b.booker.id = ?1 and b.status like 'REJECTED' "
            + "order by b.end desc")
    List<Booking> findAllRejectedByUserIdAndSortByDesc(Long bookerId, Pageable pageable);

    @Query("select b "
            + "from Booking as b "
            + "join fetch b.booker as booker "
            + "join fetch b.item as i "
            + "where b.booker.id = ?1 and b.start <= current_timestamp and b.end >= current_timestamp "
            + "order by b.end desc")
    List<Booking> findAllCurrentByUserIdAndSortByDesc(Long bookerId, Pageable pageable);

    @Query("select b "
            + "from Booking as b "
            + "join fetch b.booker as booker "
            + "join fetch b.item as i "
            + "where i.owner.id = ?1 "
            + "order by b.end desc")
    List<Booking> findAllByOwnerId(Long userId, Pageable pageable);

    @Query("select b "
            + "from Booking as b "
            + "join fetch b.booker as booker "
            + "join fetch b.item as i "
            + "where i.owner.id = ?1 and b.start > current_timestamp "
            + "order by b.end desc")
    List<Booking> findAllFutureByOwnerIdAndSortByDesc(Long bookerId, Pageable pageable);

    @Query("select b "
            + "from Booking as b "
            + "join fetch b.booker as booker "
            + "join fetch b.item as i "
            + "where i.owner.id = ?1 and b.end < current_timestamp "
            + "order by b.end desc")
    List<Booking> findAllPastByOwnerIdAndSortByDesc(Long bookerId, Pageable pageable);

    @Query("select b "
            + "from Booking as b "
            + "join fetch b.booker as booker "
            + "join fetch b.item as i "
            + "where i.owner.id = ?1 and b.status like 'WAITING' "
            + "order by b.end desc")
    List<Booking> findAllWaitingByOwnerIdAndSortByDesc(Long bookerId, Pageable pageable);

    @Query("select b "
            + "from Booking as b "
            + "join fetch b.booker as book "
            + "join fetch b.item as i "
            + "where i.owner.id = ?1 and b.status like 'REJECTED' ")
    List<Booking> findAllRejectedByOwnerIdAndSortByDesc(Long bookerId, Pageable pageable);

    @Query("select b "
            + "from Booking as b "
            + "join fetch b.booker as booker "
            + "join fetch b.item as i "
            + "where i.owner.id = ?1 and b.start <= current_timestamp and b.end >= current_timestamp "
            + "order by b.end desc")
    List<Booking> findAllCurrentByOwnerIdAndSortByDesc(Long bookerId, Pageable pageable);

    @Query("select b "
            + "from Booking as b "
            + "join fetch b.item as i "
            + "where i.id = ?1 "
            + "and b.start < current_timestamp "
            + "and b.status not like 'REJECTED'"
            + "order by b.start desc")
    List<Booking> findLastBookingForItem_Id(Long itemId);

    @Query("select b "
            + "from Booking as b "
            + "join fetch b.item as i "
            + "where i.id = ?1 "
            + "and b.start > current_timestamp "
            + "and b.status not like 'REJECTED'"
            + "order by b.start asc")
    List<Booking> findNextBookingForItem_Id(Long itemId);
}
