package controllers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import containers.ListContainer;
import enums.EnumClientOperations;
import ocsf.server.ConnectionToClient;
import reports.IncomeReport;
import reports.OrdersReport;
import reports.PerformanceReport;
import reports.QuarterlyReport;
import server.Server;

/**
 * The ReportController class is responsible for scheduling and generating monthly reports
 * for orders, income, and performance. It uses a ScheduledExecutorService to run the tasks
 * at the start of each month.
 */
public class ReportController {
    /**
     * The server instance.
     */
    private Server server;
    
    /**
     * The database controller instance.
     */
    private DBController dbController;
    
    /**
     * The scheduled executor service for scheduling tasks.
     */
    private ScheduledExecutorService scheduler;
    

    /**
     * Constructs a ReportController for the given server.
     *
     * @param server the server instance to associate with this ReportController
     */
    public ReportController(Server server, DBController dbController) {
        this.server = server;
        this.dbController = dbController;
        scheduler = Executors.newScheduledThreadPool(1);
        scheduleNextExecution(scheduler);
    }

    /**
     * Schedules the next execution of the report generation task to run at the start of the next month.
     *
     * @param scheduler the ScheduledExecutorService to use for scheduling the task
     */
    private void scheduleNextExecution(ScheduledExecutorService scheduler) {
        LocalDateTime now = LocalDateTime.now();
        //for testing do not delete
        //LocalDateTime nextExecutionTime = now.plusMinutes(1);
        LocalDateTime nextExecutionTime = now.plusMonths(1);
        Duration delay = Duration.between(now, nextExecutionTime);

        scheduler.schedule(() -> {
            generateReports();
            scheduleNextExecution(scheduler); // Reschedule after execution
        }, delay.getSeconds(), TimeUnit.SECONDS);
    }

    /**
     * Generates all the reports by calling the respective methods for orders, income, and performance reports.
     */
    private void generateReports() {
        generateOrdersReport();
        generateIncomeReport();
        generatePerformanceReport();
    }

    /**
     * Calls the dbController's method to generate the orders report.
     */
    private void generateOrdersReport() {
        dbController.generateOrdersReport();
    }

    /**
     * Calls the dbController's method to generate the income report.
     */
    private void generateIncomeReport() {
        dbController.generateIncomeReport();
    }

    /**
     * Calls the dbController's method to generate the performance report.
     */
    private void generatePerformanceReport() {
        dbController.generatePerformanceReport();
    }
    
    /**
     * Retrieves the income report from the database for the specified report details and sends the result to the client.
     * The result can be a report object, an error message if no report exists, or an SQL error message.
     * 
     * @param incomeReport the IncomeReport object containing the details of the report to retrieve
     * @param client the ConnectionToClient object representing the client requesting the report
     */
    public void getIncomeReport(IncomeReport incomeReport, ConnectionToClient client) {
    	Object result = dbController.getIncomeReport(incomeReport);
        manageDBReturn(result, client, "INCOME_REPORT");
    }
    
    
    
    /**
     * Retrieves the orders report based on the given OrdersReport and sends it to the specified client.
     * 
     * @param ordersReport the OrdersReport object containing the details of the report to retrieve
     * @param client the to which the report will be sent
     */
    public void getOrdersReport(OrdersReport ordersReport, ConnectionToClient client) {
        Object result = dbController.getOrdersReport(ordersReport);
        manageDBReturn(result, client, "ORDERS_REPORT");
    }

    /**
     * Retrieves the performance report based on the given PerformanceReport and sends it to the specified client.
     * 
     * @param performanceReport the PerformanceReport object containing the details of the report to retrieve
     * @param client the to which the report will be sent
     */
    public void getPerformanceReport(PerformanceReport performanceReport, ConnectionToClient client) {
        Object result = dbController.getPerformanceReport(performanceReport);
        manageDBReturn(result, client, "PERFORMANCE_REPORT");
    }

    /**
     * Manages the database return by checking the type of result and sending appropriate messages to the client.
     * 
     * @param result the result returned from the database operation
     * @param client the to which the response should be sent
     * @param reportType the type of report, used to determine the appropriate message to send
     */
    private void manageDBReturn(Object result, ConnectionToClient client, String reportType) {
        if (result instanceof String) {
            if ("no such report exists".equals(result)) 
                server.sendMessageToClient(EnumClientOperations.REPORT_ERROR, client, result);
        } else {
            server.sendMessageToClient(EnumClientOperations.valueOf(reportType), client, result);
        }
    }
    
    /**
     * Retrieves or creates a quarterly report for the given {@link QuarterlyReport} object and sends it to the specified client.
     * 
     * This method first attempts to retrieve an existing quarterly report from the database. If the report does not exist, 
     * it attempts to create a new report using the provided {@code QuarterlyReport} object. After creating the report, 
     * it retrieves the newly created report and sends it to the client. If there are errors during these operations, 
     * appropriate error messages are sent to the client.
     * 
     * @param qreport the {@link QuarterlyReport} object containing the details of the report to be retrieved or created.
     * @param client the {@link ConnectionToClient} object representing the client to whom the report or error message is sent.
     */
    
    public void getQuarterlyReport(QuarterlyReport qreport, ConnectionToClient client) {
    	Object getResult = dbController.getQuarterlyReport(qreport);
    	if (getResult instanceof QuarterlyReport) {
    		 List<Double> incomes = dbController.getIncomeListForQuarterly(qreport);
			 //encapsulate the list to avoid suppress warnings
    		 ListContainer incomeContainer = new ListContainer();
    		 incomeContainer.setListDouble(incomes);
    		 server.sendMessageToClient(EnumClientOperations.QUARTERLY_REPORT, client, new Object[] {(QuarterlyReport)getResult, incomeContainer});
    	}else {
    		boolean createResult = dbController.createQuarterlyReport(qreport);
    		if (createResult) {
    			Object newGetResult = dbController.getQuarterlyReport(qreport);
    			if (newGetResult instanceof QuarterlyReport) {
    	    		List<Double> incomes = dbController.getIncomeListForQuarterly(qreport);
    			    //encapsulate the list to avoid suppress warnings
    	    		ListContainer incomeContainer = new ListContainer();
    	    		incomeContainer.setListDouble(incomes);
    	    		server.sendMessageToClient(EnumClientOperations.QUARTERLY_REPORT, client, new Object[] {(QuarterlyReport)newGetResult, incomeContainer});
    			}
    		}
    	}	
    }
    
    /**
     * Shuts down the scheduler to stop any scheduled tasks.
     * This method allows the scheduler to complete any ongoing tasks within a 5-second timeout.
     * If the scheduler does not terminate within the timeout, it will be forcefully shut down.
     * If the scheduler still does not terminate, an error message is printed.
     * 
     * If the current thread is interrupted while waiting for termination, the scheduler is
     * forcefully shut down immediately and the interrupt status of the thread is restored.
     */
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException ie) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }


}
