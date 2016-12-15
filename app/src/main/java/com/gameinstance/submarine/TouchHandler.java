package com.gameinstance.submarine;

/**
 * Created by gringo on 15.12.2016 22:10.
 *
 */
public abstract class TouchHandler implements Comparable<TouchHandler> {
    int order;

    public TouchHandler(int order) {
        this.order = order;
    }

    abstract boolean touch(int x, int y);

    abstract boolean onDown(int x, int y);

    abstract boolean onUp(int x, int y);

    @Override
    public int compareTo(TouchHandler another) {
        return order - another.order;
    }
}
