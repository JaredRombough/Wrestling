package openwrestling.database;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class LocalDateConverter extends BidirectionalConverter<LocalDate, Date> {

    @Override
    public Date convertTo(LocalDate source, Type<Date> destinationType, MappingContext context) {
        if (LocalDate.MIN.equals(source)) {
            return new Date(Long.MIN_VALUE);
        }
        if (LocalDate.MAX.equals(source)) {
            return new Date(Long.MAX_VALUE);
        }
        return java.util.Date.from(source.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    @Override
    public LocalDate convertFrom(Date source, Type<LocalDate> destinationType, MappingContext context) {
        if (source.getTime() == Long.MIN_VALUE) {
            return LocalDate.MIN;
        }
        if (source.getTime() == Long.MAX_VALUE) {
            return LocalDate.MAX;
        }
        return source.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

}