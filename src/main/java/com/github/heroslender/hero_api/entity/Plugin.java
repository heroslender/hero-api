package com.github.heroslender.hero_api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Plugin {

    private @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    private String name;

    public Plugin() {
    }

    public Plugin(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Plugin plugin = (Plugin) o;
        return Objects.equals(getId(), plugin.getId()) && Objects.equals(getName(), plugin.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    @Override
    public String toString() {
        return "Plugin{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
