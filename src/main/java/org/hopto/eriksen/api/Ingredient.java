package org.hopto.eriksen.api;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "INGREDIENT")
public class Ingredient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// This must be IDENTITY for hsqldb?
	@Column(name = "INGREDIENT_ID")
	private Long id;

	@Column(name = "NAME", nullable = false)
	private String name;

	@ManyToMany(
			cascade = { CascadeType.PERSIST, CascadeType.MERGE },
			mappedBy = "ingredients",
			targetEntity = Part.class)
	private Set<Part> parts = new HashSet<>();

	// for JPA 
	public Ingredient() {

	}

	public Ingredient(String name) {
		this.name = name;
	}

	// Picture? 

	// private String description 

	// tags, e.g. colonial? 

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonIgnore
	public Set<Part> getCourses() {
		return parts;
	}

	public void setCourses(Set<Part> courses) {
		this.parts = courses;
	}

	@Override
	public String toString() {
		return "Ingredient [" + (id != null ? "id=" + id + ", " : "")
				+ (name != null ? "name=" + name + ", " : "") + "]";
	}

}
