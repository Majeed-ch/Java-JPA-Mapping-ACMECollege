/***************************************************************************
 * File:  MembershipCard.java Course materials (23W) CST 8277
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @date August 28, 2022
 * 
 */
package acmecollege.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.resource.beans.internal.FallbackBeanInstanceProducer;

@SuppressWarnings("unused")

/**
 * The persistent class for the membership_card database table.
 */
//TODO xMC01 - Add the missing annotations.
//TODO xMC02 - Do we need a mapped super class?  If so, which one?
@Entity
@Table(name = "membership_card")
@AttributeOverride(name = "id", column = @Column(name = "card_id"))
@NamedQuery(name = "MembershipCard.findAll", query = "SELECT mc FROM Student mc")
public class MembershipCard extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;

	// TODO xMC03 - Add annotations for 1:1 mapping.  Changes here should cascade.
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "membership_id", referencedColumnName = "membership_id", nullable = false)
	private ClubMembership clubMembership;

	// TODO xMC04 - Add annotations for M:1 mapping.  Changes here should not cascade.
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
	private Student owner;

	// TODO xMC05 - Add annotations.
	@Column(name = "signed", nullable = false, length = 1)
	private byte signed;

	public MembershipCard() {
		super();
	}
	
	public MembershipCard(ClubMembership clubMembership, Student owner, byte signed) {
		this();
		this.clubMembership = clubMembership;
		this.owner = owner;
		this.signed = signed;
	}

	public ClubMembership getClubMembership() {
		return clubMembership;
	}

	public void setClubMembership(ClubMembership clubMembership) {
		this.clubMembership = clubMembership;
		//We must manually set the 'other' side of the relationship (JPA does not 'do' auto-management of relationships)
		if (clubMembership != null) {
			clubMembership.setCard(this);
		}
	}

	public Student getOwner() {
		return owner;
	}

	public void setOwner(Student owner) {
		this.owner = owner;
		//We must manually set the 'other' side of the relationship (JPA does not 'do' auto-management of relationships)
		if (owner != null) {
			owner.getMembershipCards().add(this);
		}
	}

	public byte getSigned() {
		return signed;
	}

	public void setSigned(byte signed) {
		this.signed = signed;
	}

	public void setSigned(boolean signed) {
		this.signed = (byte) (signed ? 0b0001 : 0b0000);
	}
	
	//Inherited hashCode/equals is sufficient for this entity class

}