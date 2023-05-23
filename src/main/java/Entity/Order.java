package Entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;


@Entity
public class Order {
	
	@Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
	
	private double total_price;
	private int runnerId;
	private int restaurantID;
        
    private OrderStatus order_status;
    
  
    @Lob
    private List<String> items;

    
   
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "runnerId", insertable = false, updatable = false)
    private Runner runner;

    
    @ManyToOne
    @JoinColumn(name = "restaurantId")
    private Restaurant restaurant;
    
    public Order() {} 
    
    
    public int getId() {
        return id;
    }   
    public void setId(int id) {
        this.id=id;
    }   
    public OrderStatus getOrderStatus() {
        return order_status;
    }
    public void setOrderStatus(OrderStatus order_status) {
        this.order_status = order_status;
    }
	public double getTotal_price() {
		return total_price;
	}
	public void setTotal_price(double total_price) {
		this.total_price = total_price;
	}
	public int getRunnerId() {
		return runnerId;
	}
	public void setRunnerId(int runnerId) {
		this.runnerId = runnerId;
	}
	public int getRestaurantID() {
		return restaurantID;
	}
	public void setRestaurantID(int restaurantID) {
		this.restaurantID = restaurantID;
	}
    public void setRunner(Runner runner) {
        this.runner = runner;
    }
	public Runner getRunner() {
		return runner;
	}
	
	public void setItems(List<String> items) {
		this.items = items;
	}
	public List<String> getItems() {
		return items;
	}

}
