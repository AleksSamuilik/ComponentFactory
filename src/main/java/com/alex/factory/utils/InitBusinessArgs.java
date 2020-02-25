package com.alex.factory.utils;

import com.alex.factory.model.ProductDetails;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Log
@Component
public class InitBusinessArgs {

    public long countCost(final List<ProductDetails> detailsList, final int discount) {
        final AtomicLong count = new AtomicLong();
        detailsList.forEach(element -> count.addAndGet((long) Math.ceil(element.getSellCost() * element.getQuantity() * (100 - discount) / 100)));
        return count.get();
    }

    public LocalDate getCompletionDate(final LocalDate endDate) {
        return endDate.minusDays(4);
    }
}
