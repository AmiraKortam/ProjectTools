package Service;

import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
//import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Entity.Meal;
import Entity.Order;
import Entity.OrderStatus;
import Entity.Restaurant;
import Entity.Runner;


/*
enum OrderStatus {
	PREPARING,
    DELIVERED,
    CANCELED

}
*/


@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"RestaurantOwner,Customer,Runner"})
@Path("/service")
public class MyService2 {

	
	
	//class User
	private int id;
	private String name;
	private String role;
	
	
	@PersistenceContext(unitName="task1")
	private EntityManager em;
	
	////////////////////////////////////////////////////////////
	
	@POST
	@Path("login")
	public boolean loginCustomer(String username, String password) {
	    String customerUsername = "customer1";
	    String customerPassword = "password1";

	    if (username.equals(customerUsername) && password.equals(customerPassword)) {
	        return true; 
	    } else {
	        return false;
	    }
	}
	public boolean loginRestaurantOwner(String username, String password) {
	    String ownerUsername = "owner1";
	    String ownerPassword = "password2";

	    if (username.equals(ownerUsername) && password.equals(ownerPassword)) {
	        return true;
	    } else {
	        return false;
	    }
	}
	public boolean loginRunner(String username, String password) {
		
	    String runnerUsername = "runner1";
	    String runnerPassword = "password3";

	    if (username.equals(runnerUsername) && password.equals(runnerPassword)) {
	        return true;
	    } else {
	        return false; 
	    }
	}

	
	//////////////////////////////////////////////////////////

	@RolesAllowed("RestaurantOwner")
	@POST
	@Path("CreateMenu")
	public void CreateRestaurantMenu(Restaurant restaurant, Set<Meal> meals) {
		
	        try {            
	            em.merge(restaurant);
	            for (Meal meal : meals) {
	                em.persist(meal);
	            }

	        } catch (Exception e) {
	        	throw new EJBException(e);
	           
	        }
	}
	
	////////////////////////////////////////////////
	@RolesAllowed("RestaurantOwner")
	@PUT
	@Path("editMenu")
	public void editRestaurantMenu(int restaurantId, int mealId, String newMealName) {
		
	    try {
	        //em.getTransaction().begin();
	        Restaurant restaurant = em.find(Restaurant.class, restaurantId);
	        if (restaurant != null)
	        {
	        	 for (Meal meal : restaurant.getMeals()) {
	                 if (meal.getId() == mealId) {
	                     meal.setName(newMealName);
	                     em.merge(meal);
	                     //em.getTransaction().commit();
	                     System.out.println("Meal updated successfully!");
	                     return;
	                 }
	        	 }
	        }


	    } catch (Exception e) {
	    	throw new EJBException(e);
	    }
	}
//////////////////////////////////////////////////////////////
	
	
	@RolesAllowed("RestaurantOwner")
	@GET
	@Path("/get-byID/{id}")
	public Response getRestaurantById(@PathParam("id") int id) {
	    Restaurant restaurant = em.find(Restaurant.class, id);
	    if (restaurant != null) {
	        return Response.ok(restaurant).build();
	    } else {
	        return Response.status(Response.Status.NOT_FOUND).entity("Restaurant not found").build();
	    }
	}
	
	/////////////////////////////////////////////////////////////
	
	@RolesAllowed("RestaurantOwner")
	@GET
	@Path("create-report/{restaurantId}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response createRestaurantReport(@PathParam("restaurantId") int restaurantId) {
		TypedQuery<Double> earningsQuery = em.createQuery("SELECT SUM(o.total_price) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.order_status = :orderStatus", Double.class);
		earningsQuery.setParameter("restaurantId", restaurantId);
		earningsQuery.setParameter("orderStatus", OrderStatus.DELIVERED);

		TypedQuery<Long> completedOrdersQuery = em.createQuery("SELECT COUNT(o) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.order_status = :orderStatus", Long.class);
		completedOrdersQuery.setParameter("restaurantId", restaurantId);
		completedOrdersQuery.setParameter("orderStatus", OrderStatus.DELIVERED);

		TypedQuery<Long> canceledOrdersQuery = em.createQuery("SELECT COUNT(o) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.order_status = :orderStatus", Long.class);
		canceledOrdersQuery.setParameter("restaurantId", restaurantId);
		canceledOrdersQuery.setParameter("orderStatus", OrderStatus.CANCELED);


	    try {
	        double earnings = earningsQuery.getSingleResult();
	        long completedOrdersCount = completedOrdersQuery.getSingleResult();
	        long canceledOrdersCount = canceledOrdersQuery.getSingleResult();
	        String reportMessage = "Restaurant Report:"
	                + "\nRestaurant ID: " + restaurantId
	                + "\nTotal Earnings: $" + earnings
	                + "\nNumber of Completed Orders: " + completedOrdersCount
	                + "\nNumber of Canceled Orders: " + canceledOrdersCount;

	        return Response.ok(reportMessage).build();
	    } catch (Exception e) {
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred while generating the restaurant reportt.").build();
	    }
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	
	//@RolesAllowed("Customer")

	
	
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	
	@RolesAllowed("Customer")
	@POST
    @Path("/select_runner")
    public Response selectRandomAvailableRunner(Order order) {
        List<Runner> availableRunners = getAvailableRunners();

        if (availableRunners.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).entity("No available runners at the moment").build();
        }
        Runner randomRunner = getRandomRunner(availableRunners);

        order.setRunner(randomRunner);
        order.setRunnerId(randomRunner.getId());

        randomRunner.setStatus(Runner.Status.BUSY);

        try {
            em.merge(order);
            em.merge(randomRunner);
            return Response.ok("Order created successfully. Runner assigned: " + randomRunner.getName()).build();
        } catch (Exception e) {
            throw new EJBException(e);
        }
    }

    private List<Runner> getAvailableRunners() {
        TypedQuery<Runner> query = em.createQuery("SELECT r FROM Runner r WHERE r.status = :status", Runner.class);
        query.setParameter("status", Runner.Status.AVAILABLE);
        return query.getResultList();
    }

    private Runner getRandomRunner(List<Runner> availableRunners) {
        Random random = new Random();
        int index = random.nextInt(availableRunners.size());
        return availableRunners.get(index);
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////
	
	@RolesAllowed("Customer")
    @PUT
    @Path("/{orderId}")
	    public void editOrder(@PathParam("orderId") int orderId, List<String> newItems) {
		Order order = em.find(Order.class, orderId);
		if (order != null && order.getOrderStatus() != OrderStatus.CANCELED) 
        {
			if (order.getOrderStatus() == OrderStatus.PREPARING) 
        	{
                order.setItems(newItems);
                em.persist(order);
            }
			else
        	{
        		throw new WebApplicationException("The order is not in the preparing state and cannot be edited.",Response.Status.BAD_REQUEST);
            }
			
        }

	}
    
    
    
    
    //////////////////////////////////////////////////////////////////////////////////////////
	
	@RolesAllowed("Customer")
	@GET
	@Path("list")
	public List<String> getAllRestaurant() {
		
		TypedQuery<String> query = em.createQuery("SELECT name FROM Restaurant", String.class);
		List<String> r = query.getResultList();
		return r;
	} 

	///////////////////////////////////////////////////////////////////////////////////////////
	
	
	@RolesAllowed("Runner")
	@PUT
    //@Path("/markOrderAsDelivered/{orderId}")
	@Path("{orderId}")
    @Consumes(MediaType.APPLICATION_JSON)
    //@Transactional
    public Response markOrderAsDelivered(@PathParam("orderId") int orderId) {
        Order order = em.find(Order.class, orderId);
        if (order != null && order.getOrderStatus() != OrderStatus.DELIVERED) {
            order.setOrderStatus(OrderStatus.DELIVERED);
            Runner runner = order.getRunner();
            if (runner != null) {
                runner.setStatus(Runner.Status.AVAILABLE);
                em.persist(runner);
            }
            em.persist(order);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	
	@RolesAllowed("Runner")
	@GET
    @Path("/{runnerId}/completed-trips")
    public Response getNumberOfTripsCompletedByRunner(@PathParam("runnerId") int runnerId) {
		String queryString = "SELECT COUNT(o) FROM Order o WHERE o.runner.id = :runnerId " +
                "AND o.orderStatus = :completedStatus AND o.orderStatus != :canceledStatus";
		
		TypedQuery<Long> query = em.createQuery(queryString, Long.class)
                .setParameter("runnerId", runnerId)
                .setParameter("completedStatus", OrderStatus.DELIVERED)
                .setParameter("canceledStatus", OrderStatus.CANCELED);

		Long result = query.getSingleResult();
		int numberOfTripsCompleted = (result != null) ? result.intValue() : 0;
		return Response.ok(numberOfTripsCompleted).build();
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////
	
	//function just to test the GET
	@POST
	@Path("set")
	public String setRestaurant(Restaurant r) {
		em.persist(r);
		return "I am Working";
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	

	
	
	/*
	@GET
    @Path("{restaurantId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response generateRestaurantReport(@PathParam("restaurantId") int restaurantId) {
        String earningsQuery = "SELECT SUM(o.total_price) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.order_status = 'DELIVERED'";
        String completedOrdersQuery = "SELECT COUNT(o) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.order_status = 'DELIVERED'";
        String canceledOrdersQuery = "SELECT COUNT(o) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.order_status = 'CANCELED'";

        try {
            Query earningsQueryObj = em.createQuery(earningsQuery);
            earningsQueryObj.setParameter("restaurantId", restaurantId);
            double earnings = (Double) earningsQueryObj.getSingleResult();
            Query completedOrdersQueryObj = em.createQuery(completedOrdersQuery);
            completedOrdersQueryObj.setParameter("restaurantId", restaurantId);
            long completedOrdersCount = (Long) completedOrdersQueryObj.getSingleResult();
            Query canceledOrdersQueryObj = em.createQuery(canceledOrdersQuery);
            canceledOrdersQueryObj.setParameter("restaurantId", restaurantId);
            long canceledOrdersCount = (Long) canceledOrdersQueryObj.getSingleResult();
            String reportMessage = "Restaurant Report:"
                    + "\nRestaurant ID: " + restaurantId
                    + "\nTotal Earnings: $" + earnings
                    + "\nNumber of Completed Orders: " + completedOrdersCount
                    + "\nNumber of Canceled Orders: " + canceledOrdersCount;

            return Response.ok(reportMessage).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred while generating the restaurant report by manar.").build();
        }
    }
	*/
	
	/*
	@GET
    @Path("/{restaurantId}")
    public Response generateRestaurantReport(@PathParam("restaurantId") int restaurantId) {
        // Query to calculate the total earnings
        String earningsQuery = "SELECT SUM(o.total_price) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.orderStatus = 'DELIVERED'";
        
        // Query to count the number of completed orders
        String completedOrdersQuery = "SELECT COUNT(o) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.orderStatus = 'DELIVERED'";
        
        // Query to count the number of canceled orders
        String canceledOrdersQuery = "SELECT COUNT(o) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.orderStatus = 'CANCELED'";

        try {
            // Calculate earnings
            Query earningsQueryObj = em.createQuery(earningsQuery);
            earningsQueryObj.setParameter("restaurantId", restaurantId);
            double earnings = (Double) earningsQueryObj.getSingleResult();

            // Count completed orders
            Query completedOrdersQueryObj = em.createQuery(completedOrdersQuery);
            completedOrdersQueryObj.setParameter("restaurantId", restaurantId);
            long completedOrdersCount = (Long) completedOrdersQueryObj.getSingleResult();

            // Count canceled orders
            Query canceledOrdersQueryObj = em.createQuery(canceledOrdersQuery);
            canceledOrdersQueryObj.setParameter("restaurantId", restaurantId);
            long canceledOrdersCount = (Long) canceledOrdersQueryObj.getSingleResult();

            // Construct the report message
            String reportMessage = "Restaurant Report:"
                    + "\nRestaurant ID: " + restaurantId
                    + "\nTotal Earnings: $" + earnings
                    + "\nNumber of Completed Orders: " + completedOrdersCount
                    + "\nNumber of Canceled Orders: " + canceledOrdersCount;

            return Response.ok(reportMessage).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred while generating the restaurant report.").build();
        }
	}*/
	
	
	
	
	/*
	@POST
	@Path("create")
	public void CreateRestaurantMenu(Restaurant restaurant, List<Meal> meals) {
		
        try {            
            em.merge(restaurant);
            for (Meal meal : meals) {
                em.persist(meal);
            }
        } catch (Exception e) {
        	throw new EJBException(e);
        	}
	}
	

	@GET
	@Path("meal")
	public List<String> getAllMeals() {
		
		TypedQuery<String> query = em.createQuery("SELECT meals FROM Restaurant", String.class);
		List<String> r = query.getResultList();
		return r;
	}
	*/ 
    
    
    
/*
	
	@PUT
    @Path("/{orderId}")
	    public void editOrder(@PathParam("orderId") int orderId, List<Item> newItems) 
	{
        Order order = em.find(Order.class, orderId);
        
        if (order != null && order.getOrderStatus() != OrderStatus.CANCELED) 
        {
        	if (order.getOrderStatus() == OrderStatus.PREPARING) 
        	{
                order.setItems(newItems);
                em.persist(order);
            }
        	else
        	{
        		throw new WebApplicationException("The order is not in the preparing state and cannot be edited.",Response.Status.BAD_REQUEST);
            }
        }
        else 
        {
            throw new WebApplicationException("Invalid order or the order is canceled.",Response.Status.BAD_REQUEST);
        }
    }
	*/
}
