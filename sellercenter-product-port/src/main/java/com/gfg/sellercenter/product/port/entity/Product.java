package com.gfg.sellercenter.product.port.entity;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class Product {
    @NotNull
    private final Integer id;

    private final UUID uuid;

    private final String sourceId;

    private final String sku;

    @NotNull
    private final Price price;

    @NotNull
    @NotEmpty
    private final String sellerSku;

    @NotNull
    @NotEmpty
    private final String name;

    private final Double volumetricWeight;

    public boolean hasSpecialPrice(ZonedDateTime timePoint) {
        return price.getSpecialPrice() != null
                && price.getSpecialPrice().getAmount() != null
                && price.getSpecialPrice().rangeCovers(timePoint);
    }

    public Money getCurrentPrice() {
        ZonedDateTime now = ZonedDateTime.now();

        if (hasSpecialPrice(now)) {
            try {
                return getSpecialPrice(now);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(
                        "Product price changed during runtime, which lead to invalid state",
                        e
                );
            }
        }

        return price.getAmount();
    }

    private Money getSpecialPrice(ZonedDateTime timePoint) throws IllegalAccessException {
        if (!hasSpecialPrice(timePoint)) {
            throw new IllegalAccessException(
                    "getSpecialPrice method call should be wrapped by hasSpecialPrice if condition"
            );
        }

        if (price.getSpecialPrice().rangeCovers(ZonedDateTime.now())) {
            return new Money(
                    price.getSpecialPrice().getAmount(),
                    price.getAmount().getCurrency()
            );
        }

        return price.getAmount();
    }
}
