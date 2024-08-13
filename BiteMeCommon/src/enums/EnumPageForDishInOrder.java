package enums;
/**
 * Enum representing the UI page where a list of dishes in an order should be displayed.
 * This enum is used to distinguish between different UI windows within the client application
 * where a list of dishes in an order is needed to be sent.
 */
public enum EnumPageForDishInOrder {
    
    /**
     * Refers to the WorkerPendingOrders UI page.
     * This is where a worker can view the dishes within a customer's order.
     */
    WORKER,

    /**
     * Refers to the MyOrders UI page.
     * This is where a customer can view the selected dishes of their order.
     */
    CUSTOMER;
}

