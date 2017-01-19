/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wolf.assignment1;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * A quote with unique id, rank, price and volume
 * @author Andrey
 */
public class Quote {

    public final String id;
    
    public final int rank;

    public BigDecimal price;

    public  int volume;

    public Quote(String id, int rank, BigDecimal price, int volume) {
        this.id = id;
        this.rank = rank;
        this.price = price;
        this.volume = volume;
    }

    public String getId() {
        return id;
    }

    public int getRank() {
        return rank;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getVolume() {
        return volume;
    }
    
    public void updatePrice(BigDecimal price) {
        this.price = price;
    }
    
    public void updateVolume(int volume) {
        this.volume = volume;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final Quote other = (Quote) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return id + "/" + price + "/" + volume;
    }

    
}
