package Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import Entity.Restaurant;

@Entity
public class Meal {
	
	
	
    int mealId;
    String name;
    double price;
    int restaurantID;
    
    
    @ManyToOne
	@JoinColumn(name="restaurantID")
	private Restaurant restaurant;
    
    public Meal() {}

    public Meal(int mealId,String name,double price,int restaurantID)
    {
        this.mealId=mealId;
        this.name = name;
        this.price=price;
        this.restaurantID=restaurantID;
    }
    
    
    @Id
	@NotNull
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    public int getId() {
        return mealId;
    }
    public void setId(int mealId) {
        this.mealId=mealId;
    }
    public void setName(String name) {
    	this.name=name;
    }
    public String getName() {
        return name;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price=price;
    }
    public void setRestaurantID(int restaurantID) {
        this.restaurantID=restaurantID;
    }
    public int getRestaurantID() {
        return restaurantID;
    }
    public Restaurant getRestaurant() {
        return restaurant;
    }
    /*
    public Restaurant getRestaurantobj() {
        Restaurant restaurant = new Restaurant();
        return restaurant;
    }*/
	
	
	
}