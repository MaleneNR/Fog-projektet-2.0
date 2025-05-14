package app.entities;

import java.time.LocalDate;
import java.util.Objects;

public class Order {
    private int orderId;
    private String orderStatus;
    private int orderPrice;
    private boolean payed;
    private LocalDate date;
    private User user;
    private int length;
    private int height;
    private int width;

    public Order(int orderId, String orderStatus, int orderPrice, boolean payed, LocalDate date, User user, int length, int height, int width) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.orderPrice = orderPrice;
        this.payed = payed;
        this.date = date;
        this.user = user;
        this.length = length;
        this.height = height;
        this.width = width;
    }

    public Order(int orderId, String email, LocalDate date) {

    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(int orderPrice) {
        this.orderPrice = orderPrice;
    }

    public boolean isPayed() {
        return payed;
    }

    public void setPayed(boolean payed) {
        this.payed = payed;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", orderStatus='" + orderStatus + '\'' +
                ", orderPrice=" + orderPrice +
                ", payed=" + payed +
                ", date=" + date +
                ", user=" + user +
                ", length=" + length +
                ", height=" + height +
                ", width=" + width +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;

        return getOrderId() == order.getOrderId() && getOrderPrice() == order.getOrderPrice() && isPayed() == order.isPayed() && getLength() == order.getLength() && getHeight() == order.getHeight() && getWidth() == order.getWidth() && getOrderStatus().equals(order.getOrderStatus()) && getDate().equals(order.getDate()) && getUser().equals(order.getUser());
    }

    @Override
    public int hashCode() {
        int result = getOrderId();
        result = 31 * result + getOrderStatus().hashCode();
        result = 31 * result + getOrderPrice();
        result = 31 * result + Boolean.hashCode(isPayed());
        result = 31 * result + getDate().hashCode();
        result = 31 * result + getUser().hashCode();
        result = 31 * result + getLength();
        result = 31 * result + getHeight();
        result = 31 * result + getWidth();
        return result;
    }
}
