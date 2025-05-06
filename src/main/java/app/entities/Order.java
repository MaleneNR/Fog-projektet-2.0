package app.entities;

import java.time.LocalDate;

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
}
