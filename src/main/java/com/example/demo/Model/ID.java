//package com.example.demo.Model;
//
//import jakarta.persistence.Embeddable;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.io.Serializable;
//import java.util.Objects;
//
//@Setter
//@Getter
//@Embeddable
//public class ID implements Serializable {
//
//    private int id;
//
//    public ID() {}
//
//    public ID(int id) {
//        this.id = id;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        ID id1 = (ID) o;
//        return id == id1.id;
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id);
//    }
//}
