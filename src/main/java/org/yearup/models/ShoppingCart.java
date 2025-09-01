package org.yearup.models;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {
    private Map<Integer, ShoppingCartItem> items = new HashMap<>();
    private BigDecimal total = BigDecimal.ZERO;

    public Map<Integer, ShoppingCartItem> getItems() {
        return items;
    }

    public void setItems(Map<Integer, ShoppingCartItem> items) {
        this.items = items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        if (items != null) {
            for (ShoppingCartItem item : items.values()) {
                total = total.add(item.getLineTotal());
            }
        }
        this.total = total;
    }
}
