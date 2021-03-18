package com.xlk.paperlesstl.model.data;

/**
 * @author Created by xlk on 2021/3/1.
 * @desc
 */
public class EventMessage {
    private int type;
    private int method;
    private Object[] objects;

    private EventMessage(Builder builder) {
        this.type = builder.type;
        this.method = builder.method;
        this.objects = builder.objects;
    }

    public int getType() {
        return type;
    }

    public int getMethod() {
        return method;
    }


    public Object[] getObjects() {
        return objects;
    }

    public static class Builder {
        private int type;
        private int method;
        private Object[] objects;

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder method(int method) {
            this.method = method;
            return this;
        }

        public Builder objects(Object... objects) {
            this.objects = objects;
            return this;
        }

        public EventMessage build() {
            return new EventMessage(this);
        }
    }
}
