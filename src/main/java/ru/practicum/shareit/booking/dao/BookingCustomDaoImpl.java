package ru.practicum.shareit.booking.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dto.LastNextBookingDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookingCustomDaoImpl implements BookingCustomDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<LastNextBookingDto> findLastNextBooking(List<Long> items) {
        final String qs = "select a.itemId as itemId, " +
                "abl.id as lastBookingId, abl.booker_id as lastBookingBookerId, " +
                "abn.id as nextBookingId, abn.booker_id as nextBookingBookerId " +
                "from (" +
                "select it.id as itemId, " +
                "coalesce((select top 1 lb.id from bookings lb where lb.item_id = it.id and lb.end_date < now() " +
                "and lb.status = 'APPROVED' order by lb.end_date asc), 0) as lastBookingId, " +
                "coalesce((select top 1 nb.id from bookings nb where nb.item_id = it.id and nb.start_date > now() " +
                "and nb.status = 'APPROVED' order by nb.start_date desc), 0) as nextBookingId " +
                "from items as it where it.id in (:items)" +
                ") a " +
                "left join bookings abl on abl.id = a.lastBookingId " +
                "left join bookings abn on abn.id = a.nextBookingId";
        return jdbcTemplate.query(qs, new MapSqlParameterSource()
                        .addValue("items", items),
                (rs, rowNum) -> mapRowToLastNextBookingDto(rs)
        );

    }

    private LastNextBookingDto mapRowToLastNextBookingDto(ResultSet rs) throws SQLException {
        return LastNextBookingDto
                .builder()
                .itemId(rs.getLong("itemId"))
                .lastBookingId(rs.getLong("lastBookingId"))
                .lastBookingBookerId(rs.getLong("lastBookingBookerId"))
                .nextBookingId(rs.getLong("nextBookingId"))
                .nextBookingBookerId(rs.getLong("nextBookingBookerId"))
                .build();
    }
}
