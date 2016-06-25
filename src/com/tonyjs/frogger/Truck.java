package com.tonyjs.frogger;

/**
 * Created by tonysaavedra on 6/24/16.
 */
public class Truck {
    private Sprite truck;

    public Truck() {
        this.truck = setTruck();
    }

    private Sprite setTruck() {
        Sprite img = new Sprite();
        img.setImage("/images/truck.png");
        return img;
    }

    public Sprite getTruck() {
        return truck;
    }
}
