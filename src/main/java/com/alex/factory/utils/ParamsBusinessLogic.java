package com.alex.factory.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class ParamsBusinessLogic {
    @Value("${sam.app.logic.discount.default}")
    private int DISCOUNT_DEFAULT;
    @Value("${sam.app.logic.discount.low}")
    private int DISCOUNT_LOW;
    @Value("${sam.app.logic.discount.medium}")
    private int DISCOUNT_MEDIUM;
    @Value("${sam.app.logic.discount.vip}")
    private int DISCOUNT_VIP;
    @Value("${sam.app.logic.surcharge.default}")
    private int SURCHARGE_DEFAULT;

}
