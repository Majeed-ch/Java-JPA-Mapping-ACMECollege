/***************************************************************************
 * File:  Student.java Course materials (23W) CST 8277
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @date August 28, 2022
 * 
 */
package acmecollege.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Column;



/**
 * The persistent class for the STUDENT database table in the acmecollege schema
 * </br></br>
 * 
 * Note:  This is <b>NOT</b> the same Student entity from Lab 1/Assignment 1/Assignment 2.
 * </br>
 * This entity does <b>NOT</b> have member fields email, phoneNumber, level, or program.
 * </br>
 * 
 */
@SuppressWarnings("unused")
//TODO xST01 - Add the missing annotations.
//TODO xST02 - Do we need a mapped super class? If so, which one?
@Entity
@Table(name = "student")
@AttributeOverride(name = "id", column = @Column(name="id"))
@NamedQuery(name = "Student.findAll", query = "SELECT s FROM Student s")
public class Student extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;

	// TODO xST03 - Add annotation
	@Basic(optional = false)
	@Column(name = "first_name", nullable = false, length = 50)
	private String firstName;

	// TODO xST04 - Add annotation
	@Basic(optional = false)
	@Column(name = "last_name", nullable = false, length = 50)
	private String lastName;

	// TODO xST05 - Add annotations for 1:M relation.  Changes should not cascade.
	@OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
	private Set<MembershipCard> membershipCards = new HashSet<>();

	// TODO xST06 - Add annotations for 1:M relation.  Changes should not cascade.
	@OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
	private Set<CourseRegistration> courseRegistrations = new HashSet<>();

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Set<MembershipCard> getMembershipCards() {
		return membershipCards;
	}

	public void setMembershipCards(Set<MembershipCard> membershipCards) {
		this.membershipCards = membershipCards;
	}

	public Set<CourseRegistration> getCourseRegistrations() {
		return courseRegistrations;
	}

	public void setCourseRegistrations(Set<CourseRegistration> courseRegistrations) {
		this.courseRegistrations = courseRegistrations;
	}

	public void setFullName(String firstName, String lastName) {
		setFirstName(firstName);
		setLastName(lastName);
	}
	
	//Inherited hashCode/equals is sufficient for this entity class

}
