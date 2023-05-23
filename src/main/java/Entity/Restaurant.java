package Entity;


import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import Entity.Meal;

@Stateless
@Table(schema="public") 
@Entity
public class Restaurant {
	
	@Id
	@NotNull
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String name;
	private int ownerID;
  
   
	@OneToMany(mappedBy = "restaurant", fetch = FetchType.EAGER)
    private Set<Meal> meals;
	
	
	@OneToMany(mappedBy = "restaurant", fetch = FetchType.EAGER)
    private Set<Order> orders;
    
    public Restaurant() {}
    public Restaurant(int id, String name, int ownerID) {
        this.id = id;
        this.name = name;
        this.ownerID = ownerID;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
         this.id=id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getOwnerID() {
        return ownerID;
    }
    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }
    public Set<Meal> getMeals() {
        return meals;
    }
    public void setMeals(Set<Meal> meals) {
        this.meals = meals;
    }
    public Set<Order> getOrders() {
        return orders;
    }
    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }
	

}
