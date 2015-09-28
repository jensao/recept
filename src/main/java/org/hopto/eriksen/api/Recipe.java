package org.hopto.eriksen.api;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * This class represents a recipe, it can contains many parts E.g. sauce, main dish, ...
 * 
 *
 */
@Entity
@Table(name = "RECIPE")
@XmlRootElement
public class Recipe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// This must be IDENTITY for hsqldb?
	//	@GeneratedValue(strategy=GenerationType.AUTO)	// This gives the error of type: user lacks privilege or object not found: HIBERNATE_SEQUENCE in statement
	@Column(name = "RECIPE_ID")
	private Long id;

	@Length(min = 3)
	@Column(name = "TITLE", nullable = false)
	private String title;

	@Column(name = "COMMENT", length = 400)
	private String comment;

	// private ? photo
	// private createdBy
	// private createAt
	// private lastModified
	// private lastCooked

	// One recipe can consist of several courses (rename this to parts?), e.g. caesar salad. One part for the dressing, one for the croutons, ...
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "recipe")
	private List<Part> parts = new ArrayList<>();

	// For JPA 
	public Recipe() {
	}

	public Recipe(String title) {
		this.title = title;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@JsonIgnore
	public List<Part> getParts() {
		return parts;
	}

	public void addCPart(Part part) {
		this.parts.add(part);
		if (part.getRecipe() != this) {
			// DB kommer välo inte uppdateras av denna tex?
			part.setRecipe(this);
		}
	}

	@Override
	public String toString() {
		return "Recipe [" + (id != null ? "id=" + id + ", " : "")
				+ (title != null ? "title=" + title + ", " : "")
				+ (comment != null ? "comment=" + comment + ", " : "")
				+ (parts != null ? "courses=" + parts : "") + "]";
	}

}
