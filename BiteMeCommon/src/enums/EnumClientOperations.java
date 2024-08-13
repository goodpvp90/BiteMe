package enums;
/**
 * Enum representing the various operations that a client can perform within the application.
 * These operations are used to communicate specific requests and actions between the client and the server.
 */
public enum EnumClientOperations{
	/**
     * Get all orders that a user has created.
     */
	USERS_ORDERS,
	/**
     * Start the login process for a user.
     */
	USER,
	/**
     * Retrieve a list of all pending orders for a specific branch.
     */
	PENDING_ORDER,
	/**
     * Send SMS-style notifications to a specific customer when their order status changes.
     */
	NOTIFICATION,
	/**
     * Indicate whether a dish was successfully deleted from the server.
     */
	DELETE_DISH,
	/**
     * Retrieve the menu associated with a specific branch.
     */
	VIEW_MENU,
	/**
     * Indicate whether a dish was successfully updated on the server.
     */
	UPDATE_DISH,
	/**
     * Provide a user object if the account creation was successful; otherwise, return a boolean value indicating failure.
     */
	CREATED_ACCOUNT,
	/**
     * Indicate whether a dish was successfully added to the server.
     */
	ADD_DISH,
	/**
     * Return an error if a requested report was not found in the database.
     */
	REPORT_ERROR,
	/**
     * Return the monthly income report.
     */
	INCOME_REPORT,
	/**
     * Return the monthly orders report.
     */
	ORDERS_REPORT,
	/**
     * Return the monthly performance report.
     */
	PERFORMANCE_REPORT,
	/**
     * Return the quarterly report.
     */
	QUARTERLY_REPORT,
	/**
     * Provide the compensation amount for a specific user if they are eligible.
     */
	GET_DISCOUNT_AMOUNT,
	/**
     * Return a user object if a username was found; otherwise, return a boolean value indicating failure.
     */
	CHECK_USER,
	/**
     * Provide a list of dishes related to orders of a specific branch.
     */
	DISHES_IN_ORDER,
	/**
     * Return a list of all dish items in a specific menu, used for updating or deleting dishes.
     */
	MENU_FOR_UPDATE,

    /**
     * Indicate whether the home branch was successfully changed.
     */
	CHANGE_HOME_BRANCH,
	/**
     * Indicate that the server has disconnected, resulting in the termination of the client object for each connected user.
     */
	SERVER_DISCONNECTED,
	/**
     * Return a boolean value indicating whether an order was delivered on time or was late.
     */
	ORDER_ON_TIME,
	/**
     * Open a pop-up window for a customer in the process of creating an order when a menu change occurs.
     */
	INTERRUPT_ORDER_CREATION,
	/**
     * Open a pop-up window for a worker viewing pending orders of their branch when a new order is added by a customer.
     */
	INTERRUPT_PENDING_ORDERS;
}
