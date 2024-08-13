package enums;

/**
 * The EnumOrderStatus enum represents the different statuses of an order in the restaurant.
 * The order progresses through the following stages:
 * - PENDING: The order has just been received from the user.
 * - IN_PROGRESS: The order is being processed and approved by a worker.
 * - READY: The order is ready for delivery or pickup.
 * - COMPLETED: The order has been delivered to or received by the user.
 */
public enum EnumOrderStatus {

    /**
     * The order has just been received from the user.
     */
    PENDING,

    /**
     * The order is being processed and approved by a worker.
     */
    IN_PROGRESS,

    /**
     * The order is ready for delivery or pickup.
     */
    READY,

    /**
     * The order has been delivered to or received by the user.
     */
    COMPLETED;
}
