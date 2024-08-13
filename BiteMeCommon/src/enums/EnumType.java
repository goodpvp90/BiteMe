package enums;

/**
 * The EnumType enum represents the different user types in the system.
 */

public enum EnumType {

    /**
     * The branch manager of one of the restaurants.
     */
    BRANCH_MANAGER,

    /**
     * The CEO of the company.
     */
    CEO,

    /**
     * A worker in one of the restaurants.
     */
    WORKER,

    /**
     * A worker with the qualification to change the restaurant menu.
     */
    QUALIFIED_WORKER,

    /**
     * A customer of the restaurant.
     */
    CUSTOMER,

    /**
     * An unregistered customer who is waiting for branch manager confirmation.
     */
    UN_CUSTOMER,

    /**
     * A customer type with no special benefits.
     */
    REGULAR,

    /**
     * A customer type with the benefit of doing shared delivery.
     */
    BUSINESS,

    /**
     * A customer type with a saved credit card.
     */
    PRIVATE;
}
