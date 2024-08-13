package enums;

/**
 * Enumeration of server operations used in the BiteMe application.
 * This Enum defines various operations that can be performed on the server side.
 */
public enum EnumServerOperations {
    /** operation when client established connection to server*/
    CLIENT_CONDITION,

    /** Represents the operation to insert a new order to DB for specific branch. */
    INSERT_ORDER,

    /** login operation by given user name and password. */
    LOGIN,

    /** operation to add a new dish. */
    ADD_DISH,

    /** operation to retrieve pending orders by branch. */
    PENDING_ORDER,

    /** operation to delete a dish from branch menu. */
    DELETE_DISH,

    /** operation to update a dish. */
    UPDATE_DISH,

    /** operation to view the menu of specific branch. */
    VIEW_MENU,

    /** operation to create (activate) a new account. */
    CREATE_ACCOUNT,

    /** operation to retrieve an income report. */
    INCOME_REPORT,

    /** operation to retrieve a performance report. */
    PERFORMANCE_REPORT,

    /** operation to retrieve an orders report. */
    ORDERS_REPORT,

    /** logout operation disconnects the user, updating DB */
    LOG_OUT,

    /** operation to update the status of an order in DB. */
    UPDATE_ORDER_STATUS,

    /** operation to get the discount amount for given user from DB. */
    GET_DISCOUNT_AMOUNT,

    /** operation to set the discount amount after late order or used discount in new order*/
    SET_DISCOUNT_AMOUNT,

    /** operation to check user information before creating (activating) */
    CHECK_USER,

    /** operation to retrieve a quarterly report. */
    QUARTERLY_REPORT,

    /** operation to retrieve dishes for given order. */
    DISHES_IN_ORDER,

    /** operation to change the home branch for a user. */
    CHANGE_HOME_BRANCH,

    /** operation to retrieve the menu for updating by worker */
    MENU_FOR_UPDATE,

    /** operation to indicate the start of order creation process (for users that entered Create Order). */
    IN_ORDER_CREATION,

    /** operation to indicate the end of order creation (Finished or exited Create Order). */
    OUT_ORDER_CREATION,

    /** operation to retrieve a user's orders. */
    USERS_ORDERS,

    /** operation to check if an order received on time. */
    ORDER_ON_TIME,

    /** operation to indicate entering the pending orders page by management users to pop up window if new order received. */
    IN_PENDING_ORDERS,

    /** operation to indicate exiting the pending orders view page. */
    OUT_PENDING_ORDERS
}